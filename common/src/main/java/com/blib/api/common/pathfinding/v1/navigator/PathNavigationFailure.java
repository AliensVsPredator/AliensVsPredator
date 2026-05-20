package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;

/**
 * Reason a path navigation request did not construct a usable path.
 */
public sealed interface PathNavigationFailure permits
    PathNavigationFailure.InFailureCooldown,
    PathNavigationFailure.NoPathFound,
    PathNavigationFailure.SearchFailed,
    PathNavigationFailure.Superseded {

    record InFailureCooldown(
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget,
        int remainingTicks
    ) implements PathNavigationFailure {}

    record NoPathFound(
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget
    ) implements PathNavigationFailure {}

    record SearchFailed(
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget,
        Throwable cause
    ) implements PathNavigationFailure {}

    record Superseded() implements PathNavigationFailure {}
}
