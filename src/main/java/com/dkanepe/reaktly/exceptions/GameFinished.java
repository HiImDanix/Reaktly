package com.dkanepe.reaktly.exceptions;

public class GameFinished extends Exception {
    public GameFinished() {
        super("This game is already finished.");
    }

    public GameFinished(String message) {
        super(message);
    }
}