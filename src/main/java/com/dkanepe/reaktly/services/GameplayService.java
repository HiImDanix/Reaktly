package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.exceptions.GameAlreadyStarted;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.exceptions.NotEnoughGames;
import com.dkanepe.reaktly.exceptions.NotEnoughPlayers;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.repositories.RoomRepository;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

    public GameplayService(MapStructMapper mapper, ScoreboardRepository scoreboardRepository,
                           PlayerService playerService, RoomRepository roomRepository,
                           PerfectClickerService perfectClickerService, @Lazy GameplayService self) {
        this.scoreboardRepository = scoreboardRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.roomRepository = roomRepository;
        this.perfectClickerService = perfectClickerService;
        this.self = self;
    }

    @Transactional
    public Player test(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers,
            NotEnoughGames, GameAlreadyStarted {
        Player player = playerService.findBySessionOrThrowNonDTO(headerAccessor);
        Room room = player.getRoom();
        Hibernate.initialize(room);
        if (room.getStatus() != Room.Status.WAITING) {
            throw new GameAlreadyStarted("Cannot start the game because it has already started!");
        }
        if (room.getPlayers().size() < 1) {
            throw new NotEnoughPlayers("Not enough players to start the game!");
        }
        if (room.getGames().size() < 1) {
            throw new NotEnoughGames("You can't start playing without adding any mini-games!");
        }
        Game game = room.getGames().iterator().next();
        room.setCurrentGame(game);
        room.getGames().remove(game);
        room.setStatus(Room.Status.PLAYING);
        roomRepository.save(room);
        return player;
    }

    public void startGame(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers
            , NotEnoughGames, GameAlreadyStarted {
        Player player = self.test(headerAccessor);
        perfectClickerService.startGame(player);
//        switch (game.getType()) {
//            case PERFECT_CLICKER:
//                perfectClickerService.startGame(player);
//                break;
//        }

    }

}
