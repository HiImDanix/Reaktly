package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class GameStartDTO {
    private long startTime;

    public GameStartDTO(long startTime) {
        this.startTime = startTime;
    }

}

