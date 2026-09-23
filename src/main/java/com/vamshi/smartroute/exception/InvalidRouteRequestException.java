package com.vamshi.smartroute.exception;

public class InvalidRouteRequestException extends SmartRouteException {
    public InvalidRouteRequestException(String message) {
        super(message, "INVALID_ROUTE_REQUEST");
    }
}
