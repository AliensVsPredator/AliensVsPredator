package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A unified {@link MoveControl} for entities using BLib's {@link PathNavigator}. Handles ground walking, wall/ceiling
 * climbing, and idle surface attachment without delegating to vanilla's move control. Reads movement targets directly
 * from the navigator's path nodes rather than relying on the {@link #setWantedPosition} intermediary.
 * <p>
 * The entity must implement {@link PathNavigatorUser}. Climbing orientation is synced to clients via
 * {@link ClimbingOrientationProvider} if the entity implements it.
 * </p>
 */
public class ClimbingMoveControl extends MoveControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClimbingMoveControl.class);

    private static final int LOG_INTERVAL_TICKS = 20;

    private static final float DEFAULT_CLIMBING_SPEED_MULTIPLIER = 0.8f;

    private static final float ARRIVAL_THRESHOLD = 0.25f;

    private static final float YAW_ROTATION_SPEED = 90.0f;

    private static final float MAX_YAW_CHANGE_PER_TICK = 90.0f;

    private final float climbingSpeedMultiplier;

    private boolean wasClimbing;

    private @Nullable Direction activeSurface;

    private boolean nearEdgeTransition;

    private int tickCounter;

    public ClimbingMoveControl(Mob mob) {
        this(mob, DEFAULT_CLIMBING_SPEED_MULTIPLIER);
    }

    public ClimbingMoveControl(Mob mob, float climbingSpeedMultiplier) {
        super(mob);
        this.climbingSpeedMultiplier = climbingSpeedMultiplier;
    }

    /**
     * Returns the surface the entity is currently attached to, or {@code null} if the entity is not climbing. Set
     * during {@link #tick()} before {@code travel()} runs each server tick.
     */
    public @Nullable Direction getActiveSurface() {
        return activeSurface;
    }

    public boolean isNearEdgeTransition() {
        return nearEdgeTransition;
    }

    @Override
    public void tick() {
        tickCounter++;

        var navigator = resolveNavigator();
        var terrain = navigator != null ? navigator.getCurrentTerrain() : null;
        var isClimbingTerrain = terrain == TerrainType.CLIMBABLE;
        var physicalSurface = wasClimbing ? findPhysicalSurface() : null;
        var onSurface = physicalSurface != null;

        updateClimbingSurface(navigator, isClimbingTerrain, physicalSurface);
        updateDebugWaypoints(navigator);

        if (isClimbingTerrain) {
            wasClimbing = true;

            var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

            activeSurface = surfaceOrdinal > 0 ? Direction.values()[surfaceOrdinal] : physicalSurface;
            nearEdgeTransition = isNearEdgeTransition(navigator);

            maintainClimbingPosture(navigator);
            tickClimbingMovement(navigator);
        } else if (onSurface) {
            wasClimbing = true;
            activeSurface = physicalSurface;
            nearEdgeTransition = true;

            maintainClimbingPosture(navigator);

            if (navigator != null) {
                tickClimbingMovement(navigator);
            }
        } else if (wasClimbing && mob.onGround()) {
            wasClimbing = false;
            activeSurface = null;
            nearEdgeTransition = false;

            resetClimbingPosture(navigator);
            tickGroundMovement();
        } else {
            activeSurface = null;
            nearEdgeTransition = false;

            tickGroundMovement();
        }
    }

    // --- Ground movement ---

    private void tickGroundMovement() {
        mob.setXxa(0);
        mob.setZza(0);

        if (operation != Operation.MOVE_TO) {
            return;
        }

        operation = Operation.WAIT;

        var dx = wantedX - mob.getX();
        var dz = wantedZ - mob.getZ();
        var dy = wantedY - mob.getY();
        var horizontalDistanceSqr = dx * dx + dz * dz;

        if (horizontalDistanceSqr + dy * dy < MIN_SPEED_SQR) {
            mob.setZza(0);
            return;
        }

        var targetYaw = (float) (Mth.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;

        mob.setYRot(rotlerp(mob.getYRot(), targetYaw, MAX_YAW_CHANGE_PER_TICK));

        var speed = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED));

        mob.setSpeed(speed);
        mob.setZza(speed);

        var blockPos = mob.blockPosition();
        var blockState = mob.level().getBlockState(blockPos);
        var collisionShape = blockState.getCollisionShape(mob.level(), blockPos);

        var needsJump = (dy > mob.maxUpStep() && horizontalDistanceSqr < Math.max(1.0F, mob.getBbWidth()))
            || (!collisionShape.isEmpty()
                && mob.getY() < collisionShape.max(Direction.Axis.Y) + blockPos.getY()
                && !blockState.is(BlockTags.DOORS)
                && !blockState.is(BlockTags.FENCES));

        if (needsJump && mob.onGround()) {
            mob.getJumpControl().jump();
        }
    }

    // --- Climbing movement ---

    private void tickClimbingMovement(PathNavigator navigator) {
        mob.setXxa(0);
        mob.setZza(0);

        if (operation != Operation.MOVE_TO) {
            if (tickCounter % LOG_INTERVAL_TICKS == 0) {
                LOGGER.info(
                    "[ClimbTick] {} IDLE nav.isNavigating={}",
                    mob.getName().getString(),
                    navigator.isNavigating()
                );
            }

            return;
        }

        operation = Operation.WAIT;

        var target = computeSurfaceTarget(navigator);
        var dx = target.x - mob.getX();
        var dy = target.y - mob.getY();
        var dz = target.z - mob.getZ();
        var distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared < ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD) {
            return;
        }

        var distance = Math.sqrt(distanceSquared);
        var speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier * climbingSpeedMultiplier;

        mob.setDeltaMovement(
            (dx / distance) * speed,
            (dy / distance) * speed,
            (dz / distance) * speed
        );

        updateClimbingYaw(navigator, dx, dy, dz);

        if (tickCounter % LOG_INTERVAL_TICKS == 0) {
            var path = navigator.getCurrentPath();

            LOGGER.info(
                "[ClimbTick] {} MOVING dist={} speed={} edgeTrans={} node={}/{} surface={}",
                mob.getName().getString(),
                String.format("%.3f", distance),
                String.format("%.4f", speed),
                nearEdgeTransition,
                path != null ? path.getCurrentNodeIndex() : -1,
                path != null ? path.getNodeCount() : -1,
                navigator.getCurrentSurfaceDirection()
            );
            LOGGER.info(
                "[ClimbTick]   entityPos=({}, {}, {}) target=({}, {}, {})",
                String.format("%.2f", mob.getX()),
                String.format("%.2f", mob.getY()),
                String.format("%.2f", mob.getZ()),
                String.format("%.2f", target.x),
                String.format("%.2f", target.y),
                String.format("%.2f", target.z)
            );
        }
    }

    // --- Surface and posture management ---

    private void updateClimbingSurface(PathNavigator navigator, boolean isClimbingTerrain, Direction physicalSurface) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        if (isClimbingTerrain) {
            var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

            if (surfaceOrdinal > 0) {
                var surface = Direction.values()[surfaceOrdinal];
                var entityPos = mob.blockPosition();

                if (mob.level().getBlockState(entityPos.relative(surface)).isSolid()) {
                    provider.setClimbingSurfaceDirection(surfaceOrdinal);
                    return;
                }
            }
        }

        if (physicalSurface != null) {
            provider.setClimbingSurfaceDirection(physicalSurface.ordinal());
        } else {
            provider.setClimbingSurfaceDirection(0);
        }
    }

    private void maintainClimbingPosture(PathNavigator navigator) {
        if (navigator == null) {
            return;
        }

        var climbingPosture = navigator.getConfig().getEvaluatorConfig().getClimbingPostureIndex();

        if (climbingPosture >= 0) {
            navigator.getConfig().firePostureEnter(climbingPosture);
        }
    }

    private void resetClimbingPosture(PathNavigator navigator) {
        if (navigator == null) {
            return;
        }

        navigator.getConfig().firePostureEnter(0);
    }

    // --- Physical surface detection ---

    private Direction findPhysicalSurface() {
        var entityPos = mob.blockPosition();

        for (var direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }

            if (mob.level().getBlockState(entityPos.relative(direction)).isSolid()) {
                return direction;
            }
        }

        return null;
    }

    // --- Climbing target computation ---

    private Vec3 computeSurfaceTarget(PathNavigator navigator) {
        var node = navigator.getCurrentNode();

        if (node == null || node.getTerrainType() != TerrainType.CLIMBABLE) {
            return new Vec3(wantedX, wantedY, wantedZ);
        }

        var surface = Direction.values()[node.getSurfaceDirection()];
        var opposite = surface.getOpposite();
        var halfWidth = mob.getBbWidth() / 2.0;
        var offset = 0.5 - halfWidth;

        return new Vec3(
            node.getX() + 0.5 + opposite.getStepX() * offset,
            node.getY() + 0.5 + opposite.getStepY() * offset,
            node.getZ() + 0.5 + opposite.getStepZ() * offset
        );
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

    // --- Climbing yaw ---

    private static final float YAW_THRESHOLD = 0.0001f;

    /**
     * Updates the climbing yaw on the {@link ClimbingOrientationProvider}. Transforms the movement direction through
     * the inverse of the renderer's orientation rotation and extracts the yaw from the result.
     */
    private void updateClimbingYaw(PathNavigator navigator, double dx, double dy, double dz) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        provider.setClimbingYawOld(provider.getClimbingYaw());

        var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

        if (surfaceOrdinal <= 0) {
            return;
        }

        if (dx * dx + dy * dy + dz * dz <= YAW_THRESHOLD) {
            return;
        }

        var surface = Direction.values()[surfaceOrdinal];
        var normal = surface.getOpposite().step();
        float normalX = normal.x;
        float normalY = normal.y;
        float normalZ = normal.z;

        var orientationYaw = (float) Math.toDegrees(Mth.atan2(normalX, normalZ));
        var yawRad = Math.toRadians(orientationYaw);
        var recomputedZ = (float) (Math.sin(yawRad) * normalX + Math.cos(yawRad) * normalZ);
        var recomputedY = normalY;
        var recomputedX = (float) (Math.sin(yawRad - Math.PI / 2) * normalX + Math.cos(yawRad - Math.PI / 2) * normalZ);

        var horizontalLength = Mth.sqrt(recomputedX * recomputedX + recomputedZ * recomputedZ);
        var orientationPitch = (float) Math.toDegrees(Mth.atan2(horizontalLength, recomputedY));
        var roll = Math.signum(0.5f - recomputedY - recomputedZ - recomputedX) * orientationYaw;

        var vx = (float) dx;
        var vy = (float) dy;
        var vz = (float) dz;

        // Step 1: YP(-yaw)
        var cosY = Mth.cos((float) Math.toRadians(-orientationYaw));
        var sinY = Mth.sin((float) Math.toRadians(-orientationYaw));
        var rx = vx * cosY + vz * sinY;
        var ry = vy;
        var rz = -vx * sinY + vz * cosY;

        // Step 2: XP(-pitch)
        var cosP = Mth.cos((float) Math.toRadians(-orientationPitch));
        var sinP = Mth.sin((float) Math.toRadians(-orientationPitch));
        var px = rx;
        var pz = ry * sinP + rz * cosP;

        // Step 3: YP(-roll)
        var cosR = Mth.cos((float) Math.toRadians(-roll));
        var sinR = Mth.sin((float) Math.toRadians(-roll));
        var fx = px * cosR + pz * sinR;
        var fz = -px * sinR + pz * cosR;

        var targetYaw = (float) Math.toDegrees(Mth.atan2(-fx, fz));

        provider.setClimbingYaw(rotlerp(provider.getClimbingYaw(), targetYaw, YAW_ROTATION_SPEED));
    }

    // --- Debug ---

    private void updateDebugWaypoints(PathNavigator navigator) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        if (navigator == null || !navigator.isNavigating()) {
            provider.setDebugCurrentWaypoint(0);
            provider.setDebugTargetPos(0);
            return;
        }

        var waypoint = navigator.getCurrentTargetPos();
        provider.setDebugCurrentWaypoint(waypoint != null ? waypoint.asLong() : 0);

        var target = navigator.getTargetPos();
        provider.setDebugTargetPos(target != null ? target.asLong() : 0);
    }

    // --- Utilities ---

    private PathNavigator resolveNavigator() {
        if (mob instanceof PathNavigatorUser user) {
            return user.getPathNavigator();
        }

        return null;
    }
}
