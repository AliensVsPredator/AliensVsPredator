package com.blib.api.common.pathfinding.v1.navigator;

import java.util.concurrent.CompletableFuture;

import com.just.core.functional.result.Result;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.path.BLibPath;

/**
 * Configurable path navigation request. A request starts only when {@link #start()} is called.
 */
public interface PathNavigationRequest {

    /**
     * Applies a feature set only to this navigation request. Replans for this path reuse the same feature set until the
     * navigator is stopped or another navigation request starts.
     */
    PathNavigationRequest withFeatures(PathfindingFeatures pathfindingFeatures);

    /**
     * Starts path construction and returns its result. Requests that cannot safely run asynchronously, such as
     * block-breaking searches, compute on the calling thread and return an already completed future.
     */
    CompletableFuture<Result<BLibPath, PathNavigationFailure>> start();
}
