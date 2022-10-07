package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.Room;
import lombok.Data;

import java.util.List;

@Data
public class RoomDTO {
    private long ID;
    private String code;
    private List<PlayerDTO> players;
    private PlayerDTO host;
    private Room.Status status;

    public RoomDTO(long ID, String code, List<PlayerDTO> players, PlayerDTO host, Room.Status status) {
        this.ID = ID;
        this.code = code;
        this.players = players;
        this.host = host;
        this.status = status;
    }
}