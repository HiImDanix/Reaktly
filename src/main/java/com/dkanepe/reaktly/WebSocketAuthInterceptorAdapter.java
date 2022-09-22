package com.dkanepe.reaktly;

import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class WebSocketAuthInterceptorAdapter implements ChannelInterceptor {

    private PlayerRepository playerRepository;

    @Autowired
    public WebSocketAuthInterceptorAdapter(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {

        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand cmd = accessor.getCommand();

        if (StompCommand.CONNECT == cmd) {
            String sessionToken = accessor.getFirstNativeHeader("Authorization");
            log.info("Session token: " + sessionToken);
            Optional<Player> player = playerRepository.findBySession(sessionToken);
            log.info("Player: " + player);
            if (player.isEmpty()) {
                log.info("Player not found. Invalid session token.");
                throw new AccessDeniedException("Invalid session token");
            }
            // Save player session ID in header accessor session attributes
            accessor.getSessionAttributes().put(SessionParameters.PLAYER_SESSION.toString(), player.get().getSession());



        }
        return message;
    }
}
