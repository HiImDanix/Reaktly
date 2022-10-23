package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GameShortDTO {
    private long ID;
    @NotNull
    private Game.GameType type;

    public GameShortDTO(long ID, Game.GameType type) {
        this.ID = ID;
        this.type = type;
    }
}
