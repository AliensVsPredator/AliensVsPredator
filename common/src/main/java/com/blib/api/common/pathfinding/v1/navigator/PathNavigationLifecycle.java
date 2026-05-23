package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import com.just.core.functional.result.Result;

import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Internal mutually exclusive lifecycle states for a path navigator.
 */
sealed interface PathNavigationLifecycle permits
    PathNavigationLifecycle.Idle,
    PathNavigationLifecycle.Planning,
    PathNavigationLifecycle.Navigating,
    PathNavigationLifecycle.AwaitingNextSegment,
    PathNavigationLifecycle.AwaitingRepath,
    PathNavigationLifecycle.Reached,
    PathNavigationLifecycle.Exhausted,
    PathNavigationLifecycle.Failed {

    record RequestContext(
        BlockPos entityStart,
        BlockPos rawTarget,
        BlockPos searchTarget
    ) {
        RequestContext withTargets(BlockPos rawTarget, BlockPos searchTarget) {
            return new RequestContext(entityStart, rawTarget, searchTarget);
        }
    }

    record ActivePathContext(
        RequestContext request,
        BLibPath path,
        TerrainType currentTerrain
    ) {
        ActivePathContext withRequest(RequestContext request) {
            return new ActivePathContext(request, path, currentTerrain);
        }

        ActivePathContext withPath(BLibPath path, TerrainType currentTerrain) {
            return new ActivePathContext(request, path, currentTerrain);
        }
    }

    record Idle() implements PathNavigationLifecycle {}

    record Planning(
        RequestContext request,
        @Nullable CompletableFuture<@Nullable BLibPath> pendingPath,
        @Nullable CompletableFuture<Result<BLibPath, PathNavigationFailure>> resultFuture,
        long startNanos,
        @Nullable ActivePathContext activePath
    ) implements PathNavigationLifecycle {}

    record Navigating(ActivePathContext activePath) implements PathNavigationLifecycle {}

    record AwaitingNextSegment(ActivePathContext completedSegment) implements PathNavigationLifecycle {}

    record AwaitingRepath(RequestContext request) implements PathNavigationLifecycle {}

    record Reached(ActivePathContext completedPath) implements PathNavigationLifecycle {}

    record Exhausted(ActivePathContext completedPath) implements PathNavigationLifecycle {}

    record Failed(RequestContext request, PathNavigationFailure failure) implements PathNavigationLifecycle {}
}
