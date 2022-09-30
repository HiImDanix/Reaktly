package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private long ID;

    public RoomDTO(long ID) {
        this.ID = ID;
    }
}
