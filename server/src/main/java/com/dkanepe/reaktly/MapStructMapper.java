package com.dkanepe.reaktly;

import com.dkanepe.reaktly.dto.*;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerDTO;
import com.dkanepe.reaktly.dto.PerfectClicker.PerfectClickerGameStateDTO;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.Scoreboard;
import com.dkanepe.reaktly.models.ScoreboardLine;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.GameStatePerfectClicker;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.services.games.GameService;
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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


    TableDTO scoreboardToTableDTO(Scoreboard scoreboard);
    @AfterMapping
    default void afterMappingScoreboardToTableDTO(Scoreboard scoreboard, @MappingTarget TableDTO tableDTO) {
        String[] headers = {"Player", "Score"};
        String[][] data;
        List<ScoreboardLine> lines = scoreboard.getScores().stream()
                .sorted(Comparator.comparingInt(ScoreboardLine::getScore).reversed())
                .collect(Collectors.toList());
        data = new String[lines.size()][2];
        for (int i = 0; i < lines.size(); i++) {
            data[i][0] = lines.get(i).getPlayer().getName();
            data[i][1] = String.valueOf(lines.get(i).getScore());
        }

        // Set the headers and data
        tableDTO.setHeaders(headers);
        tableDTO.setRows(data);
    }
}
