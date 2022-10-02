package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.dto.PerfectClicker.ClickDTO;
import com.dkanepe.reaktly.exceptions.GameFinished;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.exceptions.RoomNotInProgress;
import com.dkanepe.reaktly.exceptions.WrongGame;
import com.dkanepe.reaktly.services.CommunicationService;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("gameplay/PerfectClicker")
public class PerfectClickerC {

    public final PerfectClickerService perfectClickerService;
    public final CommunicationService communicationService;

    public PerfectClickerC(PerfectClickerService perfectClickerService, CommunicationService communicationService) {
        this.perfectClickerService = perfectClickerService;
        this.communicationService = communicationService;
    }

    @MessageMapping("click")
    public void sendClick(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession, RoomNotInProgress, WrongGame, GameFinished {
        perfectClickerService.click(headerAccessor);
    }
}

