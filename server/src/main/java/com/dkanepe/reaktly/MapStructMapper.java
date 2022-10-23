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
import com.dkanepe.reaktly.services.games.PerfectClickerService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class MapStructMapper {

    @Autowired
    @Lazy
    PerfectClickerService perfectClickerService;

    public abstract PlayerDTO playerToPlayerDTO(Player player);

    public abstract PersonalPlayerDTO playerToPersonalPlayerDTO(Player player);

    public abstract RoomDTO roomToRoomDTO(Room room);

    @AfterMapping
    public void afterRoomToRoomDTO(Room room, @MappingTarget RoomDTO roomDTO) {
        Game currentGame = room.getCurrentGame();
        // TODO: Refactor to use factory pattern
        if (currentGame instanceof PerfectClicker) {
            roomDTO.setCurrentGame(gameToGameDTO((PerfectClicker) currentGame));
        }
    }

    public abstract GameStartDTO roomToGameStartedDTO(Room room);

    public abstract PerfectClickerDTO perfectClickerToPerfectClickerDTO(PerfectClicker perfectClicker);

    public abstract List<PerfectClickerGameStateDTO> perfectClickerGameStateToDTO(List<GameStatePerfectClicker> gameStatePerfectClickers);

    public abstract GameDTO gameToGameDTO(PerfectClicker perfectClicker);

    @AfterMapping
    public void afterMappingGameToGameDTO(PerfectClicker perfectClicker, @MappingTarget GameDTO gameDTO) {
        gameDTO.setStatistics(perfectClickerService.getStatistics(perfectClicker));
        // Debug
        gameDTO.setGame(perfectClickerToPerfectClickerDTO(perfectClicker));
    }


    public abstract TableDTO scoreboardToTableDTO(Scoreboard scoreboard);

    @AfterMapping
    public void afterMappingScoreboardToTableDTO(Scoreboard scoreboard, @MappingTarget TableDTO tableDTO) {
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

    public abstract GameShortDTO gameToGameAddedDTO(Game game);

    public abstract GameRemovedDTO gameToGameRemovedDTO(Game game);
}
