package com.dkanepe.reaktly.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameStartDTO {
    private GameDTO currentGame;
    private List<GameDTO> games;
    private long startTime;

    public GameStartDTO(GameDTO currentGame, List<GameDTO> games, long startTime) {
        this.currentGame = currentGame;
        this.games = games;
        this.startTime = startTime;
    }

}

