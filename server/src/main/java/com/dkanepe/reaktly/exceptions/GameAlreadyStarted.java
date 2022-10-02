package com.dkanepe.reaktly.exceptions;

public class GameAlreadyStarted extends Exception {
    public GameAlreadyStarted() {
        super("The game has already started!");
    }

    public GameAlreadyStarted(String message) {
        super(message);
    }
}
