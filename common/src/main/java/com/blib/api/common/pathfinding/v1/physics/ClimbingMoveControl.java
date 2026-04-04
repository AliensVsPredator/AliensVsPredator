package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A {@link MoveControl} that handles 3D movement on walls and ceilings.
 * <p>
 * Computes surface-relative target positions based on the entity's bounding box width, ensuring the entity doesn't
 * target unreachable block centers inside walls. Detects edge transitions (surface changes) and temporarily disables
 * sticking force so the entity can clear corners and ledges.
 * </p>
 * <p>
 * This control only handles physics (gravity, velocity, sticking force). Posture and animation are managed by the
 * pathfinding posture system via
 * {@link com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluatorConfig.Builder#withClimbingPostureIndex(int)}.
 * </p>
 * <p>
 * The entity must implement {@link PathNavigatorUser}. The navigator is resolved lazily each tick to avoid constructor
 * ordering issues.
 * </p>
 */
public class ClimbingMoveControl extends MoveControl {

    private static final float DEFAULT_STICKING_FORCE = 0.05f;

    private static final float DEFAULT_CLIMBING_SPEED_MULTIPLIER = 0.8f;

    private static final float CLIMBING_DRAG = 0.5f;

    private static final float ARRIVAL_THRESHOLD = 0.25f;

    private static final float YAW_ROTATION_SPEED = 90.0f;

    private static final int TRANSITION_GRACE_TICKS = 10;

    private final float stickingForce;

    private final float climbingSpeedMultiplier;

    private boolean wasClimbing;

    private int climbingGraceTicks;

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
        var terrainIsClimbable = navigator != null && navigator.getCurrentTerrain() == TerrainType.CLIMBABLE;

        if (terrainIsClimbable) {
            climbingGraceTicks = TRANSITION_GRACE_TICKS;
        } else if (climbingGraceTicks > 0 && mob.onGround()) {
            climbingGraceTicks--;
        }

        var shouldClimb = terrainIsClimbable || climbingGraceTicks > 0;

        if (!shouldClimb) {
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

        tickClimbing(navigator, terrainIsClimbable);
    }

    private void tickClimbing(PathNavigator navigator, boolean terrainIsClimbable) {
        mob.setXxa(0);
        mob.setZza(0);

        if (operation != Operation.MOVE_TO) {
            mob.setDeltaMovement(mob.getDeltaMovement().scale(CLIMBING_DRAG));
            return;
        }

        operation = Operation.WAIT;

        var target = terrainIsClimbable
            ? computeSurfaceTarget(navigator)
            : new Vec3(wantedX, wantedY, wantedZ);

        var dx = target.x - mob.getX();
        var dy = target.y - mob.getY();
        var dz = target.z - mob.getZ();
        var distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared < ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD) {
            mob.setDeltaMovement(mob.getDeltaMovement().scale(CLIMBING_DRAG));
            return;
        }

        var distance = Math.sqrt(distanceSquared);
        var speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier * climbingSpeedMultiplier;

        mob.setDeltaMovement(
            (dx / distance) * speed,
            (dy / distance) * speed,
            (dz / distance) * speed
        );

        updateYaw(dx, dz);

        if (terrainIsClimbable && !isNearEdgeTransition(navigator)) {
            applySurfaceStickingForce(navigator);
        }
    }

    private void updateYaw(double dx, double dz) {
        var horizontalDistanceSquared = dx * dx + dz * dz;

        if (horizontalDistanceSquared > 0.001) {
            var targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;

            mob.setYRot(rotlerp(mob.getYRot(), targetYaw, YAW_ROTATION_SPEED));
        }
    }

    private Vec3 computeSurfaceTarget(PathNavigator navigator) {
        var node = navigator.getCurrentNode();

        if (node == null || node.getTerrainType() != TerrainType.CLIMBABLE) {
            return new Vec3(wantedX, wantedY, wantedZ);
        }

        var surface = Direction.values()[node.getSurfaceDirection()];
        var opposite = surface.getOpposite();
        var halfWidth = mob.getBbWidth() / 2.0;
        var offset = 0.5 - halfWidth;

        var targetX = node.getX() + 0.5 + opposite.getStepX() * offset;
        var targetY = node.getY() + 0.5 + opposite.getStepY() * offset;
        var targetZ = node.getZ() + 0.5 + opposite.getStepZ() * offset;

        return new Vec3(targetX, targetY, targetZ);
    }

    private boolean isNearEdgeTransition(PathNavigator navigator) {
        var path = navigator.getCurrentPath();

        if (path == null) {
            return false;
        }

        var nextIndex = path.getCurrentNodeIndex() + 1;

        if (nextIndex >= path.getNodeCount()) {
            return true;
        }

        var currentNode = path.getCurrentNode();
        var nextNode = path.getNode(nextIndex);

        return nextNode.getTerrainType() != TerrainType.CLIMBABLE
            || nextNode.getSurfaceDirection() != currentNode.getSurfaceDirection();
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
