package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor
public class GameRemovedDTO {
    private long ID;

}
