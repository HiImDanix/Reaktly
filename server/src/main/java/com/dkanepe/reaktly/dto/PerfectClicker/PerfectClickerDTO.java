package com.dkanepe.reaktly.dto.PerfectClicker;

import com.dkanepe.reaktly.models.Player;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerfectClickerDTO {
    private int targetClicks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public PerfectClickerDTO(int targetClicks, LocalDateTime startTime, LocalDateTime endTime) {
        this.targetClicks = targetClicks;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
