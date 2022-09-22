package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.SessionParameters;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.repositories.ScoreboardRepository;
import com.dkanepe.reaktly.services.GameplayService;
import com.dkanepe.reaktly.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class GameplayController {
    private final MapStructMapper mapper;
    private final ScoreboardRepository scoreboardRepository;
    private final PlayerService playerService;
    private final GameplayService gameplayService;

    @Autowired
    public GameplayController(MapStructMapper mapper,
                              ScoreboardRepository scoreboardRepository,
                              PlayerService playerService,
                              GameplayService gameplayService) {
        this.mapper = mapper;
        this.scoreboardRepository = scoreboardRepository;
        this.playerService = playerService;
        this.gameplayService = gameplayService;
    }

    @MessageMapping("/gameplay.click")
    @SendTo("/topic/click")
    public Scoreboard sendClick(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        Player player = playerService.findBySessionOrThrow(headerAccessor);
        return gameplayService.click(player);
    }
}
