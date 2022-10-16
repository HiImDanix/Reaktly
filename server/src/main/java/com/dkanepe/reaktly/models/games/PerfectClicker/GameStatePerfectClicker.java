package com.dkanepe.reaktly.models.games.PerfectClicker;

import com.dkanepe.reaktly.models.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class GameStatePerfectClicker {

    public GameStatePerfectClicker(Player player) {
        this.player = player;
        this.clicks = 0;
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Player player;

    private int clicks;

    private long lastClick;


}