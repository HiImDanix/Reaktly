package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.dto.JoinRoomRequest;
import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.dto.PlayerDTO;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.services.PlayerService;
import com.dkanepe.reaktly.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class RoomController {

    private final RoomService roomService;
    private final MapStructMapper mapper;
    private final PlayerService playerService;

    @Autowired
    public RoomController(RoomService roomService, MapStructMapper mapper, PlayerService playerService) {
        this.roomService = roomService;
        this.mapper = mapper;
        this.playerService = playerService;
    }

    @GetMapping("/player/session/{session}")
    public ResponseEntity<PlayerDTO> getPlayerBySession(@PathVariable String session) {
        try {
            PlayerDTO playerDTO = playerService.findBySessionOrThrow(session);
            return new ResponseEntity(playerDTO, HttpStatus.OK);
        } catch (InvalidSession e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("join")
    public PersonalPlayerDTO joinRoom(@RequestBody JoinRoomRequest request) throws InvalidRoomCode {
        return roomService.joinRoom(request);
    }
}
