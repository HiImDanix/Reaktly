package com.dkanepe.reaktly.models.games.PerfectClicker;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Click the button as fast as you can until you reach the displayed number.
 * But be careful, if you go over the number you lose!
 */
@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PerfectClicker extends Game {

    @Transient
    private String title = "Perfect Clicker";



    private int targetClicks;
    private int gameDurationMillis;
    private int instructionsDurationMillis;

    // TODO: Make it work without eager loading.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("clicks DESC, lastClick ASC")
    private List<GameStatePerfectClicker> state = new ArrayList<>();



    public PerfectClicker(int targetClicks) {
        this.targetClicks = targetClicks;
        super.setType(GameType.PERFECT_CLICKER);
    }

    public String getInstructions() {
        return String.format("Click the button as fast as you can until you reach the number <strong>%d</strong>. But be careful, if you go over the number you lose!", getTargetClicks());
    }

    public String getShortInstructions() {
        return String.format("Click the button as fast as you can. But, be careful! if you go over %d, you lose!", getTargetClicks());
    }

}
