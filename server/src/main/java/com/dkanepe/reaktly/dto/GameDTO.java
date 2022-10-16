package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Data;

@Data
public class GameDTO {
    private long ID;
    private Game.GameType type;
    private long startTime;
    private long endTime;
    private long finishTime;
    private Game.GameStatus status;
    private String title;
    private String instructions;
    private String shortInstructions;
    private Object game;
    private TableDTO statistics;

    public GameDTO(long ID, Game.GameType type) {
        this.ID = ID;
        this.type = type;
    }
}
