package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ClimbingMoveControl.class);

    private static final int LOG_INTERVAL_TICKS = 20;

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

    private int tickCounter;

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
        tickCounter++;

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

            updateClimbingSurface(navigator, false);
            super.tick();
            return;
        }

        if (!wasClimbing) {
            mob.setNoGravity(true);
            wasClimbing = true;
        }

        updateClimbingSurface(navigator, terrainIsClimbable);
        tickClimbing(navigator, terrainIsClimbable);
    }

    /**
     * Computes the climbing surface from the entity's actual physical surroundings, not from pathfinding waypoints.
     * Only sets a surface when there's a solid block in the navigator's surface direction from the entity's current
     * position.
     */
    private void updateClimbingSurface(PathNavigator navigator, boolean terrainIsClimbable) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        if (!terrainIsClimbable) {
            provider.setClimbingSurfaceDirection(0);
            return;
        }

        var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

        if (surfaceOrdinal <= 0) {
            provider.setClimbingSurfaceDirection(0);
            return;
        }

        var surface = Direction.values()[surfaceOrdinal];
        var entityPos = mob.blockPosition();

        if (mob.level().getBlockState(entityPos.relative(surface)).isSolid()) {
            provider.setClimbingSurfaceDirection(surfaceOrdinal);
            return;
        }

        // Navigator's surface isn't adjacent — scan for the entity's actual surface.
        for (var direction : Direction.values()) {
            if (mob.level().getBlockState(entityPos.relative(direction)).isSolid()) {
                provider.setClimbingSurfaceDirection(direction.ordinal());
                return;
            }
        }
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

        updateClimbingYaw(navigator, terrainIsClimbable, dx, dy, dz);

        if (terrainIsClimbable && !isNearEdgeTransition(navigator)) {
            applySurfaceStickingForce(navigator);
        }
    }

    private static final float YAW_THRESHOLD = 0.0001f;

    /**
     * Updates the climbing yaw on the {@link ClimbingOrientationProvider}. Transforms the movement direction through
     * the inverse of the renderer's orientation rotation and extracts the yaw from the result. This produces the
     * correct yaw for ANY surface without per-surface special cases.
     */
    private void updateClimbingYaw(
        PathNavigator navigator,
        boolean terrainIsClimbable,
        double dx,
        double dy,
        double dz
    ) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        provider.setClimbingYawOld(provider.getClimbingYaw());

        if (!terrainIsClimbable) {
            return;
        }

        var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

        if (surfaceOrdinal <= 0) {
            return;
        }

        var movementLengthSquared = dx * dx + dy * dy + dz * dz;

        if (movementLengthSquared <= YAW_THRESHOLD) {
            return;
        }

        // Compute the same orientation yaw/pitch/roll that the renderer uses.
        var surface = Direction.values()[surfaceOrdinal];
        var normal = surface.getOpposite().step();
        float normalX = normal.x;
        float normalY = normal.y;
        float normalZ = normal.z;

        var componentZ = normalZ;
        var componentX = normalX;
        var orientationYaw = (float) Math.toDegrees(Mth.atan2(componentX, componentZ));

        var yawRad = Math.toRadians(orientationYaw);
        var recomputedZ = (float) (Math.sin(yawRad) * normalX + Math.cos(yawRad) * normalZ);
        var recomputedY = normalY;
        var recomputedX = (float) (Math.sin(yawRad - Math.PI / 2) * normalX + Math.cos(yawRad - Math.PI / 2) * normalZ);

        var horizontalLength = Mth.sqrt(recomputedX * recomputedX + recomputedZ * recomputedZ);
        var orientationPitch = (float) Math.toDegrees(Mth.atan2(horizontalLength, recomputedY));
        var rollSign = Math.signum(0.5f - recomputedY - recomputedZ - recomputedX);
        var roll = rollSign * orientationYaw;

        // Transform movement through inverse orientation: R^(-1) = YP(-roll) * XP(-pitch) * YP(-yaw).
        // Applied to the vector in order: YP(-yaw) first, XP(-pitch) second, YP(-roll) last.
        var vx = (float) dx;
        var vy = (float) dy;
        var vz = (float) dz;

        // Step 1: YP(-yaw)
        var oYawRad = (float) Math.toRadians(-orientationYaw);
        var cosY = Mth.cos(oYawRad);
        var sinY = Mth.sin(oYawRad);
        var rx = vx * cosY + vz * sinY;
        var ry = vy;
        var rz = -vx * sinY + vz * cosY;

        // Step 2: XP(-pitch)
        var pitchRad = (float) Math.toRadians(-orientationPitch);
        var cosP = Mth.cos(pitchRad);
        var sinP = Mth.sin(pitchRad);
        var px = rx;
        var py = ry * cosP - rz * sinP;
        var pz = ry * sinP + rz * cosP;

        // Step 3: YP(-roll)
        var rollRad = (float) Math.toRadians(-roll);
        var cosR = Mth.cos(rollRad);
        var sinR = Mth.sin(rollRad);
        var fx = px * cosR + pz * sinR;
        var fz = -px * sinR + pz * cosR;

        // Extract yaw: climbingYaw = atan2(-fx, fz) in degrees
        var targetYaw = (float) Math.toDegrees(Mth.atan2(-fx, fz));
        var smoothedYaw = rotlerp(provider.getClimbingYaw(), targetYaw, YAW_ROTATION_SPEED);

        provider.setClimbingYaw(smoothedYaw);
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
