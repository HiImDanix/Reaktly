package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.Clicks;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.GameRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class PerfectClickerService {

    private final static String END_GAME = "/topic/game/end";

    private final ScoreboardRepository scoreboardRepository;
    private final MapStructMapper mapper;
    private final PlayerService playerService;
    private final GameRepository gameRepository;
    private final PerfectClickerService self;

    private SimpMessagingTemplate template;
    private final RoomRepository roomRepository;
    private final EntityManager entityManager;

    public PerfectClickerService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                                 PlayerService playerService, GameRepository gameRepository,
                                 SimpMessagingTemplate template, @Lazy PerfectClickerService self,
                                 RoomRepository roomRepository, EntityManager entityManager) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.gameRepository = gameRepository;
        this.template = template;
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
    public Scoreboard click(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        if (player.getRoom().getStatus() != Room.Status.PLAYING) {
            throw new InvalidSession("Game is not in progress");
        }
        if (player.getRoom().getCurrentGame().getType() != Game.GameType.PERFECT_CLICKER) {
            throw new InvalidSession("Game is not Perfect Clicker");
        }
        if (player.getRoom().getCurrentGame().isFinished()) {
            throw new InvalidSession("This particular game is already finished");
        }

        PerfectClicker game = (PerfectClicker) player.getRoom().getCurrentGame();

        int clicks = game.getClicks().getOrDefault(player, 0);
        game.getClicks().put(player, clicks + 1);
        gameRepository.save(game);

        return addScoreboardPoints(player, 1);
    }

    public void startGame(Player player) {
        // execute endGame after 3 seconds
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        endCurrentGame(player.getRoom());
    }


    public void endCurrentGame(Room room) {
        room = roomRepository.findById(room.getId()).get();
        // print if method is transactional
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
         //for every click, add 2 points to the player
        game.getClicks().forEach((player, clicks) -> {
            addScoreboardPoints(player, clicks * 2);
        });

        template.convertAndSend(END_GAME, room.getScoreboard());
    }

}
