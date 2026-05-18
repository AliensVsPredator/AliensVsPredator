package com.blib.api.common.pathfinding.v1.feature;

/**
 * Runtime-togglable pathfinding behavior. Each feature is intentionally small so debug tools can isolate one behavior
 * without changing the rest of the navigator.
 */
public enum PathfindingFeature {
    SAME_LEVEL_MOVEMENT(1 << 0, "Same Level"),
    DIAGONAL_MOVEMENT(1 << 1, "Diagonal"),
    STEP_UP(1 << 2, "Step Up"),
    STEP_DOWN(1 << 3, "Step Down"),
    VERTICAL_TARGET_RESOLUTION(1 << 4, "Vertical Target"),
    PATH_SKIP_AHEAD(1 << 5, "Skip Ahead"),
    STUCK_REPLAN(1 << 6, "Stuck Replan");

    private final int mask;

    private final String displayName;

    PathfindingFeature(int mask, String displayName) {
        this.mask = mask;
        this.displayName = displayName;
    }

    public int mask() {
        return mask;
    }

    public String displayName() {
        return displayName;
    }
}
