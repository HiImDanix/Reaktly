package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("gameplay/PerfectClicker")
public class PerfectClickerC {

    public final PerfectClickerService perfectClickerService;

    public PerfectClickerC(PerfectClickerService perfectClickerService) {
        this.perfectClickerService = perfectClickerService;
    }

    @MessageMapping("click")
    @SendTo("/topic/click")
    public Scoreboard sendClick(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        return perfectClickerService.click(headerAccessor);
    }
}

