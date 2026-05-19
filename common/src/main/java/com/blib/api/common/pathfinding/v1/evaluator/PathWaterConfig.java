package com.blib.api.common.pathfinding.v1.evaluator;

/**
 * Optional pathfinding posture configuration for entities that use a shorter hitbox while swimming.
 */
public record PathWaterConfig(
    boolean enabled,
    int swimHeight
) {

    public static final PathWaterConfig DISABLED = new PathWaterConfig(false, 1);

    public static PathWaterConfig enabled(int swimHeight) {
        return new PathWaterConfig(true, swimHeight);
    }

    public PathWaterConfig {
        swimHeight = Math.max(1, swimHeight);
    }
}
