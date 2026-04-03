package com.blib.api.common.pathfinding.v1.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * Default {@link TerrainClassifier} implementations.
 */
public final class TerrainClassifiers {

    /**
     * Classifies positions for ground-only pathfinding.
     * A position is GROUND if the block at feet level is passable and the block below is solid.
     */
    public static final TerrainClassifier GROUND_ONLY = TerrainClassifiers::classifyGround;

    private static @Nullable TerrainType classifyGround(LevelReader level, BlockPos pos) {
        var stateAtFeet = level.getBlockState(pos);
        var stateBelow = level.getBlockState(pos.below());

        if (!stateAtFeet.isSolid() && !stateAtFeet.liquid() && stateBelow.isSolid() && !stateBelow.liquid()) {
            return TerrainType.GROUND;
        }

        return null;
    }

    private TerrainClassifiers() {
        throw new UnsupportedOperationException();
    }
}
