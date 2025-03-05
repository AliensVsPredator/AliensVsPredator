package com.avp.core.common.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public record ExplosionConfig(
    int blockSampleCountPerCycle,
    Vec3 centerPosition,
    BlockPos centerBlockPosition,
    int cycleDelayInTicks,
    Map<Direction, Integer> directionToRadiusMap
) {

    public int radius(Direction direction) {
        return directionToRadiusMap.get(direction);
    }

    public int largestRadius(Direction.Axis axis) {
        var leftDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        var left = radius(leftDirection);
        var right = radius(leftDirection.getOpposite());

        return Math.max(left, right);
    }

    public int largestRadius(Direction.Plane plane) {
        return directionToRadiusMap.entrySet()
            .stream()
            .filter(entry -> plane.test(entry.getKey()))
            .map(Map.Entry::getValue)
            .max(Integer::compareTo)
            .orElseThrow();
    }

    public int largestRadius() {
        return directionToRadiusMap.values().stream().max(Integer::compareTo).orElseThrow();
    }
}
