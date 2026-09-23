package com.vamshi.smartroute.exception;

import java.util.UUID;

public class NodeNotFoundException extends SmartRouteException {
    public NodeNotFoundException(UUID nodeId) {
        super("Node with ID " + nodeId + " was not found in campus graph", "NODE_NOT_FOUND");
    }
}
