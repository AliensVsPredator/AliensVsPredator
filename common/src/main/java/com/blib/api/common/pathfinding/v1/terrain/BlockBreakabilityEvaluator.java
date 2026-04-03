package com.blib.api.common.pathfinding.v1.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Determines whether a solid block can be broken by an entity for pathfinding purposes,
 * and the traversal cost of doing so. Higher cost means the pathfinder only chooses
 * to break the block when no cheaper alternative path exists.
 */
@FunctionalInterface
public interface BlockBreakabilityEvaluator {

    Result evaluate(LevelReader level, BlockPos pos, BlockState state);

    record Result(boolean canBreak, float cost) {

        public static final Result NOT_BREAKABLE = new Result(false, Float.MAX_VALUE);
    }
}
