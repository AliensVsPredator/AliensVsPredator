package com.lib.common.model;

public enum GeneDecayLevelType {

    FATAL(3),
    STABLE(0),
    UNSTABLE(1),
    VOLATILE(2);

    private final int magnitude;

    GeneDecayLevelType(int magnitude) {
        this.magnitude = magnitude;
    }

    public int getMagnitude() {
        return magnitude;
    }
}
