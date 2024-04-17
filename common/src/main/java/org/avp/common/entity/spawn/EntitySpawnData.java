package org.avp.common.entity.spawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import org.avp.api.Holder;

public record EntitySpawnData<T extends Mob>(
    Holder<EntityType<T>> entityTypeHolder,
    int weight,
    int minGroupSize,
    int maxGroupSize,
    SpawnPlacements.Type spawnPlacementType,
    Heightmap.Types heightMapType,
    SpawnPlacements.SpawnPredicate<T> spawnPredicate
) {}
