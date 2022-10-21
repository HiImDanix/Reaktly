package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class GameEndDTO {
    TableDTO scoreboard;
    TableDTO statistics;
    boolean isLastGame;

    public  GameEndDTO(TableDTO scoreboard, TableDTO statistics, boolean isLastGame) {
        this.scoreboard = scoreboard;
        this.statistics = statistics;
        this.isLastGame = isLastGame;
    }
}
