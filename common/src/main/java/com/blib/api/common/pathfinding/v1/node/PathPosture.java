package com.blib.api.common.pathfinding.v1.node;

public enum PathPosture {
    STANDING,
    CRAWLING,
    SWIMMING;

    public boolean isCrawling() {
        return this == CRAWLING;
    }

    public boolean isSwimming() {
        return this == SWIMMING;
    }
}
