package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Data;

@Data
public class GameDTO {
    private long ID;
    private Game.GameType type;

    public GameDTO(long ID, Game.GameType type) {
        this.ID = ID;
        this.type = type;
    }
}
