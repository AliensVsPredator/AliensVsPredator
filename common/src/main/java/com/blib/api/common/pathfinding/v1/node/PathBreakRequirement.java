package com.blib.api.common.pathfinding.v1.node;

/**
 * A vertical block column that must be cleared before occupying a path node.
 *
 * @param x      volume origin x
 * @param y      volume origin y
 * @param z      volume origin z
 * @param height vertical block count from the origin
 */
public record PathBreakRequirement(
    int x,
    int y,
    int z,
    int height
) {}
