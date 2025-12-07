package com.avp.common.model.spawning;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record AVPEntitySpawnConfigData(
    TagKey<Biome> biomeTagKey,
    SpawnSettings spawnSettings
) {}
