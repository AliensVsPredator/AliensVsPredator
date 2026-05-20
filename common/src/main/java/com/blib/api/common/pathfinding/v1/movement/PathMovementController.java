package com.blib.api.common.pathfinding.v1.movement;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorApi;

/**
 * Applies {@link PathNavigatorApi} waypoints to Minecraft mob movement controls.
 */
public final class PathMovementController {

    /**
     * Moves a mob toward the supplied navigator waypoint, applying terrain-specific execution behavior when needed.
     */
    public static void follow(
        PathfinderMob actor,
        PathNavigatorApi navigator,
        Vec3 waypointCenter,
        double speedMultiplier
    ) {
        var resolvedSpeedMultiplier = WaterPathMovementController.resolveSpeedMultiplier(
            actor,
            navigator,
            speedMultiplier
        );

        actor.getMoveControl()
            .setWantedPosition(
                waypointCenter.x,
                waypointCenter.y,
                waypointCenter.z,
                resolvedSpeedMultiplier
            );
        WaterPathMovementController.apply(actor, navigator, waypointCenter, resolvedSpeedMultiplier);
    }

    private PathMovementController() {
        throw new UnsupportedOperationException();
    }
}
