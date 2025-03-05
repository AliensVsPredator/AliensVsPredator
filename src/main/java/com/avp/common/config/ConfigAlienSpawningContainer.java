package com.avp.common.config;

import com.avp.common.config.property.ConfigPropertyKey;

public record ConfigAlienSpawningContainer(
    ConfigMobSpawningContainer mobSpawning,
    ConfigPropertyKey<Boolean> requiresResin
) {}
