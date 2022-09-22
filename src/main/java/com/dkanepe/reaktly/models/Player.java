package com.dkanepe.reaktly.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Player {

    public Player(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    private Long ID;

    private String session;

    @NotBlank(message = "Player name is required")
    @Size(min = 3, max = 20)
    private String name;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Room game;

    @PrePersist
    protected void onCreate() {
        this.session = java.util.UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(getID(), player.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }

}
