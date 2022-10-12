package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.actions.RoomActions;
import com.dkanepe.reaktly.dto.GameStartDTO;
import com.dkanepe.reaktly.exceptions.*;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.GameRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// add sl4j logger
@Slf4j
@Service
public class GameplayService {
    private final ScoreboardRepository scoreboardRepository;
    private final MapStructMapper mapper;
    private final PlayerService playerService;
    private final RoomRepository roomRepository;
    private final PerfectClickerService perfectClickerService;
    private final GameplayService self;
    private final CommunicationService messaging;
    private final RoomService roomService;
    private final GameRepository gameRepository;



    public GameplayService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                           PlayerService playerService, RoomRepository roomRepository,
                           PerfectClickerService perfectClickerService, @Lazy GameplayService self,
                           CommunicationService messaging, RoomService roomService, GameRepository gameRepository) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.roomRepository = roomRepository;
        this.perfectClickerService = perfectClickerService;
        this.self = self;
        this.messaging = messaging;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
    }

    public void startGame(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers
            , NotEnoughGames, GameAlreadyStarted, NotAHost {
        // TODO: Refactor. Split into smaller methods. Execute after set time in separate threads.
        // Because, if there is an exception, the game will halt.
        Player player = self.validateGameStart(headerAccessor);
        self.prepareRoomForFirstGame(player.getRoom());
        roomService.updateRoomStatus(player.getRoom(), Room.Status.ABOUT_TO_START);

        // The game is about to start, so we need to update room & send a message to the players
        Room room = player.getRoom();
        room.setStartTime(System.currentTimeMillis() + 5000); // TODO: get from config
        roomRepository.save(room);
        GameStartDTO gameStartDTO = mapper.roomToGameStartedDTO(player.getRoom());
        messaging.sendToRoom(RoomActions.PREPARE_FOR_START, player.getRoom().getID(), gameStartDTO);

        // sleep until lobby preparation time has passed.
        long sleepTime = gameStartDTO.getStartTime() - System.currentTimeMillis();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // game in progress
        self.gameInProgress(room);


//        // Start the first, specific game
//        Game.GameType gameType = player.getRoom().getCurrentGame().getType();
//        switch (gameType) {
//            case PERFECT_CLICKER:
//                perfectClickerService.startGame(player);
//                break;
//        }
    }

    @Transactional
    public void gameInProgress(Room room) {
        roomService.updateRoomStatus(room, Room.Status.IN_PROGRESS);
        Game game = room.getCurrentGame();
        game.setStartTime(System.currentTimeMillis() + 5000);
        game.setStatus(Game.GameStatus.INSTRUCTIONS);
        gameRepository.save(game);
        // print time now to game start time
        messaging.sendToGame(GameplayActions.GAME_START, room.getID(), mapper.gameToGameDTO((PerfectClicker) game));
    }

    @Transactional // Using transactional because hibernate session is closed for some reason.
    public Player validateGameStart(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers,
            NotEnoughGames, GameAlreadyStarted, NotAHost {
        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        Room room = player.getRoom();

        // not a host
        if (!room.getHost().equals(player)) {
            throw new NotAHost("Only the host can start the game");
        }

        if (room.getStatus() != Room.Status.LOBBY) {
            throw new GameAlreadyStarted("Cannot start the game because it already in progress or finished");
        }
        if (room.getPlayers().size() < 1) {
            throw new NotEnoughPlayers("Not enough players to start the game!");
        }
        if (room.getGames().size() < 1) {
            throw new NotEnoughGames("You can't start playing without adding any mini-games!");
        }
        return player;
    }

    /*
        set status of room to IN_PROGRESS and set currentGame to the first game in the list
     */
    @Transactional // Using transactional because hibernate session is closed for some reason.
    public void prepareRoomForFirstGame(Room room) {
        Game game = room.getGames().iterator().next();
        room.setCurrentGame(game);
        room.getGames().remove(game);
    }


}
