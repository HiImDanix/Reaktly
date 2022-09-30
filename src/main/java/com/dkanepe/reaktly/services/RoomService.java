package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.actions.RoomActions;
import com.dkanepe.reaktly.dto.CreateRoomRequest;
import com.dkanepe.reaktly.dto.JoinRoomRequest;
import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.exceptions.InvalidRoomCode;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import com.dkanepe.reaktly.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoomService {

    private final MapStructMapper mapper;

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final CommunicationService messaging;

    @Autowired
    public RoomService(RoomRepository roomRepository, PlayerRepository playerRepository, MapStructMapper mapper, CommunicationService messaging) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.mapper = mapper;
        this.messaging = messaging;
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
     * If room code is valid, creates a new player and adds it to the room.
     * @param joinRoomRequest Join room request
     * @throws InvalidRoomCode if room with given code does not exist
     * @return PersonalPlayerDTO
     */
    @Transactional
    public PersonalPlayerDTO joinRoom(JoinRoomRequest joinRoomRequest) throws InvalidRoomCode {
        Optional<Room> room = findRoomByCode(joinRoomRequest.getRoomCode());

        if (!room.isPresent()) {
            throw new InvalidRoomCode("Room with code " + joinRoomRequest.getRoomCode() + " does not exist");
        }

        Player player = playerRepository.save(new Player(joinRoomRequest.getPlayer().getName()));
        room.get().addPlayer(player);
        roomRepository.save(room.get());

        messaging.sendToRoom(RoomActions.PLAYER_JOINED, room.get().getId(), mapper.playerToPlayerDTO(player));
        return mapper.playerToPersonalPlayerDTO(player);

    }

    @Transactional
    public PersonalPlayerDTO createRoom(CreateRoomRequest request) {
        Player player = new Player(request.getPlayer().getName());
        player = playerRepository.save(player);
        System.out.println(player.getSession());
        Room room = new Room(player);
        roomRepository.save(room);
        return mapper.playerToPersonalPlayerDTO(player);
    }
}
