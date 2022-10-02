package com.dkanepe.reaktly.dto;

import com.dkanepe.reaktly.models.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data @NoArgsConstructor
public class CreateRoomRequest {
    private Player player;
}
