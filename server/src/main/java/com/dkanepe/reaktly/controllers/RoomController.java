package com.dkanepe.reaktly.controllers;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.services.PlayerService;
import com.dkanepe.reaktly.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     * Get a player by session ID (i.e. in case of reconnecting)
     * @param session
     * @return PlayerDTO, OR 404 if not found
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
     * Check if a room code is valid
     * @param roomCode
     * @return HttpStatus.OK if valid, HttpStatus.NOT_FOUND if not
     */
    @GetMapping("/room_code/{roomCode}")
    public @ResponseBody ResponseEntity<String> getRoomByCode(@PathVariable String roomCode) {
            boolean exists = roomService.isValidRoomCode(roomCode);
            if (exists) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
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
     * Create a new room (and player as host)
     * @param name player name
     * @return The player (with session)
     */
    @PostMapping("player")
    public @ResponseBody PersonalPlayerDTO createRoom(@RequestParam String name) {
        return roomService.createRoom(name);
    }

    /**
     * Get room info. It contains all information needed to display any state of the room (lobby, game, etc.).
     */
    @MessageMapping("room")
    @SendToUser("/queue/room")
    public RoomDTO getRoom(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        return roomService.getRoom(headerAccessor);
    }

    /**
     * Add game to room
     */
    @MessageMapping("room.add_game")
    public void addGameToRoom(SimpMessageHeaderAccessor headerAccessor, GameShortDTO gameShortDTO) throws InvalidSession {
        roomService.addGameToRoom(headerAccessor, gameShortDTO);
    }

    /**
     * Remove game from room
     */
    @MessageMapping("room.remove_game")
    public void removeGameFromRoom(SimpMessageHeaderAccessor headerAccessor, GameRemovedDTO gameRemovedDTO) throws InvalidSession {
        System.out.println("Removing game " + gameRemovedDTO);
        roomService.removeGameFromRoom(headerAccessor, gameRemovedDTO);
    }

}
