package com.vamshi.smartroute.exception;

public class RouteNotFoundException extends SmartRouteException {
    public RouteNotFoundException(String message) {
        super(message, "ROUTE_NOT_FOUND");
    }
}
