package com.stockpilot.common.exception;

public class ProductHasStockException extends RuntimeException {

    public ProductHasStockException(String message) {
        super(message);
    }
}