package com.avp.core.common.config;

import com.avp.core.common.config.property.ConfigPropertyKey;

public record ConfigAlienSpawningContainer(
    ConfigMobSpawningContainer mobSpawning,
    ConfigPropertyKey<Boolean> requiresResin
) {}
