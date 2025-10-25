package com.quorion.b2b.exception;

/**
 * Exception thrown when an invalid state machine transition is attempted
 */
public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String message) {
        super(message);
    }

    public InvalidStateTransitionException(String currentState, String targetState) {
        super(String.format("Invalid state transition from %s to %s", currentState, targetState));
    }
}
