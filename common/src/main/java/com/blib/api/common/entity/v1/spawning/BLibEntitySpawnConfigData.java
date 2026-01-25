package com.blib.api.common.entity.v1.spawning;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BLibEntitySpawnConfigData(
    TagKey<Biome> biomeTagKey,
    SpawnSettings spawnSettings
) {}
