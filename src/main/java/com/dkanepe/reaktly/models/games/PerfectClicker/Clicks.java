package com.dkanepe.reaktly.models.games.PerfectClicker;

import com.dkanepe.reaktly.models.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Clicks {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Player player;

    private int clicks;


}