package com.dkanepe.reaktly.services;

import com.dkanepe.reaktly.actions.GameplayActions;
import com.dkanepe.reaktly.actions.RoomActions;
import com.dkanepe.reaktly.models.games.Game;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService {
    /*
    * This class is used to send messages to the client
     */

    private final SimpMessagingTemplate messaging;

    public CommunicationService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    /*
        This service allows you to communicate with players in a room.
     */
    public void sendToRoom(RoomActions actionName, long roomID, Object dto) {
        messaging.convertAndSend("/topic/room/" + roomID + "/" + actionName, dto);
    }

    /*
        This service allows you to communicate with players playing a specific game.
     */
    public void sendToGame(GameplayActions actionName, long roomID, Object dto) {
        String dest = String.format("/topic/room/%d/gameplay/%s", roomID, actionName);
        System.out.println(dest);
        messaging.convertAndSend(dest, dto);
    }

}
