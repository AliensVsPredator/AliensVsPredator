package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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

    private static final float DEFAULT_CLIMBING_SPEED_MULTIPLIER = 0.8f;

    private static final float ARRIVAL_THRESHOLD = 0.25f;

    private static final float YAW_ROTATION_SPEED = 90.0f;

    private static final float MAX_YAW_CHANGE_PER_TICK = 90.0f;

    private final float climbingSpeedMultiplier;

    private boolean wasClimbing;

    private @Nullable Direction activeSurface;

    private boolean nearEdgeTransition;

    private boolean surfaceChangedThisTick;

    private int ticksSinceSurfaceChange;

    private @Nullable Direction previousClimbingSurface;

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

    public boolean wasClimbing() {
        return wasClimbing;
    }

    public int getTicksSinceSurfaceChange() {
        return ticksSinceSurfaceChange;
    }

    public String getOperationName() {
        return operation.name();
    }

    public double getSpeedModifier() {
        return speedModifier;
    }

    public float getClimbingSpeedMultiplier() {
        return climbingSpeedMultiplier;
    }

    @Override
    public void tick() {
        surfaceChangedThisTick = false;
        ticksSinceSurfaceChange++;

        var navigator = resolveNavigator();
        var terrain = navigator != null ? navigator.getCurrentTerrain() : null;
        var isClimbingTerrain = terrain == TerrainType.CLIMBABLE;
        var physicalSurface = !mob.onGround() ? findPhysicalSurface() : null;
        var onSurface = physicalSurface != null;

        updateClimbingSurface(navigator, isClimbingTerrain, physicalSurface);
        updateDebugWaypoints(navigator);

        if (isClimbingTerrain) {
            wasClimbing = true;

            var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

            if (surfaceOrdinal > 0) {
                var navSurface = Direction.values()[surfaceOrdinal];

                if (mob.level().getBlockState(mob.blockPosition().relative(navSurface)).isSolid()) {
                    activeSurface = navSurface;
                } else {
                    activeSurface = physicalSurface;
                }
            } else {
                activeSurface = physicalSurface;
            }

            nearEdgeTransition = isNearEdgeTransition(navigator);

            // When on a climbable path but no physical surface (gap between surfaces during
            // transitions), keep climbing physics active to prevent vanilla gravity.
            if (activeSurface == null && surfaceOrdinal > 0) {
                activeSurface = Direction.values()[surfaceOrdinal];
                nearEdgeTransition = true;
            }

            maintainClimbingPosture(navigator);
            tickClimbingMovement(navigator);
        } else if (onSurface) {
            // The path's current node is non-climbable but the entity is still next to a wall.
            // If the navigator is actively heading to a GROUND waypoint, this is a climb → walk
            // transition: release the wall so vanilla gravity + ground movement can land the
            // entity on the floor. Otherwise (idle or transitioning between climbable surfaces),
            // keep climbing physics active so the entity stays attached to its current wall.
            if (navigator != null && navigator.isNavigating() && navigator.getCurrentTerrain() == TerrainType.GROUND) {
                wasClimbing = false;
                activeSurface = null;
                nearEdgeTransition = false;

                tickGroundMovement();
            } else {
                wasClimbing = true;
                activeSurface = physicalSurface;
                nearEdgeTransition = true;

                if (navigator != null) {
                    tickClimbingMovement(navigator);
                }
            }
        } else if (wasClimbing && mob.onGround()) {
            wasClimbing = false;
            activeSurface = null;
            nearEdgeTransition = false;

            tickGroundMovement();
        } else {
            wasClimbing = false;
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

        if (mob.isInWater() && dy > 0) {
            var upwardSpeed = speed * 0.5;
            mob.setDeltaMovement(mob.getDeltaMovement().add(0, upwardSpeed, 0));
        } else {
            var needsJump = (dy > mob.maxUpStep() && horizontalDistanceSqr < Math.max(1.0F, mob.getBbWidth()))
                || (!collisionShape.isEmpty()
                    && mob.getY() < collisionShape.max(Direction.Axis.Y) + blockPos.getY()
                    && !blockState.is(BlockTags.DOORS)
                    && !blockState.is(BlockTags.FENCES));

            if (needsJump && mob.onGround()) {
                mob.getJumpControl().jump();
            }
        }
    }

    // --- Climbing movement ---

    private void tickClimbingMovement(PathNavigator navigator) {
        mob.setXxa(0);
        mob.setZza(0);

        if (operation != Operation.MOVE_TO) {
            return;
        }

        operation = Operation.WAIT;

        var path = navigator.getCurrentPath();
        var target = computeSurfaceTarget(navigator);
        var dx = target.x - mob.getX();
        var dy = target.y - mob.getY();
        var dz = target.z - mob.getZ();
        var distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared < ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD) {
            if (path != null && path.getCurrentNodeIndex() + 1 < path.getNodeCount()) {
                var nextNode = path.getNode(path.getCurrentNodeIndex() + 1);

                dx = nextNode.getX() + 0.5 - mob.getX();
                dy = nextNode.getY() - mob.getY();
                dz = nextNode.getZ() + 0.5 - mob.getZ();
                distanceSquared = dx * dx + dy * dy + dz * dz;

                if (distanceSquared < ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD) {
                    return;
                }
            } else {
                return;
            }
        }

        var distance = Math.sqrt(distanceSquared);
        var speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier * climbingSpeedMultiplier;

        mob.setDeltaMovement(
            (dx / distance) * speed,
            (dy / distance) * speed,
            (dz / distance) * speed
        );

        if (dx * dx + dz * dz > YAW_THRESHOLD) {
            var worldYaw = (float) (Mth.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;

            mob.setYRot(rotlerp(mob.getYRot(), worldYaw, MAX_YAW_CHANGE_PER_TICK));
            mob.yBodyRot = mob.getYRot();
            mob.yBodyRotO = mob.yRotO;
        }

        updateClimbingYaw(navigator, dx, dy, dz);
    }

    // --- Surface and posture management ---

    private void updateClimbingSurface(PathNavigator navigator, boolean isClimbingTerrain, Direction physicalSurface) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        var previousSurface = provider.getClimbingSurfaceDirection();
        int newSurface;

        if (isClimbingTerrain) {
            var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

            if (surfaceOrdinal > 0) {
                var surface = Direction.values()[surfaceOrdinal];
                var entityPos = mob.blockPosition();

                if (mob.level().getBlockState(entityPos.relative(surface)).isSolid()) {
                    newSurface = surfaceOrdinal;

                    if (newSurface != previousSurface) {
                        surfaceChangedThisTick = true;
                        ticksSinceSurfaceChange = 0;
                        previousClimbingSurface = previousSurface > 0 ? Direction.values()[previousSurface] : null;
                    }

                    provider.setClimbingSurfaceDirection(newSurface);
                    return;
                }
            }

            // Nav surface not yet reachable — keep previous surface until the entity arrives.
            if (previousSurface > 0) {
                return;
            }
        }

        // Once the entity has landed, clear any leftover climbing surface state. This ensures
        // that after a climb → walk transition the rendering and gameplay state reflect the
        // entity's actual standing posture instead of staying latched to the old wall.
        if (mob.onGround()) {
            if (previousSurface != 0) {
                provider.setClimbingSurfaceDirection(0);
            }
            return;
        }

        if (physicalSurface != null) {
            newSurface = physicalSurface.ordinal();
            provider.setClimbingSurfaceDirection(newSurface);
        } else {
            newSurface = 0;
            provider.setClimbingSurfaceDirection(newSurface);
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
     * Updates the climbing yaw on the {@link ClimbingOrientationProvider}. When the surface changes, converts the
     * previous yaw to the new coordinate system so the visual facing direction is preserved at the transition point.
     */
    private void updateClimbingYaw(PathNavigator navigator, double dx, double dy, double dz) {
        if (!(mob instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        var surfaceOrdinal = navigator.getCurrentSurfaceDirection();

        if (surfaceOrdinal <= 0) {
            provider.setClimbingYawOld(provider.getClimbingYaw());
            return;
        }

        var surface = Direction.values()[surfaceOrdinal];

        if (surfaceChangedThisTick && previousClimbingSurface != null) {
            var converted = convertClimbYaw(provider.getClimbingYaw(), previousClimbingSurface, surface);

            provider.setClimbingYaw(converted);
        }

        provider.setClimbingYawOld(provider.getClimbingYaw());

        if (dx * dx + dy * dy + dz * dz <= YAW_THRESHOLD) {
            return;
        }

        var targetYaw = worldToLocalYaw((float) dx, (float) dy, (float) dz, surface);

        if (surfaceChangedThisTick) {
            provider.setClimbingYaw(targetYaw);
            provider.setClimbingYawOld(targetYaw);
        } else {
            provider.setClimbingYaw(rotlerp(provider.getClimbingYaw(), targetYaw, YAW_ROTATION_SPEED));
        }
    }

    /**
     * Converts a climbYaw from one surface's local coordinate system to another, preserving the world-space facing
     * direction.
     */
    private static float convertClimbYaw(float climbYaw, Direction oldSurface, Direction newSurface) {
        var worldFacing = localYawToWorld(climbYaw, oldSurface);

        return worldToLocalYaw(worldFacing[0], worldFacing[1], worldFacing[2], newSurface);
    }

    /**
     * Transforms a local climbYaw + surface orientation into a world-space facing direction. This is the forward
     * transform: applies the renderer's rotation chain (orientation yaw, pitch, roll, then body rotation) to the
     * forward vector.
     */
    private static float[] localYawToWorld(float climbYaw, Direction surface) {
        var bodyRad = (float) Math.toRadians(180.0f - climbYaw);
        var localX = Mth.sin(bodyRad);
        var localY = 0.0f;
        var localZ = Mth.cos(bodyRad);

        var orientParams = computeOrientationParams(surface);
        var orientYaw = orientParams[0];
        var orientPitch = orientParams[1];
        var roll = orientParams[2];

        // Apply YP(roll)
        var cosR = Mth.cos((float) Math.toRadians(roll));
        var sinR = Mth.sin((float) Math.toRadians(roll));
        var r1x = localX * cosR + localZ * sinR;
        var r1y = localY;
        var r1z = -localX * sinR + localZ * cosR;

        // Apply XP(orientPitch)
        var cosP = Mth.cos((float) Math.toRadians(orientPitch));
        var sinP = Mth.sin((float) Math.toRadians(orientPitch));
        var r2x = r1x;
        var r2y = r1y * cosP - r1z * sinP;
        var r2z = r1y * sinP + r1z * cosP;

        // Apply YP(orientYaw)
        var cosY = Mth.cos((float) Math.toRadians(orientYaw));
        var sinY = Mth.sin((float) Math.toRadians(orientYaw));
        var worldX = r2x * cosY + r2z * sinY;
        var worldY = r2y;
        var worldZ = -r2x * sinY + r2z * cosY;

        return new float[] { worldX, worldY, worldZ };
    }

    /**
     * Transforms a world-space direction into a local climbYaw for the given surface. This is the inverse transform
     * used by the renderer: applies YP(-orientYaw), XP(-orientPitch), YP(-roll) and extracts the yaw.
     */
    private static float worldToLocalYaw(float dx, float dy, float dz, Direction surface) {
        var orientParams = computeOrientationParams(surface);
        var orientYaw = orientParams[0];
        var orientPitch = orientParams[1];
        var roll = orientParams[2];

        // Step 1: YP(-orientYaw)
        var cosY = Mth.cos((float) Math.toRadians(-orientYaw));
        var sinY = Mth.sin((float) Math.toRadians(-orientYaw));
        var rx = dx * cosY + dz * sinY;
        var ry = dy;
        var rz = -dx * sinY + dz * cosY;

        // Step 2: XP(-orientPitch)
        var cosP = Mth.cos((float) Math.toRadians(-orientPitch));
        var sinP = Mth.sin((float) Math.toRadians(-orientPitch));
        var px = rx;
        var pz = ry * sinP + rz * cosP;

        // Step 3: YP(-roll)
        var cosR = Mth.cos((float) Math.toRadians(-roll));
        var sinR = Mth.sin((float) Math.toRadians(-roll));
        var fx = px * cosR + pz * sinR;
        var fz = -px * sinR + pz * cosR;

        return (float) Math.toDegrees(Mth.atan2(-fx, fz));
    }

    /**
     * Computes the orientation yaw, pitch, and roll for a given surface direction. Shared between the forward and
     * inverse transforms.
     */
    private static float[] computeOrientationParams(Direction surface) {
        var normal = surface.getOpposite().step();
        float normalX = normal.x;
        float normalY = normal.y;
        float normalZ = normal.z;

        var orientYaw = (float) Math.toDegrees(Mth.atan2(normalX, normalZ));
        var yawRad = Math.toRadians(orientYaw);
        var recomputedZ = (float) (Math.sin(yawRad) * normalX + Math.cos(yawRad) * normalZ);
        var recomputedY = normalY;
        var recomputedX = (float) (Math.sin(yawRad - Math.PI / 2) * normalX + Math.cos(yawRad - Math.PI / 2) * normalZ);

        var horizontalLength = Mth.sqrt(recomputedX * recomputedX + recomputedZ * recomputedZ);
        var orientPitch = (float) Math.toDegrees(Mth.atan2(horizontalLength, recomputedY));
        var roll = Math.signum(0.5f - recomputedY - recomputedZ - recomputedX) * orientYaw;

        return new float[] { orientYaw, orientPitch, roll };
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
