package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.actions.RoomActions;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for sending websocket messages to clients.
 */
@Service
public class CommunicationService {

    private final SimpMessagingTemplate messaging;

    public CommunicationService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    /**
     * Send a message about actions relating to a room
     * @param actionName The name of the action
     * @param roomID The ID of the room
     * @param dto The data to send
     */
    public void sendToRoom(RoomActions actionName, long roomID, Object dto) {
        messaging.convertAndSend("/topic/room/" + roomID + "/" + actionName, dto);
    }

    /**
     * Send a message about actions relating to a specific game
     * @param actionName The name of the action
     * @param roomID The ID of the room
     * @param dto The data to send
     */
    public void sendToGame(GameplayActions actionName, long roomID, Object dto) {
        messaging.convertAndSend("/topic/room/" + roomID + "/gameplay/" + actionName, dto);
    }

}
