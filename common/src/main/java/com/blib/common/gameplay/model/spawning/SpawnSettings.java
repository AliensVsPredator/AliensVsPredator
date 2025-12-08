package com.blib.common.gameplay.model.spawning;

public record SpawnSettings(
    boolean enabled,
    int minGroupSize,
    int maxGroupSize,
    int weight
) {}
