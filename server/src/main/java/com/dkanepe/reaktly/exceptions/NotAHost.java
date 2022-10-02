package com.dkanepe.reaktly.exceptions;

public class NotAHost extends Exception {
    public NotAHost(String message) {
        super(message);
    }

    public NotAHost() {
        super("You cannot perform this action, because you are not the room's host");
    }
}
