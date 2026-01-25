package com.blib.api.common.entity.v1.spawning;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public record BLibEntitySpawnPlacementData<T extends Mob>(
    SpawnPlacementType type,
    Heightmap.Types heightmapType,
    SpawnPlacements.SpawnPredicate<T> spawnPredicate
) {}
