package com.blib.api.common.pathfinding.v1.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * Classifies a block position into a {@link TerrainType} for pathfinding purposes. Returns null if the position is
 * impassable (no terrain type applies).
 */
@FunctionalInterface
public interface TerrainClassifier {

    @Nullable
    TerrainType classify(LevelReader level, BlockPos pos);
}
