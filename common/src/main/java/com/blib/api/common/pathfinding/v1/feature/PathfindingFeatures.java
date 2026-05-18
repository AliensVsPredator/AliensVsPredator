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
    boolean stuckReplan
) {

    public static final PathfindingFeatures FLAT_ONLY = new PathfindingFeatures(
        true,
        false,
        false,
        false,
        false,
        false,
        true
    );

    public static final PathfindingFeatures STAIRS_ONLY = new PathfindingFeatures(
        true,
        false,
        true,
        true,
        true,
        false,
        true
    );

    public static final PathfindingFeatures BASIC_GROUND = new PathfindingFeatures(
        true,
        false,
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
                stuckReplan
            );
            case DIAGONAL_MOVEMENT -> new PathfindingFeatures(
                sameLevelMovement,
                enabled,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan
            );
            case STEP_UP -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                enabled,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan
            );
            case STEP_DOWN -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                enabled,
                verticalTargetResolution,
                pathSkipAhead,
                stuckReplan
            );
            case VERTICAL_TARGET_RESOLUTION -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                enabled,
                pathSkipAhead,
                stuckReplan
            );
            case PATH_SKIP_AHEAD -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                enabled,
                stuckReplan
            );
            case STUCK_REPLAN -> new PathfindingFeatures(
                sameLevelMovement,
                diagonalMovement,
                stepUp,
                stepDown,
                verticalTargetResolution,
                pathSkipAhead,
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
            enabled(mask, PathfindingFeature.STUCK_REPLAN)
        );
    }

    private static boolean enabled(int mask, PathfindingFeature feature) {
        return (mask & feature.mask()) != 0;
    }
}
