package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.dto.TableDTO;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * A service that handles the game logic.
 */
public interface GameService<T extends Game> {
    /**
     * @param game The game for which to start the loop.
     */
    void startGameLoop(T game);

    /**
     * Returns players in descending order based on their performance in the game.
     */
    @Transactional(readOnly = true)
    List<Player> getTopPlayers(T game);

    /**
     * Adds points to the scoreboard based on the player's performance in the game.
     *
     * @param game the game to calculate points for
     * @param maxPoints The points to be given to players with excellent performance
     * @param firstPlaceBonus The bonus points to be given to the player with the best performance
     * @param secondPlaceBonus The bonus points to be given to the player with the second-best performance
     * @param thirdPlaceBonus The bonus points to be given to the player with the third-best performance
     */
    @Transactional
    void distributePoints(T game, int maxPoints, int firstPlaceBonus, int secondPlaceBonus,
                          int thirdPlaceBonus);

    /**
     * Adds points to the player's score in the scoreboard
     *
     * @param player player to add points to
     * @param points points to add
     * @param scoreboardRepository scoreboard repository
     * @return the updated scoreboard
     */
    @Transactional
    default Scoreboard addScoreboardPoints(Player player, int points, ScoreboardRepository scoreboardRepository) {
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

    /**
     * Returns a sorted table of players in desc order based on their performance in the game along with their stats.
     * @param theGame the game to get the stats for
     */
    TableDTO getStatistics(Game theGame);



}
