package com.dkanepe.reaktly.repositories;

import com.dkanepe.reaktly.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>{
    Optional<Player> findBySession(String session);
}
