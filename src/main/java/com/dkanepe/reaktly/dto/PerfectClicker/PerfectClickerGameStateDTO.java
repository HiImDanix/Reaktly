package com.dkanepe.reaktly.dto.PerfectClicker;

import com.dkanepe.reaktly.dto.PlayerDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerfectClickerGameStateDTO {

    private PlayerDTO player;
    private int clicks;
    private LocalDateTime lastClick;

    public PerfectClickerGameStateDTO(PlayerDTO player, int clicks, LocalDateTime lastClick) {
        this.player = player;
        this.clicks = clicks;
        this.lastClick = lastClick;
    }
}
