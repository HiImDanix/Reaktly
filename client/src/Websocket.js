import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

import Config from './Config';

/**
 * Websocket class
 *
 * This class uses the stomp protocol to connect to the websocket server and send/receive messages.
 * Subscribing to a URL is possible only once.
 * Multiple callbacks can be added to a subscription.
 * Same callbacks can be added only once.
 */
class Websocket {
    methodsToCall = {}

    constructor() {
        this.stompClient = Stomp.over(() => new SockJS(`${Config.SERVER_URL}/ws`));
    }

    // Subscribe to a URL
    subscribe(url, methodToAdd) {
        // If the URL has not been subscribed to before, subscribe to it.
        if (this.methodsToCall[url] === undefined) {
            this.methodsToCall[url] = []
            this.stompClient.subscribe(url, (payload) => {
                this.methodsToCall[url].forEach(method => method(payload));
            });
        }
        // Add the method to the list of methods to call when a message is received.
        if (methodToAdd !== undefined) {
            if (!this.methodsToCall[url].includes(methodToAdd)) {
                this.methodsToCall[url].push(methodToAdd);
            }
        }
    }

    // Connect to the websocket server
    connect(session, onConnected, onError) {
        this.stompClient.connect({Authorization:session}, onConnected, onError);
    }

    // Disconnect from the websocket server
    disconnect() {
        this.stompClient.disconnect();
    }

    // Send a message to the websocket server
    send(url, message) {
        this.stompClient.send(url, {}, message);
    }
}

export default Websocket;