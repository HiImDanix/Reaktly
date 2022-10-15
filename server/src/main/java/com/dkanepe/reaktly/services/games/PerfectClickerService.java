package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.dto.PerfectClicker.ClickDTO;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PerfectClickerService {

    private final ScoreboardRepository scoreboardRepository;
    private final MapStructMapper mapper;
    private final PlayerService playerService;
    private final GameRepository gameRepository;
    private final PerfectClickerService self;

    private final CommunicationService messaging;
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

    /**
     * @param game The game for which to start the loop.
     */
    public void startGameLoop(Game game) {
    }

    /**
     * Receives a click from a player and adds it to the game state.
     * @param headerAccessor The header accessor for the websocket message.
     * @throws InvalidSession If the session is invalid.
     * @throws RoomNotInProgress If the room is not in progress.
     * @throws WrongGame If the current game is not this game.
     * @throws GameFinished If the game is not currently in progress.
     */
    @Transactional
    public void click(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, RoomNotInProgress, WrongGame, GameFinished {
        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        Room room = player.getRoom();

        // validation
        if (room.getStatus() != Room.Status.IN_PROGRESS) {
            throw new RoomNotInProgress("Game is not in progress");
        }
        if (room.getCurrentGame().getType() != Game.GameType.PERFECT_CLICKER) {
            throw new WrongGame("Current game is not Perfect Clicker");
        }
        if (room.getCurrentGame().getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new GameFinished("This particular game is not in progress");
        }

        PerfectClicker game = (PerfectClicker) player.getRoom().getCurrentGame();

        // add 1 click for the player
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
        ClickDTO dto = new ClickDTO(state.getPlayer(),state.getClicks(), state.getLastClick());
        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_CLICKS, room.getID(), dto);
    }

    /**
     * Adds points to the player's score in the scoreboard
     * @param player player to add points to
     * @param points points to add
     * @return the updated scoreboard
     */
    @Transactional
    public Scoreboard addScoreboardPoints(Player player, int points) {
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
     * Returns players in descending order based on their performance in the game.
     */
    @Transactional(readOnly = true)
    public List<Player> getTopPlayers(Game game) {
        // Those with the most clicks will be first.
        // Those with same clicks will be sorted by time of last click (earlier is better)
        return ((PerfectClicker) game)
                .getState().stream()
                .sorted((s1, s2) -> {
                    if (s1.getClicks() == s2.getClicks()) {
                        return s1.getLastClick().compareTo(s2.getLastClick());
                    }
                    return s2.getClicks() - s1.getClicks();
                })
                .map(GameStatePerfectClicker::getPlayer)
                .collect(Collectors.toList());


    }


    /**
     * Adds points to the scoreboard based on the player's performance in the game.
     *
     * @param theGame the game to calculate points for
     * @param maxPoints The points to be given to players with excellent performance
     * @param firstPlaceBonus The bonus points to be given to the player with the best performance
     * @param secondPlaceBonus The bonus points to be given to the player with the second-best performance
     * @param thirdPlaceBonus The bonus points to be given to the player with the third-best performance
     */
    @Transactional
    public void distributePoints(Game theGame, int maxPoints, int firstPlaceBonus, int secondPlaceBonus,
                                 int thirdPlaceBonus) {
        // Distribute points to players:
        // Players that clicked over the target get 0 points.
        // Remaining players get 100 points * (their clicks performed / target clicks)
        // First player gets 100 points extra. Second player gets 50 points extra. Third player gets 25 points extra.
        // if players have the same amount of clicks, winner is the one who's last click was earlier
        PerfectClicker game = (PerfectClicker) theGame;
        List<Player> playersRankedDesc = self.getTopPlayers(game);

        for (Player player: playersRankedDesc) {
            Optional<GameStatePerfectClicker> state = game.getState().stream()
                    .filter(s -> s.getPlayer().equals(player))
                    .findFirst();
            int clicks = state.map(GameStatePerfectClicker::getClicks).orElse(0);
            int points = 0;
            // Add points based on clicks performed. If clicks performed is over the target, no points are added.
            if (clicks <= game.getTargetClicks()) {
                points = (int) Math.round(maxPoints * (clicks / (double) game.getTargetClicks()));
            }
            // Add bonus points
            if (playersRankedDesc.indexOf(player) == 0) {
                points += firstPlaceBonus;
            } else if (playersRankedDesc.indexOf(player) == 1) {
                points += secondPlaceBonus;
            } else if (playersRankedDesc.indexOf(player) == 2) {
                points += thirdPlaceBonus;
            }
            self.addScoreboardPoints(player, points);
        }
    }

}
