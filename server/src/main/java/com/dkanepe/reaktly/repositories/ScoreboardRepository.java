package com.dkanepe.reaktly.repositories;

import com.dkanepe.reaktly.models.Scoreboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long>{

}
