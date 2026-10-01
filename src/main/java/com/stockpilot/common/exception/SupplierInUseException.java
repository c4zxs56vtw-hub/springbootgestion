package com.stockpilot.common.exception;

public class SupplierInUseException extends RuntimeException {

    public SupplierInUseException(String message) {
        super(message);
    }
}
