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
    STUCK_REPLAN(1 << 6, "Stuck Replan"),
    SECTION_CORRIDOR(1 << 7, "Section Corridor"),
    SEGMENTED_PATH_PLANNING(1 << 8, "Segmented Planning"),
    PARTIAL_PATH_RESULTS(1 << 9, "Partial Paths"),
    DOOR_OPENING(1 << 10, "Door Opening"),
    ASYNC_PATHFINDING(1 << 11, "Async Search"),
    COLLISION_SHAPE_WAYPOINTS(1 << 12, "Shape Waypoints"),
    DIAGONAL_CORNER_CLEARANCE(1 << 13, "Diagonal Clearance"),
    FOOTPRINT_CLEARANCE(1 << 14, "Footprint Clearance"),
    ANY_ANGLE_SMOOTHING(1 << 15, "Any-Angle Smoothing"),
    STEPPED_FOOTPRINT_SUPPORT(1 << 16, "Stepped Footprint"),
    DROP_DOWN_OPENINGS(1 << 17, "Drop Openings"),
    ENTITY_HITBOX_CLEARANCE(1 << 18, "Hitbox Clearance"),
    CRAWL_THROUGH_GAPS(1 << 19, "Crawl Gaps"),
    DESCENDING_STAIR_EDGE_REACH(1 << 20, "Stair Edge Reach");

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
