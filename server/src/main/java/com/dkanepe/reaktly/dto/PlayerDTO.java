package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class PlayerDTO {
    private long ID;
    private String name;

    public PlayerDTO(long ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}
