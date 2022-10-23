package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.Room;
import lombok.Data;

import java.util.List;

@Data
public class RoomDTO {
    private long ID;
    private String code;
    private List<PlayerDTO> players;
    private List<GameShortDTO> games;
    private PlayerDTO host;
    private Room.Status status;
    private long startTime;
    private GameDTO currentGame;
    private TableDTO scoreboard;
}
