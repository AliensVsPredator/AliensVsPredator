package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.just.core.functional.result.Result;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.path.BLibPath;

final class PathNavigationRequestComponent implements PathNavigationRequest {

    private final PathNavigationPlanningComponent planning;

    private final BlockPos entityPos;

    private final BlockPos rawTarget;

    private final @Nullable PathfindingFeatures pathfindingFeatures;

    PathNavigationRequestComponent(
        PathNavigationPlanningComponent planning,
        BlockPos entityPos,
        BlockPos rawTarget,
        @Nullable PathfindingFeatures pathfindingFeatures
    ) {
        this.planning = planning;
        this.entityPos = entityPos;
        this.rawTarget = rawTarget;
        this.pathfindingFeatures = pathfindingFeatures;
    }

    @Override
    public PathNavigationRequest withFeatures(PathfindingFeatures pathfindingFeatures) {
        return new PathNavigationRequestComponent(
            planning,
            entityPos,
            rawTarget,
            Objects.requireNonNull(pathfindingFeatures, "pathfindingFeatures")
        );
    }

    @Override
    public CompletableFuture<Result<BLibPath, PathNavigationFailure>> start() {
        return planning.executeAsync(entityPos, rawTarget, pathfindingFeatures);
    }
}
