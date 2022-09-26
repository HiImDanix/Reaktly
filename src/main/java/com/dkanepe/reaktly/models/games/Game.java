package com.dkanepe.reaktly.models.games;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Getter @Setter @NoArgsConstructor @ToString
public abstract class Game {

    public enum GameType {
        PERFECT_CLICKER;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private GameType type;
    private boolean isFinished = false;
}