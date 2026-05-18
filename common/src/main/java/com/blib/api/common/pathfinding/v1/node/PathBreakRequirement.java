package com.blib.api.common.pathfinding.v1.node;

import java.util.Objects;

/**
 * A vertical block column that must be cleared before occupying a path node.
 *
 * @param x      volume origin x
 * @param y      volume origin y
 * @param z      volume origin z
 * @param height vertical block count from the origin
 * @param order  preferred order for clearing solid blocks in this column
 */
public record PathBreakRequirement(
    int x,
    int y,
    int z,
    int height,
    PathBreakOrder order
) {

    public PathBreakRequirement(int x, int y, int z, int height) {
        this(x, y, z, height, PathBreakOrder.BOTTOM_UP);
    }

    public PathBreakRequirement {
        if (height < 1) {
            throw new IllegalArgumentException("height must be positive");
        }

        order = Objects.requireNonNull(order, "order");
    }
}
