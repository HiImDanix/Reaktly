package com.dkanepe.reaktly.models;

import com.dkanepe.reaktly.dto.PlayerDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Scoreboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("score DESC")
    private Set<ScoreboardLine> scores = new HashSet<>();
}
