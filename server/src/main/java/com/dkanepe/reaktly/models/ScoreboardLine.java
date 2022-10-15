package com.dkanepe.reaktly.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class ScoreboardLine {

    public ScoreboardLine(Player player) {
        this.player = player;
        this.score = 0;
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Player player;

    private Integer score;

}
