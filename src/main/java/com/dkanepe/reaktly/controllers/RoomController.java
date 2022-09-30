package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
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
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
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

    /**
     * Check if a session is (still) valid (i.e. in case of reconnecting)
     * @param session
     * @return HttpStatus.OK if valid, HttpStatus.NOT_FOUND if not
     */
    @GetMapping("/player/session/{session}")
    public @ResponseBody ResponseEntity<PlayerDTO> getPlayerBySession(@PathVariable String session) {
        try {
            PlayerDTO playerDTO = playerService.findBySessionOrThrow(session);
            return new ResponseEntity(playerDTO, HttpStatus.OK);
        } catch (InvalidSession e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Join a room
     * @return The player (with session)
     * @throws InvalidRoomCode if the entered room code is invalid
     */
    @PostMapping("join")
    public @ResponseBody PersonalPlayerDTO joinRoom(@RequestBody JoinRoomRequest request) throws InvalidRoomCode {
        return roomService.joinRoom(request);
    }

    /**
     * Create a room
     * @return The player (with session)
     */
    @PostMapping("create")
    public @ResponseBody PersonalPlayerDTO createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    /**
     * Get room and send back to user using queue
     */
    @MessageMapping("room")
    @SendToUser("/queue/room")
    public RoomDTO getRoom(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        return roomService.getRoom(headerAccessor);
    }
}
