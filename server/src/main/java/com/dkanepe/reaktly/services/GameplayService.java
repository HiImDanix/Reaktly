package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.actions.RoomActions;
import com.dkanepe.reaktly.dto.GameEndDTO;
import com.dkanepe.reaktly.dto.GameStartDTO;
import com.dkanepe.reaktly.dto.TableDTO;
import com.dkanepe.reaktly.exceptions.*;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.GameRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.games.GameService;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Receive a request to start playing.
     * @param headerAccessor The websocket message header accessor.
     * @throws InvalidSession If the session is invalid.
     * @throws NotEnoughPlayers If there are not enough players in the room.
     * @throws NotEnoughGames If not enough games have been added to the room for playing.
     * @throws GameAlreadyStarted If the room is not waiting for the game to start.
     * @throws NotAHost If the player is not the host.
     */
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

        // While there is a game to play, play it.
        while (self.getGamesLeft(room) > 0) {
            // Take the next game from the list and make it the current game.
            self.setNextGameAsCurrent(room);
            // Show current game's instructions to the players & give them time to read them.
            long gameStartTime = self.gameInstructions(room);
            sleepTime = gameStartTime - System.currentTimeMillis();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Get the specific current game's service
            GameService gameService = self.getGameService(room);

            // Start the game & inform the players.
            Thread gameLoopThread = startCurrentGame(room, gameService);

            // Wait for the game to finish.
            gameLoopThread.interrupt();
            self.waitForGameToFinish(room); // using transaction, because no hibernate session so currGame cannot be accessed.

            // Game has finished. Inform the players.
            gameLoopThread.interrupt();
            self.gameFinished(room, gameService);

            // Wait for players to look at the scoreboard & statistics.
            try {
                Thread.sleep(self.getCurrentGameFinishTime(room) - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional(readOnly = true)
    public int getGamesLeft(Room room) {
        room = entityManager.find(Room.class, room.getID());
        return room.getGames().size();
    }

    @Transactional(readOnly = true)
    public long getCurrentGameFinishTime(Room room) {
        room = entityManager.find(Room.class, room.getID());
        return room.getCurrentGame().getFinishTime();
    }

    @Transactional(readOnly = true)
    public GameService getGameService(Room room) {
        room = entityManager.find(Room.class, room.getID());
        GameService gameService = null;
        Game.GameType gameType = room.getCurrentGame().getType();
        switch (gameType) {
            case PERFECT_CLICKER -> gameService = perfectClickerService;
            default -> log.error("Game type not supported: {}", gameType);
        }
        return gameService;
    }

    /**
     * Wait until the game has finished.
     */
    @Transactional
    public void waitForGameToFinish(Room room) {
        room = entityManager.find(Room.class, room.getID());
        try {
            Thread.sleep(room.getCurrentGame().getEndTime() - System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Give the room (lobby) time to prepare for the game to start.
     * @param room The room to prepare.
     * @param startTime The time at which the game will start.
     */
    private void prepareLobbyForStart(Room room, long startTime) {
        room = entityManager.find(Room.class, room.getID());
        roomService.updateRoomStatus(room, Room.Status.ABOUT_TO_START);
        room.setStartTime(startTime); // TODO: get from config
        roomRepository.save(room);
        GameStartDTO gameStartDTO = mapper.roomToGameStartedDTO(room);
        messaging.sendToRoom(RoomActions.PREPARE_FOR_START, room.getID(), gameStartDTO);
    }

    /**
     * Set the next game in the room's game list as the current game. Does not inform the players.
     * @param room The room to set the next game for.
     */
    @Transactional
    public void setNextGameAsCurrent(Room room) {
        room = entityManager.find(Room.class, room.getID());
        Game game = room.getGames().iterator().next();
        room.setCurrentGame(game);
        room.getGames().remove(game);
        roomRepository.save(room);
    }

    /**
     * Show the current game's instructions to the players & give them time to read them.
     * It informs the players along with the current game's properties e.g. start time, end time, etc.
     * @param room The room to show the instructions for the current game to.
     * @return The time at which the instructions will end and the game will start.
     */
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

    /**
     * Start the current game in the room & inform (ping) the players that the game has started.
     * @param room The room for which to start the current game.
     * @return The thread that is running the game loop.
     */
    private Thread startCurrentGame(Room room, GameService gameService) {
        room = entityManager.find(Room.class, room.getID());
        Game game = room.getCurrentGame();
        game.setStatus(Game.GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        messaging.sendToGame(GameplayActions.GAME_START_PING, room.getID(), "");

        // Start the specific game's game-loop in a new thread.
        Game.GameType gameType = room.getCurrentGame().getType();

        Thread gameLoop = new Thread(() -> {
            switch (gameType) {
                case PERFECT_CLICKER -> gameService.startGameLoop(game);
                default -> log.error("Game type not supported: {}", gameType);
            }
        });
        gameLoop.start();
        return gameLoop;
    }

    /**
     * Validate the 'start game' request.
     * @param room The room to validate the request for.
     * @param player The player who sent the request.
     * @throws NotEnoughPlayers If there are not enough players in the room.
     * @throws NotEnoughGames If the room does not have any games to play added to it.
     * @throws GameAlreadyStarted If the room's status is not LOBBY.
     * @throws NotAHost If the player who sent the request is not the host of the room.
     */
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

    /**
     * Finish the game, distribute points, get the results (statistics, scoreboard) & inform the players.
     * @param room The room to inform the players of.
     */
    @Transactional
    public void gameFinished(Room room, GameService gameService) {
        room = entityManager.find(Room.class, room.getID());

        // Mark the game as finished.
        Game game = room.getCurrentGame();
        game.setStatus(Game.GameStatus.FINISHED);
        gameRepository.save(game);

        // Distribute points.
        gameService.distributePoints(game, 100, 100, 50, 25);

        // Get the game's statistics DTO & room's scoreboard DTO.
        TableDTO statisticsDTO = gameService.getStatistics(game);
        TableDTO scoreboard = mapper.scoreboardToTableDTO(room.getScoreboard());

        // Send the game's statistics & overall scoreboard to the players.
        boolean isLastGame = room.getGames().size() == 0;
        GameEndDTO gameEndDTO = new GameEndDTO(scoreboard, statisticsDTO, isLastGame);
        messaging.sendToGame(GameplayActions.GAME_END, room.getID(), gameEndDTO);
    }
}
