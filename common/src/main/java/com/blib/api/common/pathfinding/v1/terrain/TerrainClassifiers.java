package com.blib.api.common.pathfinding.v1.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * Default {@link TerrainClassifier} implementations.
 */
public final class TerrainClassifiers {

    /**
     * Classifies positions for ground-only pathfinding. A position is GROUND if the block at feet level is passable and
     * the block below is solid.
     */
    public static final TerrainClassifier GROUND_ONLY = TerrainClassifiers::classifyGroundOnly;

    /**
     * Classifies positions for ground and water pathfinding. Checks WATER first (fluid blocks), then GROUND (passable
     * above solid).
     */
    public static final TerrainClassifier GROUND_AND_WATER = TerrainClassifiers::classifyGroundAndWater;

    private static @Nullable TerrainType classifyGroundOnly(LevelReader level, BlockPos pos) {
        return classifyAsGround(level, pos);
    }

    private static @Nullable TerrainType classifyGroundAndWater(LevelReader level, BlockPos pos) {
        var waterResult = classifyAsWater(level, pos);

        if (waterResult != null) {
            return waterResult;
        }

        return classifyAsGround(level, pos);
    }

    private static @Nullable TerrainType classifyAsGround(LevelReader level, BlockPos pos) {
        var stateAtFeet = level.getBlockState(pos);
        var stateBelow = level.getBlockState(pos.below());

        if (!stateAtFeet.isSolid() && !stateAtFeet.liquid() && stateBelow.isSolid() && !stateBelow.liquid()) {
            return TerrainType.GROUND;
        }

        return null;
    }

    private static @Nullable TerrainType classifyAsWater(LevelReader level, BlockPos pos) {
        var fluidState = level.getFluidState(pos);

        if (!fluidState.isEmpty()) {
            return TerrainType.WATER;
        }

        return null;
    }

    private TerrainClassifiers() {
        throw new UnsupportedOperationException();
    }
}
