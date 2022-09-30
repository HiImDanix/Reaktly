package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.dto.PlayerDTO;
import com.dkanepe.reaktly.dto.RoomDTO;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    PlayerDTO playerToPlayerDTO(Player player);
    Player playerDTOToPlayer(PlayerDTO playerDTO);

    PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);
    Player personalPlayerDTOToPlayer(PersonalPlayerDTO personalPlayerDTO);

    RoomDTO roomToRoomDTO(Room room);
    Room roomDTOToRoom(RoomDTO roomDTO);
}
