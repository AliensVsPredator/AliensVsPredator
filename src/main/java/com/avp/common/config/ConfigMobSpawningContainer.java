package com.avp.common.config;

import com.avp.common.config.property.ConfigPropertyKey;

public record ConfigMobSpawningContainer(
    ConfigPropertyKey<Boolean> enabled,
    ConfigPropertyKey<Integer> maxGroupSize,
    ConfigPropertyKey<Integer> maxY,
    ConfigPropertyKey<Integer> minGroupSize,
    ConfigPropertyKey<Integer> minY,
    ConfigPropertyKey<Integer> weight
) {}
