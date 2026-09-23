package com.vamshi.smartroute.exception;

public class SmartRouteException extends RuntimeException {
    private final String errorCode;

    public SmartRouteException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
