package com.dkanepe.reaktly.services.games;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.dto.PerfectClicker.ClickDTO;
import com.dkanepe.reaktly.dto.TableDTO;
import com.dkanepe.reaktly.exceptions.GameFinished;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.exceptions.RoomNotInProgress;
import com.dkanepe.reaktly.exceptions.WrongGame;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PerfectClickerService implements GameService {

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
     * {@inheritDoc}
     */
    @Override
    public void startGameLoop(Game theGame) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Player> getTopPlayers(Game theGame) {
        PerfectClicker game = (PerfectClicker) theGame;

        // Those with the most clicks will be first.
        // Those with same clicks will be sorted by time of last click (earlier is better)
        // Those who have clicked more than the target be ranked last.
        return game
                .getState().stream()
                .sorted((s1, s2) -> {
                    if (s1.getClicks() == s2.getClicks()) {
                        return Long.compare(s1.getLastClick(), s2.getLastClick());
                    }
                    return s2.getClicks() - s1.getClicks();
                })
                .sorted((s1, s2) -> {
                    if (s1.getClicks() > game.getTargetClicks() && s2.getClicks() > game.getTargetClicks()) {
                        return 0;
                    }
                    if (s1.getClicks() > game.getTargetClicks()) {
                        return 1;
                    }

                    if (s2.getClicks() > game.getTargetClicks()) {
                        return -1;
                    }
                    return 0;
                })
                .map(GameStatePerfectClicker::getPlayer)
                .collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void distributePoints(Game theGame, int maxPoints, int firstPlaceBonus, int secondPlaceBonus,
                                 int thirdPlaceBonus) {
        PerfectClicker game = (PerfectClicker) theGame;
        // Get the players ranked by their performance in the game.
        List<Player> playersRankedDesc = self.getTopPlayers(game);

        for (Player player : playersRankedDesc) {
            // Find the player's game state.
            Optional<GameStatePerfectClicker> state = game.getState().stream()
                    .filter(s -> s.getPlayer().equals(player))
                    .findFirst();
            int clicks = state.map(GameStatePerfectClicker::getClicks).orElse(0);
            int points = 0;
            // Add points based on clicks performed. If clicks performed > target, no points are added.
            if (clicks <= game.getTargetClicks()) {
                points = (int) Math.round(maxPoints * (clicks / (double) game.getTargetClicks()));
            }
            // Add bonus points based on rank.
            if (playersRankedDesc.indexOf(player) == 0) {
                points += firstPlaceBonus;
            } else if (playersRankedDesc.indexOf(player) == 1) {
                points += secondPlaceBonus;
            } else if (playersRankedDesc.indexOf(player) == 2) {
                points += thirdPlaceBonus;
            }
            // Add the points to the player's score in the scoreboard.
            self.addScoreboardPoints(player, points, scoreboardRepository);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TableDTO getStatistics(Game theGame) {
        // TODO: Find a better data structure than this. Should include Player (obj) as 1st, and clicks as 2nd.
        //  First column should be required and standardized.
        PerfectClicker game = (PerfectClicker) theGame;
        // Table headers
        String[] headers = new String[]{"Player", "Clicks", "Clicks/Sec"};
        // Get the players ranked by their performance in the game.
        List<Player> topPlayers = self.getTopPlayers(game);
        String[][] rows = new String[topPlayers.size()][headers.length];
        for (int i = 0; i < topPlayers.size(); i++) {
            Player player = topPlayers.get(i);
            // Find the player's game state.
            Optional<GameStatePerfectClicker> state = game.getState().stream()
                    .filter(s -> s.getPlayer().equals(player))
                    .findFirst();
            int clicks = state.map(GameStatePerfectClicker::getClicks).orElse(0);
            // First column: Player name
            rows[i][0] = player.getName();
            // Second column: Clicks
            rows[i][1] = clicks > game.getTargetClicks() ? "Too many" : String.valueOf(clicks);
            // Third column: Clicks per second
            long gameStartTime = game.getStartTime();
            long lastClickTime = state.get().getLastClick();
            double clicksPerSecond = clicks > 0 ? clicks / ((lastClickTime - gameStartTime) / 1000.0) : 0;
            rows[i][2] = String.format("%.2f/s", clicksPerSecond);
        }
        return new TableDTO(headers, rows);
    }

    /**
     * Receives a click from a player and adds it to the game state.
     *
     * @param headerAccessor The header accessor for the websocket message.
     * @throws InvalidSession    If the session is invalid.
     * @throws RoomNotInProgress If the room is not in progress.
     * @throws WrongGame         If the current game is not this game.
     * @throws GameFinished      If the game is not currently in progress.
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

        // add to the player's game state
        GameStatePerfectClicker state = game.getState().stream()
                .filter(s -> s.getPlayer().equals(player))
                .findFirst()
                .orElseGet(() -> {
                    GameStatePerfectClicker newState = new GameStatePerfectClicker(player);
                    game.getState().add(newState);
                    return newState;
                });
        state.setClicks(state.getClicks() + 1);
        state.setLastClick(System.currentTimeMillis());
        gameRepository.save(game);

        // inform players of the click
        ClickDTO dto = new ClickDTO(state.getPlayer(), state.getClicks(), state.getLastClick());
        messaging.sendToGame(GameplayActions.PERFECT_CLICKER_CLICKS, room.getID(), dto);
    }
}

