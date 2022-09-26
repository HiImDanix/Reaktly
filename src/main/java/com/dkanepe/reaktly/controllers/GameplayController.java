package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.exceptions.GameAlreadyStarted;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.exceptions.NotEnoughGames;
import com.dkanepe.reaktly.exceptions.NotEnoughPlayers;
import com.dkanepe.reaktly.services.GameplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("gameplay")
public class GameplayController {
    private final GameplayService gameplayService;

    @Autowired
    public GameplayController(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
    }

    @MessageMapping("start")
    public void start(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, NotEnoughPlayers,
            NotEnoughGames, GameAlreadyStarted {
        gameplayService.startGame(headerAccessor);
    }

}
