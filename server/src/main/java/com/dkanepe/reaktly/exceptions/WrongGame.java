package com.dkanepe.reaktly.exceptions;

public class WrongGame extends Exception {
    public WrongGame(String message) {
        super(message);
    }

    public WrongGame() {
        super("This game is not currently in play.");
    }
}