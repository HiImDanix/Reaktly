package com.dkanepe.reaktly.exceptions;

public class RoomNotInProgress extends Exception {
    public RoomNotInProgress(String message) {
        super(message);
    }

    public RoomNotInProgress() {
        super("Room status is not in progress. Cannot perform this action.");
    }
}
