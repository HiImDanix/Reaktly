package com.dkanepe.reaktly.dto.PerfectClicker;

import com.dkanepe.reaktly.models.Player;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClickDTO {
    private Long playerID;
    private int clicks;
    private long timeClicked;

    public ClickDTO(Player player, int Clicks, long timeClicked) {
        this.playerID = player.getID();
        this.clicks = Clicks;
        this.timeClicked = timeClicked;
    }


}
