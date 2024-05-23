package org.avp.common.entity.data.spawn;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public record EntitySpawnData<T extends Mob>(
    TagKey<Biome> biomeTagKey,
    int weight,
    int minGroupSize,
    int maxGroupSize,
    SpawnPlacements.Type spawnPlacementType,
    Heightmap.Types heightMapType,
    SpawnPlacements.SpawnPredicate<T> spawnPredicate
) {}
