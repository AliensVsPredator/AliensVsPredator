package com.avp.common.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class ExplosionCursor {

    private final Explosion explosion;

    private final Direction direction;

    private int x;

    private int y;

    private int depth;

    public ExplosionCursor(Explosion explosion, Direction direction) {
        this.explosion = explosion;
        this.direction = direction;
    }

    public BlockPos next() {
        var centerPos = explosion.config().centerBlockPosition();
        var wallCenterPos = centerPos.relative(direction, depth);

        var wallTopLeftCorner = wallCenterPos.relative(getPerpendicularDirection1(), depth)
            .relative(getPerpendicularDirection2(), depth);

        var currentPos = wallTopLeftCorner.offset(getXOffset(), getYOffset(), getZOffset());

        advanceCursor();

        return currentPos;
    }

    private void advanceCursor() {
        x++;

        if (x > depth * 2) {
            x = 0;
            y++;
        }

        if (y > depth * 2) {
            y = 0;
            depth++;
        }
    }

    public boolean canExpandFurther() {
        return depth < explosion.config().directionToRadiusMap().get(direction);
    }

    private Direction getPerpendicularDirection1() {
        return switch (direction) {
            case NORTH, SOUTH -> Direction.WEST;
            case EAST, WEST -> Direction.UP;
            case UP, DOWN -> Direction.NORTH;
        };
    }

    private Direction getPerpendicularDirection2() {
        return switch (direction) {
            case NORTH, SOUTH -> Direction.UP;
            case EAST, WEST -> Direction.NORTH;
            case UP, DOWN -> Direction.WEST;
        };
    }

    private int getXOffset() {
        return switch (direction) {
            case NORTH, SOUTH -> x;
            case EAST, WEST -> 0;
            case UP, DOWN -> x;
        };
    }

    private int getYOffset() {
        return switch (direction) {
            case NORTH, SOUTH, EAST, WEST -> -y;
            case UP, DOWN -> 0;
        };
    }

    private int getZOffset() {
        return switch (direction) {
            case NORTH, SOUTH -> 0;
            case EAST, WEST -> x;
            case UP, DOWN -> y;
        };
    }
}
