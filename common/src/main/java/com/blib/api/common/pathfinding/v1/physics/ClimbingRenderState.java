package com.blib.api.common.pathfinding.v1.physics;

import org.jetbrains.annotations.Nullable;

/**
 * Holds all client-side rendering state for a climbing entity. The renderer reads and writes this each frame to compute
 * the climbing orientation matrix. Entities that implement {@link ClimbingOrientationProvider} hold a single instance
 * of this class — no external maps or caches needed, and the state is automatically cleaned up when the entity is
 * removed.
 */
public final class ClimbingRenderState {

    private float @Nullable [] displayedForward;

    public boolean hasDisplayedForward() {
        return displayedForward != null;
    }

    public float @Nullable [] getDisplayedForward() {
        return displayedForward;
    }

    public void setDisplayedForward(float x, float y, float z) {
        if (displayedForward == null) {
            displayedForward = new float[] { x, y, z };
        } else {
            displayedForward[0] = x;
            displayedForward[1] = y;
            displayedForward[2] = z;
        }
    }

    public void clearDisplayedForward() {
        displayedForward = null;
    }
}
