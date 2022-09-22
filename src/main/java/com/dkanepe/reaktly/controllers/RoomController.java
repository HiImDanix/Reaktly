package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.dto.JoinRoomRequest;
import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class RoomController {

    private final RoomService roomService;
    private final MapStructMapper mapper;

    @Autowired
    public RoomController(RoomService roomService, MapStructMapper mapper) {
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @PostMapping("join")
    public PersonalPlayerDTO joinRoom(@RequestBody JoinRoomRequest request) throws InvalidRoomCode {
        return roomService.joinRoom(request);
    }
}
