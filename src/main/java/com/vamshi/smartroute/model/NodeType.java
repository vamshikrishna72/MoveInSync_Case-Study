package com.vamshi.smartroute.model;

/**
 * Categorizes structural and functional indoor campus locations.
 */
public enum NodeType {
    ROOM,
    CORRIDOR,
    JUNCTION,
    STAIRS,
    LIFT,
    ENTRANCE,
    EXIT,
    WASHROOM,
    WATER_POINT,
    EMERGENCY_EXIT,
    ASSEMBLY_POINT;

    /**
     * Helper to determine if node is a Point of Interest (POI).
     */
    public boolean isPoi() {
        return this == WASHROOM ||
               this == WATER_POINT ||
               this == EMERGENCY_EXIT ||
               this == EXIT ||
               this == ASSEMBLY_POINT;
    }
}
