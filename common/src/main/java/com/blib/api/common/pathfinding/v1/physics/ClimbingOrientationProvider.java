package com.blib.api.common.pathfinding.v1.physics;

/**
 * Implemented by entities that support climbing orientation. Provides climbing surface direction and a stable yaw for
 * rendering, both computed by {@link ClimbingMoveControl} from the entity's actual physical surroundings each tick.
 * <p>
 * Surface direction values correspond to {@link net.minecraft.core.Direction} ordinals: 0=DOWN (ground, no rotation),
 * 1=UP (ceiling), 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST.
 * </p>
 */
public interface ClimbingOrientationProvider {

    /**
     * Returns the current climbing surface direction ordinal, or 0 if not climbing.
     */
    int getClimbingSurfaceDirection();

    /**
     * Sets the current climbing surface direction ordinal.
     */
    void setClimbingSurfaceDirection(int surfaceDirection);

    /**
     * Returns the stable climbing yaw for rendering, computed by {@link ClimbingMoveControl}. This value is independent
     * of vanilla's yRot/yBodyRot which can be overridden by LookControl.
     */
    float getClimbingYaw();

    /**
     * Sets the stable climbing yaw.
     */
    void setClimbingYaw(float yaw);

    /**
     * Returns the previous tick's climbing yaw for interpolation.
     */
    float getClimbingYawOld();

    /**
     * Sets the previous tick's climbing yaw.
     */
    void setClimbingYawOld(float yaw);

    /**
     * Returns the packed {@link net.minecraft.core.BlockPos} of the current waypoint for debug rendering, or 0 if none.
     */
    long getDebugCurrentWaypoint();

    /**
     * Sets the packed current waypoint position for debug rendering.
     */
    void setDebugCurrentWaypoint(long packed);

    /**
     * Returns the packed {@link net.minecraft.core.BlockPos} of the final target for debug rendering, or 0 if none.
     */
    long getDebugTargetPos();

    /**
     * Sets the packed final target position for debug rendering.
     */
    void setDebugTargetPos(long packed);

    /**
     * Returns the client-side render state for climbing orientation, or {@code null} if this entity does not support
     * it. Implementing entities should hold a single {@link ClimbingRenderState} instance as a field.
     */
    default ClimbingRenderState getClimbingRenderState() {
        return null;
    }
}
