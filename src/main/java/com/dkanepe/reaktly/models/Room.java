package com.dkanepe.reaktly.models;

import com.dkanepe.reaktly.RandomString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Room {
    public enum Status {
        WAITING, PLAYING
    }

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    private String code;

    private Status status = Status.WAITING;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "game")
    private Set<Player> players = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Scoreboard scoreboard = new Scoreboard();

    @PrePersist
    protected void onCreate() {
        RandomString gen = new RandomString(8, ThreadLocalRandom.current());
        String code = gen.nextString().toUpperCase();
        this.setCode(code);
    }

    // Add player to the room and vice versa (bidirectionally)
    public void addPlayer(Player player) {
        players.add(player);
        player.setGame(this);
    }
}
