package com.stockpilot.common.exception;

public class ProductIdentityLockedException extends RuntimeException {

    public ProductIdentityLockedException(String message) {
        super(message);
    }
}
