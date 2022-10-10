package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.dto.GameStartDTO;
import com.dkanepe.reaktly.exceptions.*;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import lombok.extern.slf4j.Slf4j;
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



    public GameplayService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                           PlayerService playerService, RoomRepository roomRepository,
                           PerfectClickerService perfectClickerService, @Lazy GameplayService self,
                           CommunicationService messaging, RoomService roomService) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.roomRepository = roomRepository;
        this.perfectClickerService = perfectClickerService;
        this.self = self;
        this.messaging = messaging;
        this.roomService = roomService;
    }

    public void startGame(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers
            , NotEnoughGames, GameAlreadyStarted, NotAHost {
        Player player = self.validateGameStart(headerAccessor);
        self.prepareRoomForFirstGame(player.getRoom());
        roomService.updateRoomStatus(player.getRoom(), Room.Status.ABOUT_TO_START);

        // The game is about to start, so we need to update room & send a message to the players
        Room room = player.getRoom();
        room.setStartTime(System.currentTimeMillis() + 5000); // get from config
        roomRepository.save(room);
        GameStartDTO gameStartDTO = mapper.roomToGameStartedDTO(player.getRoom());
        messaging.sendToGame(GameplayActions.GAME_STARTED, player.getRoom().getID(), gameStartDTO);

        // sleep until gameStartedDTO.getStartTime()
        long sleepTime = gameStartDTO.getStartTime() - System.currentTimeMillis();

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //roomService.updateRoomStatus(player.getRoom(), Room.Status.IN_PROGRESS);

        // Start the first, specific game
        Game.GameType gameType = player.getRoom().getCurrentGame().getType();
        switch (gameType) {
            case PERFECT_CLICKER:
                perfectClickerService.startGame(player);
                break;
        }
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
