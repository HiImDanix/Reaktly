package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.MapStructMapper;
import com.dkanepe.reaktly.SessionParameters;
import com.dkanepe.reaktly.dto.PlayerDTO;
import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MapStructMapper mapper;

    public PlayerService(MapStructMapper mapper, PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.mapper = mapper;
    }

    public PlayerDTO findBySessionOrThrow(String session) throws InvalidSession {
        Player player = playerRepository.findBySession(session).orElseThrow(InvalidSession::new);
        return mapper.playerToPlayerDTO(player);
    }

    public PlayerDTO findBySessionOrThrow(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        String session = (String) headerAccessor.getSessionAttributes().get(SessionParameters.PLAYER_SESSION.toString());
        return findBySessionOrThrow(session);
    }

    public Player findBySessionOrThrowNonDTO(SimpMessageHeaderAccessor headerAccessor) throws InvalidSession {
        String session = (String) headerAccessor.getSessionAttributes().get(SessionParameters.PLAYER_SESSION.toString());
        return playerRepository.findBySession(session).orElseThrow(InvalidSession::new);
    }
}
