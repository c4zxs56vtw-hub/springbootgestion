package com.stockpilot.common.exception;

public class InvalidMovementTypeException extends RuntimeException {

    public InvalidMovementTypeException(String message) {
        super(message);
    }
}
