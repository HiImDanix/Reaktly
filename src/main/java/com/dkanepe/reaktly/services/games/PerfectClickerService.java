package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.dto.PerfectClicker.ClickDTO;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerDTO;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerGameStateDTO;
import com.dkanepe.reaktly.exceptions.GameFinished;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.exceptions.RoomNotInProgress;
import com.dkanepe.reaktly.exceptions.WrongGame;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.GameStatePerfectClicker;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.GameRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.CommunicationService;
import com.dkanepe.reaktly.services.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PerfectClickerService {

    private final static String END_GAME = "/topic/game/end";

    private final ScoreboardRepository scoreboardRepository;
    private final MapStructMapper mapper;
    private final PlayerService playerService;
    private final GameRepository gameRepository;
    private final PerfectClickerService self;

    private CommunicationService messaging;
    private final RoomRepository roomRepository;
    private final EntityManager entityManager;

    public PerfectClickerService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                                 PlayerService playerService, GameRepository gameRepository,
                                 CommunicationService messaging, @Lazy PerfectClickerService self,
                                 RoomRepository roomRepository, EntityManager entityManager) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.gameRepository = gameRepository;
        this.messaging = messaging;
        this.self = self;
        this.roomRepository = roomRepository;
        this.entityManager = entityManager;
    }

    public Scoreboard addScoreboardPoints(Player player, int points) {
        log.debug("Adding {} points to player {}", points, player);

        Scoreboard scoreboard = player.getRoom().getScoreboard();
        Set<ScoreboardLine> scores = scoreboard.getScores();

        // find the player's scoreboard-line or create a new line in the scoreboard
        ScoreboardLine scoreboardLine = scores.stream()
                .filter(score -> score.getPlayer().equals(player))
                .findFirst()
                .orElseGet(() -> {
                    ScoreboardLine newScoreboardLine = new ScoreboardLine(player);
                    scores.add(newScoreboardLine);
                    return newScoreboardLine;
                });

        // add the points
        scoreboardLine.setScore(scoreboardLine.getScore() + points);

        // Save scoreboard to DB & return saved object
        return scoreboardRepository.save(scoreboard);
    }

    @Transactional
    public void click(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, RoomNotInProgress, WrongGame, GameFinished {
        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        Room room = player.getRoom();

        // validation
        if (room.getStatus() != Room.Status.IN_PROGRESS) {
            throw new RoomNotInProgress("Game is not in progress");
        }
        if (room.getCurrentGame().getType() != Game.GameType.PERFECT_CLICKER) {
            throw new WrongGame("Game is not Perfect Clicker");
        }
        if (room.getCurrentGame().isFinished()) {
            throw new GameFinished("This particular game is already finished");
        }

        PerfectClicker game = (PerfectClicker) player.getRoom().getCurrentGame();

        // add 1 click to the player's state
        GameStatePerfectClicker state = game.getState().stream()
                .filter(s -> s.getPlayer().equals(player))
                .findFirst()
                .orElseGet(() -> {
                    GameStatePerfectClicker newState = new GameStatePerfectClicker(player);
                    game.getState().add(newState);
                    return newState;
                });
        state.setClicks(state.getClicks() + 1);
        state.setLastClick(LocalDateTime.now());
        gameRepository.save(game);

        // inform players of the click
        ClickDTO dto = new ClickDTO(state.getPlayer(), state.getLastClick());
        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_CLICK, room.getID(), dto);
    }

    public void startGame(Player player) {
        // TODO: Refactor the whole mess
        PerfectClickerDTO dto = mapper.perfectClickerToPerfectClickerDTO((PerfectClicker) player.getRoom().getCurrentGame());

        // Set the game's status to instructions screen
        PerfectClicker game = (PerfectClicker) player.getRoom().getCurrentGame();
        game.setStatus(Game.GameStatus.INSTRUCTIONS);
        gameRepository.save(game);

        // Inform users of game start & end
        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_GAME_START, player.getRoom().getID(), dto);

        // Sleep until game starts
        try {
            Thread.sleep(LocalDateTime.now().until(dto.getStartTime(), ChronoUnit.MILLIS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set game status to in-progress. No need to inform user.
        player.getRoom().getCurrentGame().setStatus(Game.GameStatus.IN_PROGRESS);
        gameRepository.save(game);

        try {
            Thread.sleep(LocalDateTime.now().until(dto.getEndTime(), ChronoUnit.MILLIS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Game has ended
        endCurrentGame(player.getRoom());
        Room room = roomRepository.findById(player.getRoom().getID()).get();

        // print game state
        self.printGameState(room);

        // Inform users of game end
//        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_GAME_END, room.getID(),
//                mapper.perfectClickerToPerfectClickerGameStateDTO((PerfectClicker) room.getCurrentGame()));

        // All games have ended
        // TODO: call when really all games have ended
        messaging.sendToGame(GameplayActions.END_GAME, player.getRoom().getID(), room.getScoreboard());
    }

    @Transactional
    public void printGameState(Room room) {
        room = roomRepository.findById(room.getID()).get();
        PerfectClicker game = (PerfectClicker) room.getCurrentGame();
        List<PerfectClickerGameStateDTO> gameState = mapper.perfectClickerGameStateToDTO(game.getState());
        System.out.println("Game state:" + gameState);
        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_GAME_END, room.getID(),
                gameState);
    }


    public void endCurrentGame(Room room) {
        // TODO: find a better way. Not using the below line results in errors
        room = roomRepository.findById(room.getID()).get();
        Game game = room.getCurrentGame();
        game.setFinished(true);
        gameRepository.save(game);
        System.out.println("Game finished");
        self.distributePoints(room);

    }

    @Transactional
    public void distributePoints(Room room) {
        // TODO: find a better way. Not using the below line results in a LazyInitializationException
        room = entityManager.merge(room);
        PerfectClicker game = (PerfectClicker) room.getCurrentGame();
        // Distribute points to players:
        // Players that clicked over the target get 0 points.
        // Remaining players get 100 points * (their clicks performed / target clicks)
        // First player gets 100 points extra. Second player gets 50 points extra. Third player gets 25 points extra.
        // if players have the same amount of clicks, winner is the one who's last click was earlier
        List<GameStatePerfectClicker> state = game.getState().stream()
                .filter(s -> s.getClicks() <= game.getTargetClicks())
                .sorted((s1, s2) -> {
                    if (s1.getClicks() == s2.getClicks()) {
                        return s1.getLastClick().compareTo(s2.getLastClick());
                    }
                    return s2.getClicks() - s1.getClicks();
                })
                .collect(Collectors.toList());
        int maxPoints = 100;
        for (int i = 0; i < state.size(); i++) {
            GameStatePerfectClicker s = state.get(i);
            // Calculate points
            int points = (int) (maxPoints * (s.getClicks() / (double) game.getTargetClicks()));
            // add bonus points
            if (i == 0) {
                points += 100;
            } else if (i == 1) {
                points += 50;
            } else if (i == 2) {
                points += 25;
            }
            addScoreboardPoints(s.getPlayer(), points);
        }
    }

}
