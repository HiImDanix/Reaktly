package com.dkanepe.reaktly;

import com.dkanepe.reaktly.exceptions.InvalidSession;
import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.repositories.PlayerRepository;
import com.dkanepe.reaktly.services.PlayerService;
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

    private PlayerService playerService;

    @Autowired
    public WebSocketAuthInterceptorAdapter(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Called when websocket connection is established. Checks if session is valid & associates it with the connection.
     * @throws AuthenticationException if session is invalid
     */
    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {

        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand cmd = accessor.getCommand();

        if (StompCommand.CONNECT == cmd) {
            String sessionToken = accessor.getFirstNativeHeader("Authorization");
            Player player;
            try {
                player = playerService.findBySessionOrThrowNonDTO(sessionToken);
                log.info("New player: " + player);
            } catch (InvalidSession e) {
                log.error("Invalid session: " + sessionToken);
                throw new AccessDeniedException("Invalid session");
            }
            // Save player session ID in header accessor session attributes
            accessor.getSessionAttributes().put(SessionParameters.PLAYER_SESSION.toString(), player.getSession());
        }
        return message;
    }
}
