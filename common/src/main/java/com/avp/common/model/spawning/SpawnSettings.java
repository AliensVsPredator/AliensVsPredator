package com.avp.common.model.spawning;

public record SpawnSettings(
    boolean enabled,
    int minGroupSize,
    int maxGroupSize,
    int weight
) {}
