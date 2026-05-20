package com.blib.api.common.pathfinding.v1.node;

import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Blocks that must be broken before traversing into a path node from its parent.
 *
 * @param blocks block positions to break before movement into the node
 */
public record PathBlockBreakPlan(List<BlockPos> blocks) {

    public static final PathBlockBreakPlan EMPTY = new PathBlockBreakPlan(List.of());

    public PathBlockBreakPlan {
        if (blocks == null || blocks.isEmpty()) {
            blocks = List.of();
        } else {
            blocks = blocks.stream().map(BlockPos::immutable).toList();
        }
    }

    public static PathBlockBreakPlan of(BlockPos block) {
        return new PathBlockBreakPlan(List.of(block));
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    public int size() {
        return blocks.size();
    }
}
