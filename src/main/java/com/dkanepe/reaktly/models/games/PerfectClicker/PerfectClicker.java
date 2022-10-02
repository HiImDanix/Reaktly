package com.dkanepe.reaktly.models.games.PerfectClicker;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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

    private int targetClicks;
    private int gameDurationMillis;
    private int instructionsDurationMillis;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("clicks DESC, lastClick ASC")
    private List<GameStatePerfectClicker> state = new ArrayList<>();



    public PerfectClicker(int targetClicks, int gameDurationMillis, int instructionsDurationMillis) {
        this.targetClicks = targetClicks;
        super.setType(GameType.PERFECT_CLICKER);
        this.gameDurationMillis = gameDurationMillis;
        this.instructionsDurationMillis = instructionsDurationMillis;
    }

}
