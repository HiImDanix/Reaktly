package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class GameEndDTO {
    TableDTO scoreboard;
    TableDTO statistics;

    public GameEndDTO(TableDTO scoreboard, TableDTO statistics) {
        this.scoreboard = scoreboard;
        this.statistics = statistics;
    }
}
