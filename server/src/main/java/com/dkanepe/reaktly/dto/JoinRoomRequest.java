package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.Player;
import lombok.*;

import javax.validation.Valid;

@Data
public class JoinRoomRequest {
    private Player player;
    private String roomCode;

}
