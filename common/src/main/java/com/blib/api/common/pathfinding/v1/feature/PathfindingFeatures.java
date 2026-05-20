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
    boolean blockBreaking,
    boolean asyncPathfinding,
    boolean collisionShapeWaypoints,
    boolean horizontalDiagonalClearance,
    boolean verticalDiagonalClearance,
    boolean diagonalSweptShapeClearance,
    boolean footprintClearance,
    boolean anyAngleSmoothing,
    boolean steppedFootprintSupport,
    boolean dropDownOpenings,
    boolean entityHitboxClearance,
    boolean crawlThroughGaps,
    boolean descendingStairEdgeReach,
    boolean waterPathfinding,
    boolean waterSwimClearance,
    boolean waterEntry,
    boolean waterVerticalSwim,
    boolean waterSlopeSwim,
    boolean waterExit,
    boolean waterMovementAssist,
    boolean waterExitBreach,
    boolean waterStepUpPreLift,
    boolean waterAnyAngleSmoothing,
    boolean searchCaching,
    boolean terrainPrecheck,
    boolean anyAngleSmoothingCache,
    boolean footprintScanCache,
    boolean pathPrefixReuse,
    boolean groundedTargetProjection,
    boolean bidirectionalSearch,
    boolean balancedBidirectionalExpansion,
    boolean parentEdgePruning
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
            true, // segmentedPathPlanning
            true, // partialPathResults
            true, // doorOpening
            false, // blockBreaking
            true, // asyncPathfinding
            true, // collisionShapeWaypoints
            true, // horizontalDiagonalClearance
            true, // verticalDiagonalClearance
            true, // diagonalSweptShapeClearance
            true, // footprintClearance
            true, // anyAngleSmoothing
            true, // steppedFootprintSupport
            true, // dropDownOpenings
            true, // entityHitboxClearance
            true, // crawlThroughGaps
            true, // descendingStairEdgeReach
            true, // waterPathfinding
            true, // waterSwimClearance
            true, // waterEntry
            true, // waterVerticalSwim
            true, // waterSlopeSwim
            true, // waterExit
            true, // waterMovementAssist
            true, // waterExitBreach
            true, // waterStepUpPreLift
            true, // waterAnyAngleSmoothing
            true, // searchCaching
            true, // terrainPrecheck
            true, // anyAngleSmoothingCache
            true, // footprintScanCache
            true, // pathPrefixReuse
            true, // groundedTargetProjection
            true, // bidirectionalSearch
            true, // balancedBidirectionalExpansion
            true // parentEdgePruning
        );
    }

    public static final PathfindingFeatures EMPTY = PathfindingFeatures.fromMask(0L);

    public static final PathfindingFeatures LEGACY_PERMISSIVE = new PathfindingFeatures(
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    );

    public static final PathfindingFeatures FLAT_ONLY = LEGACY_PERMISSIVE
        .with(PathfindingFeature.DIAGONAL_MOVEMENT, false)
        .with(PathfindingFeature.STEP_UP, false)
        .with(PathfindingFeature.STEP_DOWN, false)
        .with(PathfindingFeature.VERTICAL_TARGET_RESOLUTION, false)
        .with(PathfindingFeature.PATH_SKIP_AHEAD, false)
        .with(PathfindingFeature.SECTION_CORRIDOR, false)
        .with(PathfindingFeature.DROP_DOWN_OPENINGS, false)
        .withWaterFeatures(false);

    public static final PathfindingFeatures STAIRS_ONLY = LEGACY_PERMISSIVE
        .with(PathfindingFeature.DIAGONAL_MOVEMENT, false)
        .with(PathfindingFeature.PATH_SKIP_AHEAD, false)
        .with(PathfindingFeature.SECTION_CORRIDOR, false)
        .withWaterFeatures(false);

    public static final PathfindingFeatures BASIC_GROUND = LEGACY_PERMISSIVE
        .with(PathfindingFeature.DIAGONAL_MOVEMENT, false)
        .with(PathfindingFeature.SECTION_CORRIDOR, false)
        .withWaterFeatures(false);

    public long toMask() {
        var mask = 0L;

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
            case BLOCK_BREAKING -> blockBreaking;
            case ASYNC_PATHFINDING -> asyncPathfinding;
            case COLLISION_SHAPE_WAYPOINTS -> collisionShapeWaypoints;
            case HORIZONTAL_DIAGONAL_CLEARANCE -> horizontalDiagonalClearance;
            case VERTICAL_DIAGONAL_CLEARANCE -> verticalDiagonalClearance;
            case DIAGONAL_SWEPT_SHAPE_CLEARANCE -> diagonalSweptShapeClearance;
            case FOOTPRINT_CLEARANCE -> footprintClearance;
            case ANY_ANGLE_SMOOTHING -> anyAngleSmoothing;
            case STEPPED_FOOTPRINT_SUPPORT -> steppedFootprintSupport;
            case DROP_DOWN_OPENINGS -> dropDownOpenings;
            case ENTITY_HITBOX_CLEARANCE -> entityHitboxClearance;
            case CRAWL_THROUGH_GAPS -> crawlThroughGaps;
            case DESCENDING_STAIR_EDGE_REACH -> descendingStairEdgeReach;
            case WATER_PATHFINDING -> waterPathfinding;
            case WATER_SWIM_CLEARANCE -> waterSwimClearance;
            case WATER_ENTRY -> waterEntry;
            case WATER_VERTICAL_SWIM -> waterVerticalSwim;
            case WATER_SLOPE_SWIM -> waterSlopeSwim;
            case WATER_EXIT -> waterExit;
            case WATER_MOVEMENT_ASSIST -> waterMovementAssist;
            case WATER_EXIT_BREACH -> waterExitBreach;
            case WATER_STEP_UP_PRE_LIFT -> waterStepUpPreLift;
            case WATER_ANY_ANGLE_SMOOTHING -> waterAnyAngleSmoothing;
            case SEARCH_CACHING -> searchCaching;
            case TERRAIN_PRECHECK -> terrainPrecheck;
            case ANY_ANGLE_SMOOTHING_CACHE -> anyAngleSmoothingCache;
            case FOOTPRINT_SCAN_CACHE -> footprintScanCache;
            case PATH_PREFIX_REUSE -> pathPrefixReuse;
            case GROUNDED_TARGET_PROJECTION -> groundedTargetProjection;
            case BIDIRECTIONAL_SEARCH -> bidirectionalSearch;
            case BALANCED_BIDIRECTIONAL_EXPANSION -> balancedBidirectionalExpansion;
            case PARENT_EDGE_PRUNING -> parentEdgePruning;
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
            feature == PathfindingFeature.BLOCK_BREAKING ? enabled : blockBreaking,
            feature == PathfindingFeature.ASYNC_PATHFINDING ? enabled : asyncPathfinding,
            feature == PathfindingFeature.COLLISION_SHAPE_WAYPOINTS ? enabled : collisionShapeWaypoints,
            feature == PathfindingFeature.HORIZONTAL_DIAGONAL_CLEARANCE ? enabled : horizontalDiagonalClearance,
            feature == PathfindingFeature.VERTICAL_DIAGONAL_CLEARANCE ? enabled : verticalDiagonalClearance,
            feature == PathfindingFeature.DIAGONAL_SWEPT_SHAPE_CLEARANCE ? enabled : diagonalSweptShapeClearance,
            feature == PathfindingFeature.FOOTPRINT_CLEARANCE ? enabled : footprintClearance,
            feature == PathfindingFeature.ANY_ANGLE_SMOOTHING ? enabled : anyAngleSmoothing,
            feature == PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT ? enabled : steppedFootprintSupport,
            feature == PathfindingFeature.DROP_DOWN_OPENINGS ? enabled : dropDownOpenings,
            feature == PathfindingFeature.ENTITY_HITBOX_CLEARANCE ? enabled : entityHitboxClearance,
            feature == PathfindingFeature.CRAWL_THROUGH_GAPS ? enabled : crawlThroughGaps,
            feature == PathfindingFeature.DESCENDING_STAIR_EDGE_REACH ? enabled : descendingStairEdgeReach,
            feature == PathfindingFeature.WATER_PATHFINDING ? enabled : waterPathfinding,
            feature == PathfindingFeature.WATER_SWIM_CLEARANCE ? enabled : waterSwimClearance,
            feature == PathfindingFeature.WATER_ENTRY ? enabled : waterEntry,
            feature == PathfindingFeature.WATER_VERTICAL_SWIM ? enabled : waterVerticalSwim,
            feature == PathfindingFeature.WATER_SLOPE_SWIM ? enabled : waterSlopeSwim,
            feature == PathfindingFeature.WATER_EXIT ? enabled : waterExit,
            feature == PathfindingFeature.WATER_MOVEMENT_ASSIST ? enabled : waterMovementAssist,
            feature == PathfindingFeature.WATER_EXIT_BREACH ? enabled : waterExitBreach,
            feature == PathfindingFeature.WATER_STEP_UP_PRE_LIFT ? enabled : waterStepUpPreLift,
            feature == PathfindingFeature.WATER_ANY_ANGLE_SMOOTHING ? enabled : waterAnyAngleSmoothing,
            feature == PathfindingFeature.SEARCH_CACHING ? enabled : searchCaching,
            feature == PathfindingFeature.TERRAIN_PRECHECK ? enabled : terrainPrecheck,
            feature == PathfindingFeature.ANY_ANGLE_SMOOTHING_CACHE ? enabled : anyAngleSmoothingCache,
            feature == PathfindingFeature.FOOTPRINT_SCAN_CACHE ? enabled : footprintScanCache,
            feature == PathfindingFeature.PATH_PREFIX_REUSE ? enabled : pathPrefixReuse,
            feature == PathfindingFeature.GROUNDED_TARGET_PROJECTION ? enabled : groundedTargetProjection,
            feature == PathfindingFeature.BIDIRECTIONAL_SEARCH ? enabled : bidirectionalSearch,
            feature == PathfindingFeature.BALANCED_BIDIRECTIONAL_EXPANSION ? enabled : balancedBidirectionalExpansion,
            feature == PathfindingFeature.PARENT_EDGE_PRUNING ? enabled : parentEdgePruning
        );
    }

    public PathfindingFeatures withWaterFeatures(boolean enabled) {
        var current = this;

        for (var feature : PathfindingFeature.values()) {
            if (feature.category() == PathfindingFeature.Category.WATER) {
                current = current.with(feature, enabled);
            }
        }

        return current;
    }

    public static PathfindingFeatures fromMask(int mask) {
        return fromMask(Integer.toUnsignedLong(mask));
    }

    public static PathfindingFeatures fromMask(long mask) {
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
            enabled(mask, PathfindingFeature.BLOCK_BREAKING),
            enabled(mask, PathfindingFeature.ASYNC_PATHFINDING),
            enabled(mask, PathfindingFeature.COLLISION_SHAPE_WAYPOINTS),
            enabled(mask, PathfindingFeature.HORIZONTAL_DIAGONAL_CLEARANCE),
            enabled(mask, PathfindingFeature.VERTICAL_DIAGONAL_CLEARANCE),
            enabled(mask, PathfindingFeature.DIAGONAL_SWEPT_SHAPE_CLEARANCE),
            enabled(mask, PathfindingFeature.FOOTPRINT_CLEARANCE),
            enabled(mask, PathfindingFeature.ANY_ANGLE_SMOOTHING),
            enabled(mask, PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT),
            enabled(mask, PathfindingFeature.DROP_DOWN_OPENINGS),
            enabled(mask, PathfindingFeature.ENTITY_HITBOX_CLEARANCE),
            enabled(mask, PathfindingFeature.CRAWL_THROUGH_GAPS),
            enabled(mask, PathfindingFeature.DESCENDING_STAIR_EDGE_REACH),
            enabled(mask, PathfindingFeature.WATER_PATHFINDING),
            enabled(mask, PathfindingFeature.WATER_SWIM_CLEARANCE),
            enabled(mask, PathfindingFeature.WATER_ENTRY),
            enabled(mask, PathfindingFeature.WATER_VERTICAL_SWIM),
            enabled(mask, PathfindingFeature.WATER_SLOPE_SWIM),
            enabled(mask, PathfindingFeature.WATER_EXIT),
            enabled(mask, PathfindingFeature.WATER_MOVEMENT_ASSIST),
            enabled(mask, PathfindingFeature.WATER_EXIT_BREACH),
            enabled(mask, PathfindingFeature.WATER_STEP_UP_PRE_LIFT),
            enabled(mask, PathfindingFeature.WATER_ANY_ANGLE_SMOOTHING),
            enabled(mask, PathfindingFeature.SEARCH_CACHING),
            enabled(mask, PathfindingFeature.TERRAIN_PRECHECK),
            enabled(mask, PathfindingFeature.ANY_ANGLE_SMOOTHING_CACHE),
            enabled(mask, PathfindingFeature.FOOTPRINT_SCAN_CACHE),
            enabled(mask, PathfindingFeature.PATH_PREFIX_REUSE),
            enabled(mask, PathfindingFeature.GROUNDED_TARGET_PROJECTION),
            enabled(mask, PathfindingFeature.BIDIRECTIONAL_SEARCH),
            enabled(mask, PathfindingFeature.BALANCED_BIDIRECTIONAL_EXPANSION),
            enabled(mask, PathfindingFeature.PARENT_EDGE_PRUNING)
        );
    }

    private static boolean enabled(long mask, PathfindingFeature feature) {
        return (mask & feature.mask()) != 0;
    }
}
