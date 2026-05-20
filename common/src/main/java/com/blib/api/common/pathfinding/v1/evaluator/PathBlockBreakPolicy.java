package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Plan-time policy for blocks a pathfinding edge may require breaking. Execution code must re-check its own authority
 * before mutating the world.
 */
@FunctionalInterface
public interface PathBlockBreakPolicy {

    PathBlockBreakPolicy NEVER = ($level, $pos, $state) -> false;

    boolean canBreak(LevelReader level, BlockPos pos, BlockState state);
}
