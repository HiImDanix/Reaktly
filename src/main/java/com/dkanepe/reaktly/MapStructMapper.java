package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.PersonalPlayerDTO;
import com.dkanepe.reaktly.dto.PlayerDTO;
import com.dkanepe.reaktly.models.Player;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    PlayerDTO playerToPlayerDTO(Player player);
    Player playerDTOToPlayer(PlayerDTO playerDTO);

    PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);
    Player personalPlayerDTOToPlayer(PersonalPlayerDTO personalPlayerDTO);
}
