package com.dkanepe.reaktly.exceptions;

public class NotEnoughPlayers extends Exception {
    public NotEnoughPlayers() {
        super("Not enough players");
    }

    public NotEnoughPlayers(String message) {
        super(message);
    }
}
