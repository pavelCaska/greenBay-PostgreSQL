package com.pc.greenbay.exception;

public class ItemNotSellableException extends RuntimeException {
    public ItemNotSellableException(String message) {
        super(message);
    }
}
