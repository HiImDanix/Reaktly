package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.SessionParameters;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Player findBySessionOrThrow(String session) throws InvalidSession {
        return playerRepository.findBySession(session).orElseThrow(InvalidSession::new);
    }

    public Player findBySessionOrThrow(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        String session = (String) headerAccessor.getSessionAttributes().get(SessionParameters.PLAYER_SESSION.toString());
        return findBySessionOrThrow(session);
    }
}
