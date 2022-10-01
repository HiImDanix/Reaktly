package com.dkanepe.reaktly.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GameStartedDTO {
    private GameDTO currentGame;
    private List<GameDTO> games;
    private LocalDateTime startTime;

    public GameStartedDTO(GameDTO currentGame, List<GameDTO> games, LocalDateTime startTime) {
        this.currentGame = currentGame;
        this.games = games;
        this.startTime = startTime;
    }

}

