package com.dkanepe.reaktly.dto.PerfectClicker;

import com.dkanepe.reaktly.models.Player;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClickDTO {
    private Long playerID;
    private LocalDateTime timeClicked;

    public ClickDTO(Player player, LocalDateTime timeClicked) {
        this.playerID = player.getID();
        this.timeClicked = timeClicked;
    }


}
