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

    public enum GameStatus {
        NOT_STARTED,
        INSTRUCTIONS,
        IN_PROGRESS,
        FINISHED
    }

    public enum GameType {
        PERFECT_CLICKER,
        TRAFFIC_LIGHT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    private GameType type;
    private GameStatus status = GameStatus.NOT_STARTED;

    // TODO: Get from config. Add scoreboard duration only in DTO.
    private int instructionsDurationMillis = 8000;
    private int gameDurationMillis = 10000;
    private long startTime;
    private int scoreboardDurationMillis = 5000;

    public Game(GameType type) {
        this.type = type;
    }

    public long getEndTime() {
        return startTime + gameDurationMillis;
    }

    public long getFinishTime() {
        return getEndTime() + scoreboardDurationMillis;
    }

    public abstract String getInstructions();
    public abstract String getShortInstructions();
    public abstract String getTitle();

}