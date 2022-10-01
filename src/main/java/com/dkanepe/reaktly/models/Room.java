package com.dkanepe.reaktly.models;

import com.dkanepe.reaktly.RandomString;
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
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Room {
    public enum Status {
        LOBBY, ABOUT_TO_START, IN_PROGRESS, FINISHED
    }

    public Room(Player player) {
        this.host = player;
        this.addPlayer(player);
    }

    @Id
    @GeneratedValue
    private long ID;

    @Column(nullable = false, unique = true)
    private String code;

    private Status status = Status.LOBBY;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private Set<Player> players = new HashSet<>();

    @OneToOne
    private Player host;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Scoreboard scoreboard = new Scoreboard();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Game> games = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Game currentGame;

    @PrePersist
    protected void onCreate() {
        RandomString gen = new RandomString(8, ThreadLocalRandom.current());
        String code = gen.nextString().toUpperCase();
        this.setCode(code);
    }

    // Add player to the room and vice versa (bidirectionally)
    public void addPlayer(Player player) {
        players.add(player);
        player.setRoom(this);
    }
}
