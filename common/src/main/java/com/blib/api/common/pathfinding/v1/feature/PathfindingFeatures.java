package com.blib.api.common.pathfinding.v1.feature;

/**
 * Immutable set of pathfinding feature flags. Profiles are just named presets over this value object; callers can also
 * toggle individual flags and keep the result as a custom feature set.
 */
public record PathfindingFeatures(
    boolean sameLevelMovement,
    boolean diagonalMovement,
    boolean stepUp,
    boolean stepDown,
    boolean verticalTargetResolution,
    boolean pathSkipAhead,
    boolean stuckReplan,
    boolean sectionCorridor,
    boolean segmentedPathPlanning,
    boolean partialPathResults,
    boolean doorOpening,
    boolean asyncPathfinding,
    boolean collisionShapeWaypoints,
    boolean diagonalCornerClearance,
    boolean footprintClearance,
    boolean anyAngleSmoothing,
    boolean steppedFootprintSupport,
    boolean dropDownOpenings,
    boolean entityHitboxClearance,
    boolean crawlThroughGaps
) {

    public PathfindingFeatures(
        boolean sameLevelMovement,
        boolean diagonalMovement,
        boolean stepUp,
        boolean stepDown,
        boolean verticalTargetResolution,
        boolean pathSkipAhead,
        boolean stuckReplan
    ) {
        this(
            sameLevelMovement,
            diagonalMovement,
            stepUp,
            stepDown,
            verticalTargetResolution,
            pathSkipAhead,
            stuckReplan,
            false
        );
    }

    public PathfindingFeatures(
        boolean sameLevelMovement,
        boolean diagonalMovement,
        boolean stepUp,
        boolean stepDown,
        boolean verticalTargetResolution,
        boolean pathSkipAhead,
        boolean stuckReplan,
        boolean sectionCorridor
    ) {
        this(
            sameLevelMovement,
            diagonalMovement,
            stepUp,
            stepDown,
            verticalTargetResolution,
            pathSkipAhead,
            stuckReplan,
            sectionCorridor,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true
        );
    }

    public static final PathfindingFeatures FLAT_ONLY = new PathfindingFeatures(
        true,
        false,
        false,
        false,
        false,
        false,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        true
    );

    public static final PathfindingFeatures STAIRS_ONLY = new PathfindingFeatures(
        true,
        false,
        true,
        true,
        true,
        false,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    );

    public static final PathfindingFeatures BASIC_GROUND = new PathfindingFeatures(
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    );

    public static final PathfindingFeatures LEGACY_PERMISSIVE = new PathfindingFeatures(
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    );

    public int toMask() {
        var mask = 0;

        for (var feature : PathfindingFeature.values()) {
            if (enabled(feature)) {
                mask |= feature.mask();
            }
        }

        return mask;
    }

    public boolean enabled(PathfindingFeature feature) {
        return switch (feature) {
            case SAME_LEVEL_MOVEMENT -> sameLevelMovement;
            case DIAGONAL_MOVEMENT -> diagonalMovement;
            case STEP_UP -> stepUp;
            case STEP_DOWN -> stepDown;
            case VERTICAL_TARGET_RESOLUTION -> verticalTargetResolution;
            case PATH_SKIP_AHEAD -> pathSkipAhead;
            case STUCK_REPLAN -> stuckReplan;
            case SECTION_CORRIDOR -> sectionCorridor;
            case SEGMENTED_PATH_PLANNING -> segmentedPathPlanning;
            case PARTIAL_PATH_RESULTS -> partialPathResults;
            case DOOR_OPENING -> doorOpening;
            case ASYNC_PATHFINDING -> asyncPathfinding;
            case COLLISION_SHAPE_WAYPOINTS -> collisionShapeWaypoints;
            case DIAGONAL_CORNER_CLEARANCE -> diagonalCornerClearance;
            case FOOTPRINT_CLEARANCE -> footprintClearance;
            case ANY_ANGLE_SMOOTHING -> anyAngleSmoothing;
            case STEPPED_FOOTPRINT_SUPPORT -> steppedFootprintSupport;
            case DROP_DOWN_OPENINGS -> dropDownOpenings;
            case ENTITY_HITBOX_CLEARANCE -> entityHitboxClearance;
            case CRAWL_THROUGH_GAPS -> crawlThroughGaps;
        };
    }

    public PathfindingFeatures with(PathfindingFeature feature, boolean enabled) {
        return new PathfindingFeatures(
            feature == PathfindingFeature.SAME_LEVEL_MOVEMENT ? enabled : sameLevelMovement,
            feature == PathfindingFeature.DIAGONAL_MOVEMENT ? enabled : diagonalMovement,
            feature == PathfindingFeature.STEP_UP ? enabled : stepUp,
            feature == PathfindingFeature.STEP_DOWN ? enabled : stepDown,
            feature == PathfindingFeature.VERTICAL_TARGET_RESOLUTION ? enabled : verticalTargetResolution,
            feature == PathfindingFeature.PATH_SKIP_AHEAD ? enabled : pathSkipAhead,
            feature == PathfindingFeature.STUCK_REPLAN ? enabled : stuckReplan,
            feature == PathfindingFeature.SECTION_CORRIDOR ? enabled : sectionCorridor,
            feature == PathfindingFeature.SEGMENTED_PATH_PLANNING ? enabled : segmentedPathPlanning,
            feature == PathfindingFeature.PARTIAL_PATH_RESULTS ? enabled : partialPathResults,
            feature == PathfindingFeature.DOOR_OPENING ? enabled : doorOpening,
            feature == PathfindingFeature.ASYNC_PATHFINDING ? enabled : asyncPathfinding,
            feature == PathfindingFeature.COLLISION_SHAPE_WAYPOINTS ? enabled : collisionShapeWaypoints,
            feature == PathfindingFeature.DIAGONAL_CORNER_CLEARANCE ? enabled : diagonalCornerClearance,
            feature == PathfindingFeature.FOOTPRINT_CLEARANCE ? enabled : footprintClearance,
            feature == PathfindingFeature.ANY_ANGLE_SMOOTHING ? enabled : anyAngleSmoothing,
            feature == PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT ? enabled : steppedFootprintSupport,
            feature == PathfindingFeature.DROP_DOWN_OPENINGS ? enabled : dropDownOpenings,
            feature == PathfindingFeature.ENTITY_HITBOX_CLEARANCE ? enabled : entityHitboxClearance,
            feature == PathfindingFeature.CRAWL_THROUGH_GAPS ? enabled : crawlThroughGaps
        );
    }

    public static PathfindingFeatures fromMask(int mask) {
        return new PathfindingFeatures(
            enabled(mask, PathfindingFeature.SAME_LEVEL_MOVEMENT),
            enabled(mask, PathfindingFeature.DIAGONAL_MOVEMENT),
            enabled(mask, PathfindingFeature.STEP_UP),
            enabled(mask, PathfindingFeature.STEP_DOWN),
            enabled(mask, PathfindingFeature.VERTICAL_TARGET_RESOLUTION),
            enabled(mask, PathfindingFeature.PATH_SKIP_AHEAD),
            enabled(mask, PathfindingFeature.STUCK_REPLAN),
            enabled(mask, PathfindingFeature.SECTION_CORRIDOR),
            enabled(mask, PathfindingFeature.SEGMENTED_PATH_PLANNING),
            enabled(mask, PathfindingFeature.PARTIAL_PATH_RESULTS),
            enabled(mask, PathfindingFeature.DOOR_OPENING),
            enabled(mask, PathfindingFeature.ASYNC_PATHFINDING),
            enabled(mask, PathfindingFeature.COLLISION_SHAPE_WAYPOINTS),
            enabled(mask, PathfindingFeature.DIAGONAL_CORNER_CLEARANCE),
            enabled(mask, PathfindingFeature.FOOTPRINT_CLEARANCE),
            enabled(mask, PathfindingFeature.ANY_ANGLE_SMOOTHING),
            enabled(mask, PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT),
            enabled(mask, PathfindingFeature.DROP_DOWN_OPENINGS),
            enabled(mask, PathfindingFeature.ENTITY_HITBOX_CLEARANCE),
            enabled(mask, PathfindingFeature.CRAWL_THROUGH_GAPS)
        );
    }

    private static boolean enabled(int mask, PathfindingFeature feature) {
        return (mask & feature.mask()) != 0;
    }
}
