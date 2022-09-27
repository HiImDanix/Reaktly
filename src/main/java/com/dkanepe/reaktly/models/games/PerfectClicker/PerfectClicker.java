package com.dkanepe.reaktly.models.games.PerfectClicker;

import com.dkanepe.reaktly.models.games.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Click the button as fast as you can until you reach the displayed number.
 * But be careful, if you go over the number you lose!
 */
@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PerfectClicker extends Game {

    private int targetClicks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GameStatePerfectClicker> state = new HashSet<>();



    public PerfectClicker(int targetClicks) {
        this.targetClicks = targetClicks;
        super.setType(GameType.PERFECT_CLICKER);
    }

}
