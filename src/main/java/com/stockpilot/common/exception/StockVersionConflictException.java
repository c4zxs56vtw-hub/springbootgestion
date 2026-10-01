package com.stockpilot.common.exception;

public class StockVersionConflictException extends RuntimeException {

    public StockVersionConflictException(String message) {
        super(message);
    }
}