package com.blib.common.gameplay.model.spawning;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BLibEntitySpawnConfigData(
    TagKey<Biome> biomeTagKey,
    SpawnSettings spawnSettings
) {}
