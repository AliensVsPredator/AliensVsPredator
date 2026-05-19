package com.blib.api.common.pathfinding.v1.movement;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Applies {@link PathNavigator} waypoints to Minecraft mob movement controls.
 */
public final class PathMovementController {

    private static final double WATER_MOVE_CONTROL_SPEED_SCALE = 1.0;

    private static final double WATER_HORIZONTAL_SPEED_SCALE = 0.70;

    private static final double WATER_MIN_HORIZONTAL_SPEED = 0.04;

    private static final double WATER_MAX_HORIZONTAL_SPEED = 0.18;

    private static final double WATER_HORIZONTAL_ACCELERATION = 0.035;

    private static final double WATER_VERTICAL_SPEED_SCALE = 0.14;

    private static final double WATER_MAX_ASCEND_SPEED = 0.16;

    private static final double WATER_MAX_DESCEND_SPEED = 0.12;

    private static final double WATER_VERTICAL_ACCELERATION = 0.05;

    private static final double WATER_VERTICAL_DEADZONE = 0.12;

    private static final double WATER_VERTICAL_IDLE_DAMPING = 0.65;

    private static final double WATER_EXIT_HORIZONTAL_SPEED = 0.12;

    private static final double WATER_EXIT_MIN_UPWARD_SPEED = 0.20;

    private static final double WATER_EXIT_VERTICAL_ACCELERATION = 0.10;

    private static final double WATER_EXIT_LEVEL_TOLERANCE = 0.35;

    private static final double WATER_STEP_UP_READY_TOLERANCE = 0.06;

    private static final double WATER_STEP_UP_PRE_LIFT_HORIZONTAL_SPEED = 0.02;

    private static final double WATER_STEP_UP_MIN_UPWARD_SPEED = 0.20;

    private static final double WATER_STEP_UP_VERTICAL_ACCELERATION = 0.10;

    private static final double WATER_MIN_HORIZONTAL_DISTANCE_SQUARED = 1.0E-4;

    /**
     * Moves a mob toward the supplied navigator waypoint, applying terrain-specific execution behavior when needed.
     */
    public static void follow(
        PathfinderMob actor,
        PathNavigator navigator,
        Vec3 waypointCenter,
        double speedMultiplier
    ) {
        var resolvedSpeedMultiplier = waterAwareSpeedMultiplier(actor, navigator, speedMultiplier);

        actor.getMoveControl()
            .setWantedPosition(
                waypointCenter.x,
                waypointCenter.y,
                waypointCenter.z,
                resolvedSpeedMultiplier
            );
        applyWaterPathMovement(actor, navigator, waypointCenter, resolvedSpeedMultiplier);
    }

    private static double waterAwareSpeedMultiplier(
        PathfinderMob actor,
        PathNavigator navigator,
        double speedMultiplier
    ) {
        if (!shouldUseWaterMovementAssist(actor, navigator)) {
            return speedMultiplier;
        }

        return speedMultiplier * WATER_MOVE_CONTROL_SPEED_SCALE;
    }

    private static void applyWaterPathMovement(
        PathfinderMob actor,
        PathNavigator navigator,
        Vec3 waypointCenter,
        double speedMultiplier
    ) {
        if (!usesWaterPathMovement(actor, navigator)) {
            return;
        }

        var dx = waypointCenter.x - actor.getX();
        var dy = waypointCenter.y - actor.getY();
        var dz = waypointCenter.z - actor.getZ();
        var horizontalDistanceSquared = dx * dx + dz * dz;
        var features = navigator.getPathfindingFeatures();
        var movementAssist = features.waterMovementAssist();
        var exitingWater = features.waterExitBreach() && isWaterExitMovement(actor, navigator, waypointCenter);
        var preLiftingWaterStepUp = features.waterStepUpPreLift() && isPreLiftingWaterStepUp(actor, navigator, waypointCenter);

        if (!movementAssist && !exitingWater && !preLiftingWaterStepUp) {
            return;
        }

        if (movementAssist) {
            navigator.markPathfindingFeatureUsed(PathfindingFeature.WATER_MOVEMENT_ASSIST);
        }

        if (exitingWater) {
            navigator.markPathfindingFeatureUsed(PathfindingFeature.WATER_EXIT_BREACH);
        }

        if (preLiftingWaterStepUp) {
            navigator.markPathfindingFeatureUsed(PathfindingFeature.WATER_STEP_UP_PRE_LIFT);
        }

        var baseSpeed = actor.getAttributeValue(Attributes.MOVEMENT_SPEED) * Math.max(0.05, speedMultiplier);
        var targetHorizontalSpeed = movementAssist
            ? clamp(baseSpeed * WATER_HORIZONTAL_SPEED_SCALE, WATER_MIN_HORIZONTAL_SPEED, WATER_MAX_HORIZONTAL_SPEED)
            : 0.0;

        if (exitingWater) {
            targetHorizontalSpeed = Math.max(targetHorizontalSpeed, WATER_EXIT_HORIZONTAL_SPEED);
        } else if (preLiftingWaterStepUp) {
            targetHorizontalSpeed = movementAssist
                ? Math.min(targetHorizontalSpeed, WATER_STEP_UP_PRE_LIFT_HORIZONTAL_SPEED)
                : WATER_STEP_UP_PRE_LIFT_HORIZONTAL_SPEED;
        }

        var targetVelocityX = 0.0;
        var targetVelocityZ = 0.0;

        if (horizontalDistanceSquared > WATER_MIN_HORIZONTAL_DISTANCE_SQUARED) {
            var horizontalDistance = Math.sqrt(horizontalDistanceSquared);
            targetVelocityX = dx / horizontalDistance * targetHorizontalSpeed;
            targetVelocityZ = dz / horizontalDistance * targetHorizontalSpeed;
        }

        var delta = actor.getDeltaMovement();
        var nextX = approach(delta.x, targetVelocityX, WATER_HORIZONTAL_ACCELERATION);
        var nextZ = approach(delta.z, targetVelocityZ, WATER_HORIZONTAL_ACCELERATION);
        var targetVelocityY = movementAssist && Math.abs(dy) > WATER_VERTICAL_DEADZONE
            ? clamp(dy * WATER_VERTICAL_SPEED_SCALE, -WATER_MAX_DESCEND_SPEED, WATER_MAX_ASCEND_SPEED)
            : delta.y * (movementAssist ? WATER_VERTICAL_IDLE_DAMPING : 1.0);

        if (exitingWater) {
            targetVelocityY = Math.max(targetVelocityY, WATER_EXIT_MIN_UPWARD_SPEED);
        } else if (preLiftingWaterStepUp) {
            targetVelocityY = Math.max(targetVelocityY, WATER_STEP_UP_MIN_UPWARD_SPEED);
        }

        var verticalAcceleration = exitingWater
            ? WATER_EXIT_VERTICAL_ACCELERATION
            : preLiftingWaterStepUp
                ? WATER_STEP_UP_VERTICAL_ACCELERATION
                : WATER_VERTICAL_ACCELERATION;
        var nextY = approach(delta.y, targetVelocityY, verticalAcceleration);

        actor.setDeltaMovement(nextX, nextY, nextZ);
    }

    private static boolean usesWaterPathMovement(PathfinderMob actor, PathNavigator navigator) {
        return actor.isInWater() && navigator.getPathfindingFeatures().waterPathfinding();
    }

    private static boolean shouldUseWaterMovementAssist(PathfinderMob actor, PathNavigator navigator) {
        return usesWaterPathMovement(actor, navigator) && navigator.getPathfindingFeatures().waterMovementAssist();
    }

    private static boolean isWaterExitMovement(
        PathfinderMob actor,
        PathNavigator navigator,
        Vec3 waypointCenter
    ) {
        var currentNode = navigator.getCurrentNode();

        return currentNode != null
            && currentNode.getTerrainType() != TerrainType.WATER
            && waypointCenter.y >= actor.getY() - WATER_EXIT_LEVEL_TOLERANCE;
    }

    private static boolean isPreLiftingWaterStepUp(
        PathfinderMob actor,
        PathNavigator navigator,
        Vec3 waypointCenter
    ) {
        if (actor.getY() >= waypointCenter.y - WATER_STEP_UP_READY_TOLERANCE) {
            return false;
        }

        var path = navigator.getCurrentPath();

        if (path == null || path.isDone()) {
            return false;
        }

        var currentIndex = path.getCurrentNodeIndex();

        if (currentIndex <= 0 || currentIndex >= path.getNodeCount()) {
            return false;
        }

        var from = path.getNode(currentIndex - 1);
        var to = path.getNode(currentIndex);

        return from.getTerrainType() == TerrainType.WATER
            && to.getTerrainType() == TerrainType.WATER
            && to.getY() > from.getY()
            && (to.getX() != from.getX() || to.getZ() != from.getZ());
    }

    private static double approach(double current, double target, double maxDelta) {
        if (current < target) {
            return Math.min(current + maxDelta, target);
        }

        if (current > target) {
            return Math.max(current - maxDelta, target);
        }

        return current;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private PathMovementController() {
        throw new UnsupportedOperationException();
    }
}
