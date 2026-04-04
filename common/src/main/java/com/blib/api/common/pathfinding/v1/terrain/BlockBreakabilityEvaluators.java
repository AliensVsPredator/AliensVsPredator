package com.blib.api.common.pathfinding.v1.terrain;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Default {@link BlockBreakabilityEvaluator} implementations.
 */
public final class BlockBreakabilityEvaluators {

    /**
     * Creates an evaluator that considers blocks breakable if their destroy time is non-negative and below the given
     * threshold. Cost is normalized to 0-1 using maxDestroyTime as the ceiling, keeping break costs comparable to
     * movement costs. The BREAKABLE terrain cost multiplier on the entity's config controls the overall weight of
     * breaking vs walking around.
     * <p>
     * Blocks with block entities or indestructible blocks (-1 destroy time) are excluded.
     * </p>
     */
    public static BlockBreakabilityEvaluator defaultEvaluator(float maxDestroyTime) {
        return (level, pos, state) -> {
            var destroyTime = state.getBlock().defaultDestroyTime();

            if (destroyTime < 0 || destroyTime >= maxDestroyTime || state.hasBlockEntity()) {
                return BlockBreakabilityEvaluator.Result.NOT_BREAKABLE;
            }

            var cost = Math.max(0.01f, destroyTime / maxDestroyTime);

            return new BlockBreakabilityEvaluator.Result(true, cost);
        };
    }

    /**
     * Wraps an existing evaluator to exclude blocks matching a tag.
     */
    public static BlockBreakabilityEvaluator withExcludedTag(
        BlockBreakabilityEvaluator base,
        TagKey<Block> excludedTag
    ) {
        return (level, pos, state) -> {
            if (state.is(excludedTag)) {
                return BlockBreakabilityEvaluator.Result.NOT_BREAKABLE;
            }

            return base.evaluate(level, pos, state);
        };
    }

    private BlockBreakabilityEvaluators() {
        throw new UnsupportedOperationException();
    }
}
