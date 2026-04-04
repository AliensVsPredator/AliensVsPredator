package com.blib.api.common.pathfinding.v1.physics;

/**
 * Implemented by entities that support climbing orientation. Provides the current climbing surface direction as a
 * synced value for client-side rendering. The renderer rotates the entity model to match the surface the entity is
 * clinging to.
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
}
