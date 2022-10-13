package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerDTO;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerGameStateDTO;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.GameStatePerfectClicker;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    PlayerDTO playerToPlayerDTO(Player player);

    PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);

    RoomDTO roomToRoomDTO(Room room);

    @AfterMapping
    default void afterRoomToRoomDTO(Room room, @MappingTarget RoomDTO roomDTO) {
        Game currentGame = room.getCurrentGame();
        if (currentGame instanceof PerfectClicker) {
            roomDTO.setCurrentGame(gameToGameDTO((PerfectClicker) currentGame));
        }
    }

    GameStartDTO roomToGameStartedDTO(Room room);

    PerfectClickerDTO perfectClickerToPerfectClickerDTO(PerfectClicker perfectClicker);

    List<PerfectClickerGameStateDTO> perfectClickerGameStateToDTO(List<GameStatePerfectClicker> gameStatePerfectClickers);

    GameDTO gameToGameDTO(PerfectClicker perfectClicker);

    @AfterMapping
    default void afterMappingGameToGameDTO(PerfectClicker perfectClicker, @MappingTarget GameDTO gameDTO){
        gameDTO.setGame(perfectClickerToPerfectClickerDTO(perfectClicker));
    }






}
