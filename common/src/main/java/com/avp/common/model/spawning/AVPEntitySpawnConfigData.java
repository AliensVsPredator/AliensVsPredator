package com.avp.common.model.spawning;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import com.avp.common.config.AVPConfig;

public record AVPEntitySpawnConfigData(
    TagKey<Biome> biomeTagKey,
    AVPConfig.SpawnConfigs.SpawnSettings spawnSettings
) {}
