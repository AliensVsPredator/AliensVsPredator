package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Optional pathfinding configuration for edges that become traversable only after breaking blocks.
 *
 * @param enabled          whether path search may propose block-breaking edges
 * @param maxBlocksPerEdge maximum number of blocks one path edge may require breaking
 * @param maxHardness      maximum default destroy time accepted by the plan-time policy
 * @param flatCostPerBlock fixed path cost added for each planned broken block
 * @param costPerHardness  path cost multiplier applied to each block's default destroy time
 * @param damagePerTick    intended execution damage rate for later block-breaking executors
 * @param breakPolicy      caller-owned plan-time policy for whether a block may be broken
 */
public record PathBlockBreakingConfig(
    boolean enabled,
    int maxBlocksPerEdge,
    float maxHardness,
    float flatCostPerBlock,
    float costPerHardness,
    float damagePerTick,
    PathBlockBreakPolicy breakPolicy
) {

    public static final PathBlockBreakingConfig DISABLED = new PathBlockBreakingConfig(
        false,
        0,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        PathBlockBreakPolicy.NEVER
    );

    public static PathBlockBreakingConfig enabled(PathBlockBreakPolicy breakPolicy) {
        return new PathBlockBreakingConfig(true, 2, 5.0f, 4.0f, 8.0f, 1.0f, breakPolicy);
    }

    public PathBlockBreakingConfig {
        maxBlocksPerEdge = Math.max(0, maxBlocksPerEdge);
        maxHardness = finiteNonNegative(maxHardness);
        flatCostPerBlock = finiteNonNegative(flatCostPerBlock);
        costPerHardness = finiteNonNegative(costPerHardness);
        damagePerTick = finiteNonNegative(damagePerTick);
        breakPolicy = breakPolicy != null ? breakPolicy : PathBlockBreakPolicy.NEVER;

        if (!enabled) {
            maxBlocksPerEdge = 0;
        }
    }

    public boolean canBreak(LevelReader level, BlockPos pos, BlockState state) {
        if (!enabled || maxBlocksPerEdge <= 0) {
            return false;
        }

        var hardness = state.getBlock().defaultDestroyTime();
        return hardness >= 0.0f && hardness <= maxHardness && breakPolicy.canBreak(level, pos, state);
    }

    public float costFor(BlockState state) {
        var hardness = Math.max(0.0f, state.getBlock().defaultDestroyTime());
        return flatCostPerBlock + hardness * costPerHardness;
    }

    private static float finiteNonNegative(float value) {
        return Float.isFinite(value) ? Math.max(0.0f, value) : 0.0f;
    }
}
