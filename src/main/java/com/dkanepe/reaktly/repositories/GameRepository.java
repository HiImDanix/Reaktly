package com.dkanepe.reaktly.repositories;

import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.games.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>{
}
