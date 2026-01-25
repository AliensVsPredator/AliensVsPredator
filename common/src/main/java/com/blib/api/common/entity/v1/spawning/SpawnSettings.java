package com.blib.api.common.entity.v1.spawning;

public record SpawnSettings(
    boolean enabled,
    int minGroupSize,
    int maxGroupSize,
    int weight
) {}
