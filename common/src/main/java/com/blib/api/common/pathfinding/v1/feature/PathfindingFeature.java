package com.blib.api.common.pathfinding.v1.feature;

/**
 * Runtime-togglable pathfinding behavior. Each feature is intentionally small so debug tools can isolate one behavior
 * without changing the rest of the navigator.
 */
public enum PathfindingFeature {
    SAME_LEVEL_MOVEMENT(1L << 0, "Same Level"),
    DIAGONAL_MOVEMENT(1L << 1, "Diagonal"),
    STEP_UP(1L << 2, "Step Up"),
    STEP_DOWN(1L << 3, "Step Down"),
    VERTICAL_TARGET_RESOLUTION(1L << 4, "Vertical Target"),
    PATH_SKIP_AHEAD(1L << 5, "Skip Ahead"),
    STUCK_REPLAN(1L << 6, "Stuck Replan"),
    SECTION_CORRIDOR(1L << 7, "Section Corridor"),
    SEGMENTED_PATH_PLANNING(1L << 8, "Segmented Planning"),
    PARTIAL_PATH_RESULTS(1L << 9, "Partial Paths"),
    DOOR_OPENING(1L << 10, "Door Opening"),
    ASYNC_PATHFINDING(1L << 11, "Async Search"),
    COLLISION_SHAPE_WAYPOINTS(1L << 12, "Shape Waypoints"),
    HORIZONTAL_DIAGONAL_CLEARANCE(1L << 13, "Diag Horizontal"),
    FOOTPRINT_CLEARANCE(1L << 14, "Footprint Clearance"),
    ANY_ANGLE_SMOOTHING(1L << 15, "Any-Angle Smoothing"),
    STEPPED_FOOTPRINT_SUPPORT(1L << 16, "Stepped Footprint"),
    DROP_DOWN_OPENINGS(1L << 17, "Drop Openings"),
    ENTITY_HITBOX_CLEARANCE(1L << 18, "Hitbox Clearance"),
    CRAWL_THROUGH_GAPS(1L << 19, "Crawl Gaps"),
    DESCENDING_STAIR_EDGE_REACH(1L << 20, "Stair Edge Reach"),
    WATER_PATHFINDING(1L << 21, "Water Base", Category.WATER),
    SEARCH_CACHING(1L << 22, "Search Cache"),
    TERRAIN_PRECHECK(1L << 23, "Terrain Precheck"),
    ANY_ANGLE_SMOOTHING_CACHE(1L << 24, "Smoothing Cache"),
    FOOTPRINT_SCAN_CACHE(1L << 25, "Footprint Cache"),
    PATH_PREFIX_REUSE(1L << 26, "Path Reuse"),
    GROUNDED_TARGET_PROJECTION(1L << 27, "Target Projection"),
    BIDIRECTIONAL_SEARCH(1L << 28, "Bidirectional"),
    BALANCED_BIDIRECTIONAL_EXPANSION(1L << 29, "Balanced Bidir"),
    VERTICAL_DIAGONAL_CLEARANCE(1L << 30, "Diag Vertical"),
    DIAGONAL_SWEPT_SHAPE_CLEARANCE(1L << 31, "Diag Sweep"),
    WATER_ENTRY(1L << 32, "Water Entry", Category.WATER),
    WATER_VERTICAL_SWIM(1L << 33, "Vertical Swim", Category.WATER),
    WATER_SLOPE_SWIM(1L << 34, "Slope Swim", Category.WATER),
    WATER_EXIT(1L << 35, "Water Exit", Category.WATER),
    WATER_MOVEMENT_ASSIST(1L << 36, "Movement Assist", Category.WATER),
    WATER_EXIT_BREACH(1L << 37, "Exit Breach", Category.WATER),
    WATER_STEP_UP_PRE_LIFT(1L << 38, "Step-Up Pre-Lift", Category.WATER);

    private final long mask;

    private final String displayName;

    private final Category category;

    PathfindingFeature(long mask, String displayName) {
        this(mask, displayName, Category.GENERAL);
    }

    PathfindingFeature(long mask, String displayName, Category category) {
        this.mask = mask;
        this.displayName = displayName;
        this.category = category;
    }

    public long mask() {
        return mask;
    }

    public String displayName() {
        return displayName;
    }

    public Category category() {
        return category;
    }

    public enum Category {
        GENERAL,
        WATER
    }
}
