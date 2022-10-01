package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    PlayerDTO playerToPlayerDTO(Player player);

    PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);

    RoomDTO roomToRoomDTO(Room room);
    Room roomDTOToRoom(RoomDTO roomDTO);

    GameStartedDTO roomToGameStartedDTO(Room room);
    @AfterMapping
    default void afterMappingRoomToGameStartedDTO(Room room, @MappingTarget GameStartedDTO gameStartedDTO) {
        // TODO: make this configurable
        // 5 seconds from now
        gameStartedDTO.setStartTime(LocalDateTime.now().plusSeconds(5));
    }


    GameDTO roomToGameDTO(Room room);

}
