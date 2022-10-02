package com.dkanepe.reaktly.exceptions;

public class InvalidSession extends Exception {
    public InvalidSession(String message) {
        super(message);
    }

    public InvalidSession() {
        super("Invalid or expired session token");
    }
}
