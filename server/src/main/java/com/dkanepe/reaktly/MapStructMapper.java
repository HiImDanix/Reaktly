package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerDTO;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerGameStateDTO;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.PerfectClicker.GameStatePerfectClicker;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    PlayerDTO playerToPlayerDTO(Player player);

    PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);

    RoomDTO roomToRoomDTO(Room room);

    GameStartDTO roomToGameStartedDTO(Room room);

    PerfectClickerDTO perfectClickerToPerfectClickerDTO(PerfectClicker perfectClicker);
    @AfterMapping
    default void afterMappingPerfectClickerToPerfectClickerDTO(PerfectClicker perfectClicker,
                                                               @MappingTarget PerfectClickerDTO perfectClickerDTO) {
        long instructionsDuration = perfectClicker.getInstructionsDurationMillis();
        perfectClickerDTO.setStartTime(LocalDateTime.now().plusNanos(instructionsDuration * 1000000));
        long gameDuration = perfectClicker.getGameDurationMillis();
        perfectClickerDTO.setEndTime(perfectClickerDTO.getStartTime().plusNanos(gameDuration * 1000000));

    };

    List<PerfectClickerGameStateDTO> perfectClickerGameStateToDTO(List<GameStatePerfectClicker> gameStatePerfectClickers);



}
