package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.RoomActions;
import com.dkanepe.reaktly.dto.JoinRoomRequest;
import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.dto.RoomDTO;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoomService {

    private final MapStructMapper mapper;

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final CommunicationService messaging;
    private final PlayerService playerService;

    @Autowired
    public RoomService(RoomRepository roomRepository, PlayerRepository playerRepository, MapStructMapper mapper, CommunicationService messaging, PlayerService playerService) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.mapper = mapper;
        this.messaging = messaging;
        this.playerService = playerService;
    }

    /**
     * Find a room by its code
     * @param code Room code
     * @return The room
     */
    private Optional<Room> findRoomByCode(String code){
        return roomRepository.findByCode(code);
    }

    /**
     * Check if a room code is valid
     */
    public boolean isValidRoomCode(String code){
        return findRoomByCode(code.toUpperCase().strip()).isPresent();
    }

    /**
     * Creates a new player and adds it to the room by its code.
     * @param joinRoomRequest Join room request
     * @throws InvalidRoomCode if room with given code does not exist
     * @return PersonalPlayerDTO (includes the player's session id)
     */
    @Transactional
    public PersonalPlayerDTO joinRoom(JoinRoomRequest joinRoomRequest) throws InvalidRoomCode {
        Optional<Room> room = findRoomByCode(joinRoomRequest.getRoomCode().toUpperCase().strip());

        if (room.isEmpty()) {
            throw new InvalidRoomCode("Room with code " + joinRoomRequest.getRoomCode() + " does not exist");
        }

        Player player = playerRepository.save(new Player(joinRoomRequest.getName()));
        room.get().addPlayer(player);
        roomRepository.save(room.get());

        messaging.sendToRoom(RoomActions.PLAYER_JOINED, room.get().getID(), mapper.playerToPlayerDTO(player));
        return mapper.playerToPersonalPlayerDTO(player);

    }

    /**
     * Creates a new room and a new player. The player is added to the room as the host.
     * @param name Player name
     * @return RoomDTO
     */
    @Transactional
    public PersonalPlayerDTO createRoom(String name) {
        Player player = new Player(name);
        player = playerRepository.save(player);
        System.out.println(player.getSession());
        Room room = new Room(player);
        // add default games, for now.
        room.getGames().add(new PerfectClicker(10));
        room.getGames().add(new PerfectClicker(20));
        roomRepository.save(room);
        return mapper.playerToPersonalPlayerDTO(player);
    }

    @Transactional
    public RoomDTO getRoom(SimpMessageHeaderAccessor headers) throws InvalidSession {
        Player player = playerService.findBySessionOrThrowNonDTO(headers);
        Room room = player.getRoom();
        return mapper.roomToRoomDTO(room);
    }

    public void updateRoomStatus(Room room, Room.Status status) {
        room.setStatus(status);
        roomRepository.save(room);
    }
}
