package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A {@link MoveControl} that handles 3D movement on walls and ceilings.
 * <p>
 * When the entity's {@link PathNavigator} reports {@link TerrainType#CLIMBABLE} terrain, this control cancels gravity,
 * computes 3D velocity toward the waypoint, and applies a sticking force toward the climbing surface. When on any other
 * terrain, it delegates to vanilla ground movement.
 * </p>
 * <p>
 * The entity must implement {@link PathNavigatorUser}. The navigator is resolved lazily each tick to avoid constructor
 * ordering issues.
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>{@code
 *
 * var moveControl = new ClimbingMoveControl(mob);
 *
 * // or with custom parameters:
 * var moveControl = new ClimbingMoveControl(mob, 0.05f, 0.8f);
 * }</pre>
 */
public class ClimbingMoveControl extends MoveControl {

    private static final float DEFAULT_STICKING_FORCE = 0.05f;

    private static final float DEFAULT_CLIMBING_SPEED_MULTIPLIER = 0.8f;

    private static final float CLIMBING_DRAG = 0.5f;

    private static final float ARRIVAL_THRESHOLD = 0.25f;

    private static final float YAW_ROTATION_SPEED = 90.0f;

    private final float stickingForce;

    private final float climbingSpeedMultiplier;

    private boolean wasClimbing;

    public ClimbingMoveControl(Mob mob) {
        this(mob, DEFAULT_STICKING_FORCE, DEFAULT_CLIMBING_SPEED_MULTIPLIER);
    }

    public ClimbingMoveControl(Mob mob, float stickingForce, float climbingSpeedMultiplier) {
        super(mob);
        this.stickingForce = stickingForce;
        this.climbingSpeedMultiplier = climbingSpeedMultiplier;
    }

    @Override
    public void tick() {
        var navigator = resolveNavigator();

        if (navigator == null || navigator.getCurrentTerrain() != TerrainType.CLIMBABLE) {
            if (wasClimbing) {
                mob.setNoGravity(false);
                wasClimbing = false;
            }

            super.tick();
            return;
        }

        if (!wasClimbing) {
            mob.setNoGravity(true);
            wasClimbing = true;
        }

        tickClimbing(navigator);
    }

    private void tickClimbing(PathNavigator navigator) {
        mob.setXxa(0);
        mob.setZza(0);

        if (operation != Operation.MOVE_TO) {
            mob.setDeltaMovement(mob.getDeltaMovement().scale(CLIMBING_DRAG));
            return;
        }

        operation = Operation.WAIT;

        var dx = wantedX - mob.getX();
        var dy = wantedY - mob.getY();
        var dz = wantedZ - mob.getZ();
        var distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared < ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD) {
            mob.setDeltaMovement(mob.getDeltaMovement().scale(CLIMBING_DRAG));
            return;
        }

        var distance = Math.sqrt(distanceSquared);
        var speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier * climbingSpeedMultiplier;

        var velocityX = (dx / distance) * speed;
        var velocityY = (dy / distance) * speed;
        var velocityZ = (dz / distance) * speed;

        mob.setDeltaMovement(velocityX, velocityY, velocityZ);

        var horizontalDistanceSquared = dx * dx + dz * dz;

        if (horizontalDistanceSquared > 0.001) {
            var targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;

            mob.setYRot(rotlerp(mob.getYRot(), targetYaw, YAW_ROTATION_SPEED));
        }

        applySurfaceStickingForce(navigator);
    }

    private void applySurfaceStickingForce(PathNavigator navigator) {
        var surfaceOrdinal = navigator.getCurrentSurfaceDirection();
        var surface = Direction.values()[surfaceOrdinal];

        mob.setDeltaMovement(
            mob.getDeltaMovement()
                .add(
                    surface.getStepX() * stickingForce,
                    surface.getStepY() * stickingForce,
                    surface.getStepZ() * stickingForce
                )
        );
    }

    private PathNavigator resolveNavigator() {
        if (mob instanceof PathNavigatorUser user) {
            return user.getPathNavigator();
        }

        return null;
    }
}
