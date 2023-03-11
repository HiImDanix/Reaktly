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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrafficLightService implements GameService {

    private final ScoreboardRepository scoreboardRepository;
    private final MapStructMapper mapper;
    private final PlayerService playerService;
    private final GameRepository gameRepository;
    private final TrafficLightService self;

    private final CommunicationService messaging;
    private final RoomRepository roomRepository;
    private final EntityManager entityManager;

    public TrafficLightService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                               PlayerService playerService, GameRepository gameRepository,
                               CommunicationService messaging, @Lazy TrafficLightService self,
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
        // For now, return empty list
        return List.of();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void distributePoints(Game theGame, int maxPoints, int firstPlaceBonus, int secondPlaceBonus,
                                 int thirdPlaceBonus) {
        // For now, do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TableDTO getStatistics(Game theGame) {
        // TODO: Find a better data structure than this. Should include Player (obj) as 1st, and clicks as 2nd.
        //  First column should be required and standardized.
        //  Eventually move to the mapper.
        // For now, return empty table
        String[] headers = new String[]{"Player", "Clicks", "Clicks/Sec"};
        return new TableDTO(headers, new String[0][0]);
    }
}

