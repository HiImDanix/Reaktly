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

import javax.persistence.EntityManager;

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

    private final EntityManager entityManager;

    public GameplayService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                           PlayerService playerService, RoomRepository roomRepository,
                           PerfectClickerService perfectClickerService, @Lazy GameplayService self,
                           CommunicationService messaging, RoomService roomService, GameRepository gameRepository,
                           EntityManager entityManager) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.roomRepository = roomRepository;
        this.perfectClickerService = perfectClickerService;
        this.self = self;
        this.messaging = messaging;
        this.roomService = roomService;
        this.gameRepository = gameRepository;
        this.entityManager = entityManager;
    }

    public void startGame(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers
            , NotEnoughGames, GameAlreadyStarted, NotAHost {

        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        Room room = player.getRoom();

        // Validate the 'start game' request
        self.validateGameStart(room, player);

        // Give the lobby 5 seconds to prepare for the games to start.
        long prepFinishTime = System.currentTimeMillis() + 5000;
        prepareLobbyForStart(room, prepFinishTime);
        // sleep until lobby preparation time has passed.
        long sleepTime = prepFinishTime - System.currentTimeMillis();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Take the next game from the list and make it the current game.
        self.setNextGameAsCurrent(room);
        // Show current game's instructions to the players & give them time to read them.
        long instructionsEndTime = self.gameInstructions(room);
        sleepTime = instructionsEndTime - System.currentTimeMillis();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start the game & inform the players.
        startCurrentGame(room);
    }

    private void prepareLobbyForStart(Room room, long startTime) {
        room = entityManager.find(Room.class, room.getID());
        roomService.updateRoomStatus(room, Room.Status.ABOUT_TO_START);
        room.setStartTime(startTime); // TODO: get from config
        roomRepository.save(room);
        GameStartDTO gameStartDTO = mapper.roomToGameStartedDTO(room);
        messaging.sendToRoom(RoomActions.PREPARE_FOR_START, room.getID(), gameStartDTO);
    }

    @Transactional
    public long gameInstructions(Room room) {
        room = entityManager.find(Room.class, room.getID());
        roomService.updateRoomStatus(room, Room.Status.IN_PROGRESS);
        Game game = room.getCurrentGame();
        game.setStartTime(System.currentTimeMillis() + 5000);
        game.setStatus(Game.GameStatus.INSTRUCTIONS);
        gameRepository.save(game);
        messaging.sendToGame(GameplayActions.GAME_START_INFO, room.getID(), mapper.gameToGameDTO((PerfectClicker) game));
        return game.getStartTime();
    }

    private void startCurrentGame(Room room) {
        room = entityManager.find(Room.class, room.getID());
        Game game = room.getCurrentGame();
        game.setStatus(Game.GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        messaging.sendToGame(GameplayActions.GAME_START_PING, room.getID(), "");

        // Start the specific game's game-loop in a new thread.
        Game.GameType gameType = room.getCurrentGame().getType();
        Room finalRoom = room;
        new Thread(() -> {
            switch (gameType) {
                case PERFECT_CLICKER:
                    perfectClickerService.startGameLoop(finalRoom);
                    break;
            }
        }).start();
    }

    @Transactional(readOnly = true)
    public void validateGameStart(Room room, Player player) throws NotEnoughPlayers,
            NotEnoughGames, GameAlreadyStarted, NotAHost {
        room = entityManager.find(Room.class, room.getID());

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
    }

    @Transactional
    public void setNextGameAsCurrent(Room room) {
        room = entityManager.find(Room.class, room.getID());
        Game game = room.getGames().iterator().next();
        room.setCurrentGame(game);
        room.getGames().remove(game);
        roomRepository.save(room);
    }


}
