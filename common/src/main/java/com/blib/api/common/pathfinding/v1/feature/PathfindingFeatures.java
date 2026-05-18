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
    boolean dropDownOpenings
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
        false
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
        };
    }

    public PathfindingFeatures with(PathfindingFeature feature, boolean enabled) {
        return switch (feature) {
            case SAME_LEVEL_MOVEMENT -> new PathfindingFeatures(
                enabled,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case DIAGONAL_MOVEMENT -> new PathfindingFeatures(
                sameLevelMovement,
                enabled,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case STEP_UP -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                enabled,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case STEP_DOWN -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                enabled,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case VERTICAL_TARGET_RESOLUTION -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                enabled,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case PATH_SKIP_AHEAD -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                enabled,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case STUCK_REPLAN -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                enabled,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case SECTION_CORRIDOR -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                enabled,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case SEGMENTED_PATH_PLANNING -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                enabled,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case PARTIAL_PATH_RESULTS -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                enabled,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case DOOR_OPENING -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                enabled,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case ASYNC_PATHFINDING -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                enabled,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case COLLISION_SHAPE_WAYPOINTS -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                enabled,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case DIAGONAL_CORNER_CLEARANCE -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                enabled,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case FOOTPRINT_CLEARANCE -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                enabled,
                anyAngleSmoothing,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case ANY_ANGLE_SMOOTHING -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                enabled,
                steppedFootprintSupport,
                dropDownOpenings
            );
            case STEPPED_FOOTPRINT_SUPPORT -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                enabled,
                dropDownOpenings
            );
            case DROP_DOWN_OPENINGS -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan,
                sectionCorridor,
                segmentedPathPlanning,
                partialPathResults,
                doorOpening,
                asyncPathfinding,
                collisionShapeWaypoints,
                diagonalCornerClearance,
                footprintClearance,
                anyAngleSmoothing,
                steppedFootprintSupport,
                enabled
            );
        };
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
            enabled(mask, PathfindingFeature.DROP_DOWN_OPENINGS)
        );
    }

    private static boolean enabled(int mask, PathfindingFeature feature) {
        return (mask & feature.mask()) != 0;
    }
}
