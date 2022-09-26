package com.dkanepe.reaktly.exceptions;

public class NotEnoughGames extends Exception {
    public NotEnoughGames() {
        super("You have to add at least 1 game.");
    }

    public NotEnoughGames(String message) {
        super(message);
    }
}
