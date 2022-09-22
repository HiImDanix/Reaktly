package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

// add sl4j logger
@Slf4j
@Service
public class GameplayService {
    private final ScoreboardRepository scoreboardRepository;

    public GameplayService(ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    private Scoreboard addScoreboardPoints(Player player, int points) {
        log.debug("Adding {} points to player {}", points, player);

        Scoreboard scoreboard = player.getGame().getScoreboard();
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
        scoreboardLine.setScore(scoreboardLine.getScore() + 1);

        // Save scoreboard to DB & return saved object
        return scoreboardRepository.save(scoreboard);
    }

    public Scoreboard click(Player player) {
        return addScoreboardPoints(player, 1);
    }

}
