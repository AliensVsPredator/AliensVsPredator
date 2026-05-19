package com.blib.api.common.pathfinding.v1.node;

public enum PathPosture {
    STANDING,
    CRAWLING;

    public boolean isCrawling() {
        return this == CRAWLING;
    }
}
