package com.dkanepe.reaktly.dto.PerfectClicker;

import lombok.Data;

import java.util.List;

@Data
public class PerfectClickerDTO {
    private int targetClicks;
    private List<PerfectClickerGameStateDTO> state;


    public PerfectClickerDTO(int targetClicks) {
        this.targetClicks = targetClicks;
    }
}
