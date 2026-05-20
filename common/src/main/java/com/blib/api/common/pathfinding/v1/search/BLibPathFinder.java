package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathEdgeDebugType;
import com.blib.api.common.pathfinding.v1.debug.PathDebugBlockPos;
import com.blib.api.common.pathfinding.v1.debug.PathRejectionReason;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugData;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugRecorder;
import com.blib.api.common.pathfinding.v1.debug.PathSearchMode;
import com.blib.api.common.pathfinding.v1.debug.PathSearchOutcome;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.debug.PathSearchTermination;
import com.blib.api.common.pathfinding.v1.debug.PathSearchTimingPhase;
import com.blib.api.common.pathfinding.v1.debug.StableGroundDebugEntry;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A* pathfinding with optional two-level hierarchical search. When the section-corridor feature is enabled and a
 * {@link TerrainClassificationCache} is provided, the pathfinder first runs a fast section-level A* via
 * {@link SectionCorridorFinder} to identify a corridor of 16x16x16 sections, then runs the block-level A* restricted
 * to that corridor.
 */
public final class BLibPathFinder {

    private static final int MAX_NEIGHBORS = 256;

    private static final ExecutorService PATHFINDING_EXECUTOR = Executors.newFixedThreadPool(
        Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
        runnable -> {
            var thread = new Thread(runnable, "BLib-Pathfinding");
            thread.setDaemon(true);
            return thread;
        }
    );

    private final TerrainEvaluator evaluator;

    private SearchConfig config;

    private PathfindingTuning tuning = PathfindingTuning.DEFAULT;

    private final @Nullable TerrainClassificationCache classificationCache;

    private final @Nullable SectionCorridorFinder corridorFinder;

    private @Nullable PathSearchSnapshot lastSearchSnapshot;

    private long lastFeatureUsageMask;

    // --- Pooled search data structures ---
    private final PriorityQueue<PathNode> openSet = new PriorityQueue<>();

    private final ArrayList<PathNode> closedNodes = new ArrayList<>();

    private final PathNode[] neighborBuffer = new PathNode[MAX_NEIGHBORS];

    private boolean debugEnabled;

    private boolean debugCaptureEnabled;

    private @Nullable Set<TerrainType> excludedTerrains;

    private PathfindingFeatures features = PathfindingProfile.LEGACY_PERMISSIVE.features();

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config) {
        this(evaluator, config, null);
    }

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.evaluator = evaluator;
        this.config = Objects.requireNonNull(config, "config");
        this.classificationCache = classificationCache;
        this.corridorFinder = classificationCache != null
            ? new SectionCorridorFinder(classificationCache, evaluator, this::getTuning)
            : null;
    }

    public void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains) {
        this.excludedTerrains = excludedTerrains;
    }

    public void setFeatures(PathfindingFeatures features) {
        this.features = features;
    }

    public SearchConfig getSearchConfig() {
        return config;
    }

    public void setSearchConfig(SearchConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    public PathfindingTuning getTuning() {
        return tuning;
    }

    public void setTuning(PathfindingTuning tuning) {
        this.tuning = Objects.requireNonNull(tuning, "tuning");
    }

    public void setDebugCaptureEnabled(boolean debugCaptureEnabled) {
        this.debugCaptureEnabled = debugCaptureEnabled;
    }

    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshot;
    }

    public long consumeFeatureUsageMask() {
        var mask = lastFeatureUsageMask;
        lastFeatureUsageMask = 0L;

        return mask;
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        debugEnabled = debugCaptureEnabled;

        evaluator.prepare(level);
        applyFeatures();
        applyExcludedTerrains();

        try {
            Set<Long> corridor = null;
            var mode = PathSearchMode.DIRECT;

            if (shouldUseCorridor(startPos, targetPos)) {
                var result = corridorFinder.findCorridor(level, startPos, targetPos);

                if (result != null) {
                    corridor = result.corridor();
                    mode = PathSearchMode.CORRIDOR;
                } else {
                    mode = PathSearchMode.DIRECT_FALLBACK;
                }
            }

            return searchBlocks(startPos, targetPos, corridor, mode);
        } finally {
            evaluator.cleanup();
        }
    }

    /**
     * Asynchronous path finding. Snapshots chunk data and pre-populates the terrain cache on the calling thread, then
     * dispatches the A* search to a background thread. The evaluator must be a {@link UnifiedTerrainEvaluator}.
     *
     * @return a future that completes with the path (or null if no path found)
     */
    public CompletableFuture<@Nullable BLibPath> findPathAsync(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        if (
            !features.asyncPathfinding()
                || features.blockBreaking()
                || !(evaluator instanceof UnifiedTerrainEvaluator unifiedEvaluator)
        ) {
            return CompletableFuture.completedFuture(findPath(level, startPos, targetPos));
        }

        debugEnabled = debugCaptureEnabled;

        // --- Main thread: snapshot chunks and pre-populate terrain cache ---

        var minCX = Math.min(startPos.getX(), targetPos.getX()) >> 4;
        var minCZ = Math.min(startPos.getZ(), targetPos.getZ()) >> 4;
        var maxCX = Math.max(startPos.getX(), targetPos.getX()) >> 4;
        var maxCZ = Math.max(startPos.getZ(), targetPos.getZ()) >> 4;

        unifiedEvaluator.prepareAsync();
        applyFeatures();
        applyExcludedTerrains();

        var asyncChunkMargin = tuning.asyncChunkMargin();

        for (int cx = minCX - asyncChunkMargin; cx <= maxCX + asyncChunkMargin; cx++) {
            for (int cz = minCZ - asyncChunkMargin; cz <= maxCZ + asyncChunkMargin; cz++) {
                unifiedEvaluator.preloadChunk(cx, cz, level.getChunk(cx, cz));
            }
        }

        if (shouldUseCorridor(startPos, targetPos)) {
            var margin = asyncChunkMargin * 16;
            classificationCache.prePopulateArea(
                level,
                Math.min(startPos.getX(), targetPos.getX()) - margin,
                Math.min(startPos.getY(), targetPos.getY()) - 16,
                Math.min(startPos.getZ(), targetPos.getZ()) - margin,
                Math.max(startPos.getX(), targetPos.getX()) + margin,
                Math.max(startPos.getY(), targetPos.getY()) + 16,
                Math.max(startPos.getZ(), targetPos.getZ()) + margin
            );
        }

        // --- Main thread: reachability check + corridor ---

        Set<Long> corridor = null;
        var mode = PathSearchMode.DIRECT;

        if (shouldUseCorridor(startPos, targetPos)) {
            var result = corridorFinder.findCorridor(level, startPos, targetPos);

            if (result != null) {
                corridor = result.corridor();
                mode = PathSearchMode.CORRIDOR;
            } else {
                mode = PathSearchMode.DIRECT_FALLBACK;
            }
        }

        // --- Background thread: run the search ---

        var capturedCorridor = corridor;
        var capturedMode = mode;

        return CompletableFuture.supplyAsync(() -> {
            try {
                return searchBlocks(startPos, targetPos, capturedCorridor, capturedMode);
            } finally {
                unifiedEvaluator.cleanup();
            }
        }, PATHFINDING_EXECUTOR);
    }

    /**
     * Computes the section-level corridor without running a block-level search. Returns null if no corridor finder is
     * configured or if the target is unreachable. The evaluator is prepared and cleaned up within this call so that
     * terrain cost queries return correct values during the corridor search.
     */
    public @Nullable CorridorResult computeCorridor(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        if (!features.sectionCorridor() || corridorFinder == null) {
            return null;
        }

        evaluator.prepare(level);
        applyFeatures();
        applyExcludedTerrains();

        try {
            return corridorFinder.findCorridor(level, startPos, targetPos);
        } finally {
            evaluator.cleanup();
        }
    }

    /**
     * Runs block-level A* directly without a section corridor gate. Use this as a fallback when the section-level route is
     * unavailable or when nearby targets need exact block-level digging/clearance decisions.
     */
    public @Nullable BLibPath findPathDirect(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        debugEnabled = debugCaptureEnabled;

        evaluator.prepare(level);
        applyFeatures();
        applyExcludedTerrains();

        try {
            return searchBlocks(startPos, targetPos, null, PathSearchMode.DIRECT);
        } finally {
            evaluator.cleanup();
        }
    }

    /**
     * Runs a block-level A* search using a pre-computed corridor constraint. Use this with corridors obtained from
     * {@link #computeCorridor} for segmented long-distance pathfinding.
     */
    public @Nullable BLibPath findPathInCorridor(
        LevelReader level,
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor
    ) {
        debugEnabled = debugCaptureEnabled;

        evaluator.prepare(level);
        applyFeatures();
        applyExcludedTerrains();

        try {
            var activeCorridor = features.sectionCorridor() ? corridor : null;

            return searchBlocks(
                startPos,
                targetPos,
                activeCorridor,
                activeCorridor != null ? PathSearchMode.CORRIDOR : PathSearchMode.DIRECT
            );
        } finally {
            evaluator.cleanup();
        }
    }

    private void applyExcludedTerrains() {
        if (excludedTerrains != null && !excludedTerrains.isEmpty() && evaluator instanceof UnifiedTerrainEvaluator unified) {
            unified.excludeTerrains(excludedTerrains);
        }
    }

    private void applyFeatures() {
        if (evaluator instanceof UnifiedTerrainEvaluator unified) {
            unified.setFeatures(features);
        }
    }

    private boolean shouldUseCorridor(BlockPos startPos, BlockPos targetPos) {
        return features.sectionCorridor()
            && corridorFinder != null
            && startPos.distManhattan(targetPos) > tuning.corridorDistanceThreshold();
    }

    private @Nullable BLibPath searchBlocks(
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor,
        PathSearchMode mode
    ) {
        lastFeatureUsageMask = 0L;
        var recorder = debugEnabled ? new PathSearchDebugRecorder() : null;
        var searchConfig = config;
        var activeTuning = tuning;
        setEvaluatorDebugRecorder(recorder);

        try {
            if (corridor != null) {
                markFeatureUsed(recorder, PathfindingFeature.SECTION_CORRIDOR);
            }

            var path = searchBlocksWithDiagnostics(startPos, targetPos, corridor, mode, recorder, searchConfig, activeTuning);
            lastFeatureUsageMask = recorder != null ? recorder.featureUsageMask() : 0L;

            return path;
        } finally {
            setEvaluatorDebugRecorder(null);
        }
    }

    private void setEvaluatorDebugRecorder(@Nullable PathSearchDebugRecorder recorder) {
        if (evaluator instanceof UnifiedTerrainEvaluator unified) {
            unified.setDebugRecorder(recorder);
        }
    }

    private @Nullable BLibPath searchBlocksWithDiagnostics(
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor,
        PathSearchMode mode,
        @Nullable PathSearchDebugRecorder recorder,
        SearchConfig searchConfig,
        PathfindingTuning activeTuning
    ) {
        var totalStart = startTiming(recorder);
        var nodeResolutionStart = startTiming(recorder);
        var startNode = evaluator.getStartNode(startPos);
        var resolvedStartPos = new BlockPos(startNode.getX(), startNode.getY(), startNode.getZ());
        var goalNode = evaluator.getGoalNode(resolvedStartPos, targetPos);
        recordTiming(recorder, PathSearchTimingPhase.NODE_RESOLUTION, nodeResolutionStart);

        if (shouldUseBidirectionalSearch()) {
            markFeatureUsed(recorder, PathfindingFeature.BIDIRECTIONAL_SEARCH);
            return searchBlocksBidirectionalWithDiagnostics(
                startPos,
                targetPos,
                corridor,
                bidirectionalMode(mode),
                recorder,
                searchConfig,
                activeTuning,
                startNode,
                goalNode,
                totalStart
            );
        }

        var setupStart = startTiming(recorder);
        startNode.setGCost(0);
        startNode.setHCost(heuristic(startNode, goalNode, searchConfig));

        openSet.clear();
        closedNodes.clear();
        openSet.add(startNode);
        recordTiming(recorder, PathSearchTimingPhase.SEARCH_SETUP, setupStart);

        var visitedCount = 0;
        PathNode bestNode = startNode;

        while (!openSet.isEmpty() && visitedCount < searchConfig.maxSearchNodes()) {
            var pollStart = startTiming(recorder);
            var current = openSet.poll();
            recordTiming(recorder, PathSearchTimingPhase.OPEN_SET_POLL, pollStart);

            var closedRecordStart = startTiming(recorder);
            if (current.isClosed()) {
                reject(recorder, PathRejectionReason.ALREADY_CLOSED, current);
                recordTiming(recorder, PathSearchTimingPhase.CLOSED_NODE_RECORD, closedRecordStart);
                continue;
            }

            current.setClosed(true);
            if (recorder != null) {
                recorder.recordClosedNode(current, false);
            }

            if (debugEnabled) {
                closedNodes.add(current);
            }

            visitedCount++;
            recordTiming(recorder, PathSearchTimingPhase.CLOSED_NODE_RECORD, closedRecordStart);

            var goalTestStart = startTiming(recorder);
            if (current.equals(goalNode)) {
                recordTiming(recorder, PathSearchTimingPhase.GOAL_TEST, goalTestStart);
                var pathBuildStart = startTiming(recorder);
                var path = buildPath(current, true, searchConfig);
                recordTiming(recorder, PathSearchTimingPhase.PATH_BUILD, pathBuildStart);
                recordTiming(recorder, PathSearchTimingPhase.TOTAL_SEARCH, totalStart);
                lastSearchSnapshot = debugEnabled
                    ? buildSnapshot(
                        closedNodes,
                        path,
                        corridor,
                        visitedCount,
                        startPos,
                        targetPos,
                        startNode,
                        goalNode,
                        current,
                        mode,
                        PathSearchTermination.GOAL_REACHED,
                        recorder,
                        searchConfig
                    )
                    : null;

                return path;
            }
            recordTiming(recorder, PathSearchTimingPhase.GOAL_TEST, goalTestStart);

            var bestNodeUpdateStart = startTiming(recorder);
            if (current.distanceSquaredTo(goalNode) < bestNode.distanceSquaredTo(goalNode)) {
                bestNode = current;
            }
            recordTiming(recorder, PathSearchTimingPhase.BEST_NODE_UPDATE, bestNodeUpdateStart);

            var neighborGenerationStart = startTiming(recorder);
            var neighborCount = getNeighbors(current, neighborBuffer);
            recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_GENERATION, neighborGenerationStart);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighborBuffer[i];

                var filterStart = startTiming(recorder);
                if (neighbor.isClosed()) {
                    reject(recorder, PathRejectionReason.ALREADY_CLOSED, neighbor);
                    rejectEdge(recorder, current, neighbor, PathRejectionReason.ALREADY_CLOSED, false);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);
                    continue;
                }

                if (corridor != null && !SectionCorridorFinder.isInCorridor(neighbor, corridor)) {
                    reject(recorder, PathRejectionReason.OUTSIDE_CORRIDOR, neighbor);
                    rejectEdge(recorder, current, neighbor, PathRejectionReason.OUTSIDE_CORRIDOR, false);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);
                    continue;
                }
                recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);

                var edgeCostStart = startTiming(recorder);
                var edgeCost = current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType())
                    + neighbor.getPendingCostMalus();
                var tentativeG = current.getGCost() + edgeCost;
                recordTiming(recorder, PathSearchTimingPhase.EDGE_COSTING, edgeCostStart);

                var improvementFilterStart = startTiming(recorder);
                if (neighbor.getGCost() > 0 && tentativeG >= neighbor.getGCost() - activeTuning.minImprovement()) {
                    reject(recorder, PathRejectionReason.NOT_BETTER, neighbor);
                    rejectEdge(recorder, current, neighbor, PathRejectionReason.NOT_BETTER, false);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, improvementFilterStart);
                    continue;
                }
                recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, improvementFilterStart);

                var queueUpdateStart = startTiming(recorder);
                neighbor.setParent(current);
                neighbor.commitPendingTraversal();
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, goalNode, searchConfig));
                openSet.add(neighbor);
                recordOpenNode(recorder, neighbor, current, tentativeG, neighbor.getHCost(), false);
                recordTiming(recorder, PathSearchTimingPhase.QUEUE_UPDATE, queueUpdateStart);
            }
        }

        BLibPath path = null;

        if (features.partialPathResults() && bestNode != startNode) {
            markFeatureUsed(recorder, PathfindingFeature.PARTIAL_PATH_RESULTS);
            var pathBuildStart = startTiming(recorder);
            path = buildPath(bestNode, false, searchConfig);
            recordTiming(recorder, PathSearchTimingPhase.PATH_BUILD, pathBuildStart);
        }

        var termination = visitedCount >= searchConfig.maxSearchNodes()
            ? PathSearchTermination.BUDGET_EXHAUSTED
            : PathSearchTermination.OPEN_SET_EXHAUSTED;
        if (path == null && bestNode == startNode) {
            termination = PathSearchTermination.START_ONLY;
        }

        recordTiming(recorder, PathSearchTimingPhase.TOTAL_SEARCH, totalStart);
        lastSearchSnapshot = debugEnabled
            ? buildSnapshot(
                closedNodes,
                path,
                corridor,
                visitedCount,
                startPos,
                targetPos,
                startNode,
                goalNode,
                bestNode,
                mode,
                termination,
                recorder,
                searchConfig
            )
            : null;

        return path;
    }

    private boolean shouldUseBidirectionalSearch() {
        return features.bidirectionalSearch() && evaluator.supportsBidirectionalSearch();
    }

    private boolean shouldExpandForwardBidirectional(
        PriorityQueue<SearchRecord> forwardOpenSet,
        PriorityQueue<SearchRecord> backwardOpenSet,
        int visitedCount
    ) {
        if (forwardOpenSet.isEmpty()) {
            return false;
        }

        if (backwardOpenSet.isEmpty()) {
            return true;
        }

        if (features.balancedBidirectionalExpansion()) {
            return (visitedCount & 1) == 0;
        }

        return forwardOpenSet.peek().totalCost() <= backwardOpenSet.peek().totalCost();
    }

    private PathSearchMode bidirectionalMode(PathSearchMode mode) {
        return switch (mode) {
            case CORRIDOR -> PathSearchMode.BIDIRECTIONAL_CORRIDOR;
            case DIRECT_FALLBACK -> PathSearchMode.BIDIRECTIONAL_DIRECT_FALLBACK;
            case DIRECT -> PathSearchMode.BIDIRECTIONAL_DIRECT;
            case BIDIRECTIONAL_DIRECT, BIDIRECTIONAL_CORRIDOR, BIDIRECTIONAL_DIRECT_FALLBACK -> mode;
        };
    }

    private @Nullable BLibPath searchBlocksBidirectionalWithDiagnostics(
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor,
        PathSearchMode mode,
        @Nullable PathSearchDebugRecorder recorder,
        SearchConfig searchConfig,
        PathfindingTuning activeTuning,
        PathNode startNode,
        PathNode goalNode,
        long totalStart
    ) {
        var setupStart = startTiming(recorder);
        var forwardOpenSet = new PriorityQueue<SearchRecord>();
        var backwardOpenSet = new PriorityQueue<SearchRecord>();
        var forwardRecords = new HashMap<PathNode, SearchRecord>();
        var backwardRecords = new HashMap<PathNode, SearchRecord>();
        var forwardClosed = new HashSet<PathNode>();
        var backwardClosed = new HashSet<PathNode>();

        closedNodes.clear();

        var startRecord = new SearchRecord(startNode, null, 0.0f, heuristic(startNode, goalNode, searchConfig));
        var goalRecord = new SearchRecord(goalNode, null, 0.0f, heuristic(goalNode, startNode, searchConfig));

        forwardRecords.put(startNode, startRecord);
        backwardRecords.put(goalNode, goalRecord);
        forwardOpenSet.add(startRecord);
        backwardOpenSet.add(goalRecord);
        recordTiming(recorder, PathSearchTimingPhase.SEARCH_SETUP, setupStart);

        if (startNode.equals(goalNode)) {
            var materializeStart = startTiming(recorder);
            var nodes = materializePath(List.of(startNode), true, goalNode, searchConfig, recorder);
            recordTiming(recorder, PathSearchTimingPhase.PATH_MATERIALIZATION, materializeStart);
            var path = nodes != null ? new BLibPath(nodes, true) : null;
            recordTiming(recorder, PathSearchTimingPhase.TOTAL_SEARCH, totalStart);
            lastSearchSnapshot = debugEnabled
                ? buildSnapshot(
                    closedNodes,
                    path,
                    corridor,
                    0,
                    startPos,
                    targetPos,
                    startNode,
                    goalNode,
                    startNode,
                    mode,
                    PathSearchTermination.GOAL_REACHED,
                    recorder,
                    searchConfig
                )
                : null;

            return path;
        }

        if (features.balancedBidirectionalExpansion()) {
            markFeatureUsed(recorder, PathfindingFeature.BALANCED_BIDIRECTIONAL_EXPANSION);
        }

        var visitedCount = 0;
        var bestForwardRecord = startRecord;
        BidirectionalMeet meet = null;

        while (
            (!forwardOpenSet.isEmpty() || !backwardOpenSet.isEmpty())
                && visitedCount < searchConfig.maxSearchNodes()
        ) {
            var directionSelectStart = startTiming(recorder);
            var expandForward = shouldExpandForwardBidirectional(forwardOpenSet, backwardOpenSet, visitedCount);
            recordTiming(recorder, PathSearchTimingPhase.BIDIRECTIONAL_DIRECTION_SELECT, directionSelectStart);
            var expanded = expandForward
                ? expandBidirectionalSide(
                    forwardOpenSet,
                    forwardRecords,
                    backwardRecords,
                    forwardClosed,
                    corridor,
                    goalNode,
                    true,
                    recorder,
                    searchConfig,
                    activeTuning
                )
                : expandBidirectionalSide(
                    backwardOpenSet,
                    backwardRecords,
                    forwardRecords,
                    backwardClosed,
                    corridor,
                    startNode,
                    false,
                    recorder,
                    searchConfig,
                    activeTuning
                );

            if (expanded == null) {
                continue;
            }

            visitedCount++;

            if (expanded.forwardRecord() != null) {
                bestForwardRecord = closerToGoal(expanded.forwardRecord(), bestForwardRecord, goalNode)
                    ? expanded.forwardRecord()
                    : bestForwardRecord;
            }

            if (expanded.meet() != null) {
                meet = expanded.meet();
                break;
            }
        }

        BLibPath path = null;
        var bestNode = bestForwardRecord.node();
        var termination = PathSearchTermination.OPEN_SET_EXHAUSTED;

        if (meet != null) {
            var pathBuildStart = startTiming(recorder);
            var nodes = reconstructBidirectionalNodes(meet.forwardRecord(), meet.backwardRecord(), searchConfig);
            recordTiming(recorder, PathSearchTimingPhase.PATH_BUILD, pathBuildStart);
            var materializeStart = startTiming(recorder);
            nodes = materializePath(nodes, true, goalNode, searchConfig, recorder);
            recordTiming(recorder, PathSearchTimingPhase.PATH_MATERIALIZATION, materializeStart);
            path = nodes != null ? new BLibPath(nodes, true) : null;
            bestNode = meet.forwardRecord().node();
            termination = PathSearchTermination.GOAL_REACHED;
        } else {
            if (visitedCount >= searchConfig.maxSearchNodes()) {
                termination = PathSearchTermination.BUDGET_EXHAUSTED;
            } else if (bestForwardRecord == startRecord) {
                termination = PathSearchTermination.START_ONLY;
            }

            if (features.partialPathResults() && bestForwardRecord != startRecord) {
                markFeatureUsed(recorder, PathfindingFeature.PARTIAL_PATH_RESULTS);
                var pathBuildStart = startTiming(recorder);
                var nodes = reconstructForwardNodes(bestForwardRecord, searchConfig);
                recordTiming(recorder, PathSearchTimingPhase.PATH_BUILD, pathBuildStart);
                var materializeStart = startTiming(recorder);
                nodes = materializePath(nodes, false, goalNode, searchConfig, recorder);
                recordTiming(recorder, PathSearchTimingPhase.PATH_MATERIALIZATION, materializeStart);
                path = nodes != null ? new BLibPath(nodes, false) : null;
            }
        }

        recordTiming(recorder, PathSearchTimingPhase.TOTAL_SEARCH, totalStart);
        lastSearchSnapshot = debugEnabled
            ? buildSnapshot(
                closedNodes,
                path,
                corridor,
                visitedCount,
                startPos,
                targetPos,
                startNode,
                goalNode,
                bestNode,
                mode,
                termination,
                recorder,
                searchConfig
            )
            : null;

        return path;
    }

    private @Nullable BidirectionalExpansion expandBidirectionalSide(
        PriorityQueue<SearchRecord> openSet,
        HashMap<PathNode, SearchRecord> ownRecords,
        HashMap<PathNode, SearchRecord> otherRecords,
        HashSet<PathNode> ownClosed,
        @Nullable Set<Long> corridor,
        PathNode heuristicTarget,
        boolean forward,
        @Nullable PathSearchDebugRecorder recorder,
        SearchConfig searchConfig,
        PathfindingTuning activeTuning
    ) {
        while (!openSet.isEmpty()) {
            var pollStart = startTiming(recorder);
            var current = openSet.poll();
            recordTiming(recorder, PathSearchTimingPhase.OPEN_SET_POLL, pollStart);

            if (ownRecords.get(current.node()) != current) {
                continue;
            }

            var closedRecordStart = startTiming(recorder);
            if (!ownClosed.add(current.node())) {
                reject(recorder, PathRejectionReason.ALREADY_CLOSED, current.node());
                recordTiming(recorder, PathSearchTimingPhase.CLOSED_NODE_RECORD, closedRecordStart);
                continue;
            }

            if (recorder != null) {
                recorder.recordClosedNode(current.node(), !forward);
            }

            if (debugEnabled) {
                current.node().setGCost(current.gCost());
                current.node().setHCost(current.hCost());
                current.node().setClosed(true);
                closedNodes.add(current.node());
            }
            recordTiming(recorder, PathSearchTimingPhase.CLOSED_NODE_RECORD, closedRecordStart);

            var meetCheckStart = startTiming(recorder);
            var otherRecord = otherRecords.get(current.node());

            if (otherRecord != null) {
                recordTiming(recorder, PathSearchTimingPhase.BIDIRECTIONAL_MEET_CHECK, meetCheckStart);
                return new BidirectionalExpansion(
                    forward ? current : otherRecord,
                    new BidirectionalMeet(forward ? current : otherRecord, forward ? otherRecord : current)
                );
            }
            recordTiming(recorder, PathSearchTimingPhase.BIDIRECTIONAL_MEET_CHECK, meetCheckStart);

            var neighborGenerationStart = startTiming(recorder);
            var neighborCount = forward
                ? getNeighbors(current, neighborBuffer)
                : getPredecessors(current, neighborBuffer);
            recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_GENERATION, neighborGenerationStart);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighborBuffer[i];

                var filterStart = startTiming(recorder);
                if (ownClosed.contains(neighbor)) {
                    reject(recorder, PathRejectionReason.ALREADY_CLOSED, neighbor);
                    rejectEdge(recorder, current.node(), neighbor, PathRejectionReason.ALREADY_CLOSED, !forward);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);
                    continue;
                }

                if (corridor != null && !SectionCorridorFinder.isInCorridor(neighbor, corridor)) {
                    reject(recorder, PathRejectionReason.OUTSIDE_CORRIDOR, neighbor);
                    rejectEdge(recorder, current.node(), neighbor, PathRejectionReason.OUTSIDE_CORRIDOR, !forward);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);
                    continue;
                }
                recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, filterStart);

                var edgeCostStart = startTiming(recorder);
                var edgeCost = bidirectionalEdgeCost(current.node(), neighbor, forward);
                var tentativeG = current.gCost() + edgeCost;
                var existing = ownRecords.get(neighbor);
                recordTiming(recorder, PathSearchTimingPhase.EDGE_COSTING, edgeCostStart);

                var improvementFilterStart = startTiming(recorder);
                if (
                    existing != null
                        && tentativeG >= existing.gCost() - activeTuning.minImprovement()
                ) {
                    reject(recorder, PathRejectionReason.NOT_BETTER, neighbor);
                    rejectEdge(recorder, current.node(), neighbor, PathRejectionReason.NOT_BETTER, !forward);
                    recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, improvementFilterStart);
                    continue;
                }
                recordTiming(recorder, PathSearchTimingPhase.NEIGHBOR_FILTERING, improvementFilterStart);

                var queueUpdateStart = startTiming(recorder);
                var nextRecord = new SearchRecord(
                    neighbor,
                    current,
                    tentativeG,
                    heuristic(neighbor, heuristicTarget, searchConfig)
                );

                ownRecords.put(neighbor, nextRecord);
                openSet.add(nextRecord);
                recordOpenNode(recorder, neighbor, current.node(), tentativeG, nextRecord.hCost(), !forward);
                if (!forward && recorder != null) {
                    recorder.recordAcceptedEdge(current.node(), neighbor, edgeType(current.node(), neighbor), true);
                }
                recordTiming(recorder, PathSearchTimingPhase.QUEUE_UPDATE, queueUpdateStart);

                meetCheckStart = startTiming(recorder);
                otherRecord = otherRecords.get(neighbor);

                if (otherRecord != null) {
                    recordTiming(recorder, PathSearchTimingPhase.BIDIRECTIONAL_MEET_CHECK, meetCheckStart);
                    return new BidirectionalExpansion(
                        forward ? nextRecord : null,
                        new BidirectionalMeet(forward ? nextRecord : otherRecord, forward ? otherRecord : nextRecord)
                    );
                }
                recordTiming(recorder, PathSearchTimingPhase.BIDIRECTIONAL_MEET_CHECK, meetCheckStart);
            }

            return new BidirectionalExpansion(forward ? current : null, null);
        }

        return null;
    }

    private float bidirectionalEdgeCost(PathNode current, PathNode neighbor, boolean forward) {
        if (forward) {
            return current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType())
                + neighbor.getPendingCostMalus();
        }

        return neighbor.distanceTo(current) * evaluator.getTerrainCost(current.getTerrainType())
            + current.getPendingCostMalus();
    }

    private boolean closerToGoal(SearchRecord candidate, SearchRecord currentBest, PathNode goalNode) {
        return candidate.node().distanceSquaredTo(goalNode) < currentBest.node().distanceSquaredTo(goalNode);
    }

    private List<PathNode> reconstructBidirectionalNodes(
        SearchRecord forwardMeet,
        SearchRecord backwardMeet,
        SearchConfig searchConfig
    ) {
        var nodes = reconstructForwardNodes(forwardMeet, searchConfig);
        var current = backwardMeet.parent();

        while (current != null && nodes.size() < searchConfig.maxPathLength()) {
            nodes.add(current.node());
            current = current.parent();
        }

        return nodes;
    }

    private List<PathNode> reconstructForwardNodes(SearchRecord endRecord, SearchConfig searchConfig) {
        var nodes = new ArrayList<PathNode>();
        var current = endRecord;

        while (current != null && nodes.size() < searchConfig.maxPathLength()) {
            nodes.add(current.node());
            current = current.parent();
        }

        Collections.reverse(nodes);

        return nodes;
    }

    private @Nullable List<PathNode> materializePath(
        List<PathNode> nodes,
        boolean reached,
        PathNode goalNode,
        SearchConfig searchConfig,
        @Nullable PathSearchDebugRecorder recorder
    ) {
        if (nodes.isEmpty()) {
            return null;
        }

        var materialized = new ArrayList<PathNode>(Math.min(nodes.size(), searchConfig.maxPathLength()));
        var start = nodes.getFirst();

        start.setParent(null);
        start.setGCost(0.0f);
        start.setHCost(heuristic(start, goalNode, searchConfig));
        materialized.add(start);

        var totalCost = 0.0f;

        for (var index = 1; index < nodes.size() && materialized.size() < searchConfig.maxPathLength(); index++) {
            var from = nodes.get(index - 1);
            var to = nodes.get(index);
            var neighborCount = evaluator.getNeighbors(from, neighborBuffer);
            var committed = false;

            for (var neighborIndex = 0; neighborIndex < neighborCount; neighborIndex++) {
                var neighbor = neighborBuffer[neighborIndex];

                if (!neighbor.equals(to)) {
                    continue;
                }

                totalCost += from.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType())
                    + neighbor.getPendingCostMalus();
                neighbor.setParent(from);
                neighbor.commitPendingTraversal();
                neighbor.setGCost(totalCost);
                neighbor.setHCost(heuristic(neighbor, goalNode, searchConfig));
                materialized.add(neighbor);
                committed = true;
                break;
            }

            if (!committed) {
                return reached ? null : materialized;
            }
        }

        return materialized;
    }

    private PathSearchSnapshot buildSnapshot(
        List<PathNode> closedNodes,
        @Nullable BLibPath path,
        @Nullable Set<Long> corridor,
        int visitedCount,
        BlockPos startPos,
        BlockPos targetPos,
        PathNode startNode,
        PathNode goalNode,
        PathNode bestNode,
        PathSearchMode mode,
        PathSearchTermination termination,
        @Nullable PathSearchDebugRecorder recorder,
        SearchConfig searchConfig
    ) {
        var snapshotStart = startTiming(recorder);
        var pathIndexByNode = new HashMap<PathNode, Integer>();

        if (path != null) {
            for (int i = 0; i < path.getNodeCount(); i++) {
                pathIndexByNode.put(path.getNode(i), i);
            }
        }

        var entries = new ArrayList<DebugNodeEntry>(closedNodes.size());
        var closedNodeSet = new HashSet<>(closedNodes);

        for (var i = 0; i < closedNodes.size(); i++) {
            var node = closedNodes.get(i);
            entries.add(toDebugEntry(node, pathIndexByNode.getOrDefault(node, -1), i));
        }

        if (path != null) {
            for (int i = 0; i < path.getNodeCount(); i++) {
                var node = path.getNode(i);

                if (!closedNodeSet.contains(node)) {
                    entries.add(toDebugEntry(node, i, -1));
                }
            }
        }

        var reached = path != null && path.isReached();
        recordTiming(recorder, PathSearchTimingPhase.SNAPSHOT_BUILD, snapshotStart);
        var diagnostics = new PathSearchDebugData(
            mode,
            path == null ? PathSearchOutcome.FAILED : reached ? PathSearchOutcome.COMPLETE : PathSearchOutcome.PARTIAL,
            termination,
            PathDebugBlockPos.of(startPos),
            PathDebugBlockPos.of(targetPos),
            new PathDebugBlockPos(goalNode.getX(), goalNode.getY(), goalNode.getZ()),
            new PathDebugBlockPos(bestNode.getX(), bestNode.getY(), bestNode.getZ()),
            visitedCount,
            searchConfig.maxSearchNodes(),
            path != null ? path.getNodeCount() : 0,
            reached,
            corridor != null,
            corridor != null ? corridor.size() : 0,
            recorder != null ? recorder.rejectionSummary() : List.of(),
            recorder != null ? recorder.timingSummary() : List.of()
        );

        return new PathSearchSnapshot(
            entries,
            collectStableGroundEntries(path),
            corridor != null ? List.copyOf(corridor) : List.<Long>of(),
            recorder != null ? recorder.openNodeEntries() : List.of(),
            recorder != null ? recorder.edgeAttemptEntries() : List.of(),
            recorder != null ? recorder.clearanceBoxEntries() : List.of(),
            recorder != null ? recorder.supportFootprintEntries() : List.of(),
            recorder != null ? recorder.blockingBlockEntries() : List.of(),
            visitedCount,
            searchConfig.maxSearchNodes(),
            diagnostics
        );
    }

    private static List<StableGroundDebugEntry> collectStableGroundEntries(@Nullable BLibPath path) {
        if (path == null) {
            return List.of();
        }

        var stableGround = new LinkedHashMap<PathDebugBlockPos, StableGroundDebugEntry>();

        for (int i = 0; i < path.getNodeCount(); i++) {
            var node = path.getNode(i);
            if (!node.hasStableGround()) {
                continue;
            }

            for (int dx = 0; dx < node.getStableGroundXSize(); dx++) {
                for (int dz = 0; dz < node.getStableGroundZSize(); dz++) {
                    var pos = new PathDebugBlockPos(
                        node.getStableGroundX() + dx,
                        node.getStableGroundY(),
                        node.getStableGroundZ() + dz
                    );
                    stableGround.putIfAbsent(pos, new StableGroundDebugEntry(pos.x(), pos.y(), pos.z(), i));
                }
            }
        }

        return List.copyOf(stableGround.values());
    }

    private static DebugNodeEntry toDebugEntry(PathNode node, int pathIndex, int expansionOrder) {
        var parent = node.getParent() != null
            ? new PathDebugBlockPos(node.getParent().getX(), node.getParent().getY(), node.getParent().getZ())
            : PathDebugBlockPos.NONE;
        return new DebugNodeEntry(
            node.getX(),
            node.getY(),
            node.getZ(),
            node.getTerrainType().ordinal(),
            pathIndex,
            node.getGCost(),
            node.getHCost(),
            node.getCostMalus(),
            expansionOrder,
            parent,
            blockBreakPlan(node)
        );
    }

    private static List<PathDebugBlockPos> blockBreakPlan(PathNode node) {
        if (!node.requiresBlockBreaking()) {
            return List.of();
        }

        var blocks = new ArrayList<PathDebugBlockPos>(node.getBlockBreakPlan().size());

        for (var block : node.getBlockBreakPlan().blocks()) {
            blocks.add(new PathDebugBlockPos(block.getX(), block.getY(), block.getZ()));
        }

        return List.copyOf(blocks);
    }

    private static void reject(@Nullable PathSearchDebugRecorder recorder, PathRejectionReason reason, PathNode node) {
        if (recorder != null) {
            recorder.reject(reason, node.getX(), node.getY(), node.getZ());
        }
    }

    private static long startTiming(@Nullable PathSearchDebugRecorder recorder) {
        return recorder != null ? recorder.startTiming() : 0L;
    }

    private static void recordTiming(
        @Nullable PathSearchDebugRecorder recorder,
        PathSearchTimingPhase phase,
        long startNanos
    ) {
        if (recorder != null) {
            recorder.recordTiming(phase, startNanos);
        }
    }

    private static void rejectEdge(
        @Nullable PathSearchDebugRecorder recorder,
        PathNode from,
        PathNode to,
        PathRejectionReason reason,
        boolean backward
    ) {
        if (recorder != null) {
            recorder.recordRejectedEdge(from, to, edgeType(from, to), reason, backward);
        }
    }

    private static void recordOpenNode(
        @Nullable PathSearchDebugRecorder recorder,
        PathNode node,
        PathNode parent,
        float gCost,
        float hCost,
        boolean backward
    ) {
        if (recorder != null) {
            recorder.recordOpenNode(node, parent, gCost, hCost, backward);
        }
    }

    private static PathEdgeDebugType edgeType(PathNode from, PathNode to) {
        if (to.requiresBlockBreaking()) {
            return PathEdgeDebugType.BLOCK_BREAKING;
        }

        if (from.getTerrainType() == TerrainType.WATER || to.getTerrainType() == TerrainType.WATER) {
            return from.getY() == to.getY() ? PathEdgeDebugType.WATER_TRAVEL : PathEdgeDebugType.WATER_VERTICAL;
        }

        if (to.getY() > from.getY()) {
            return PathEdgeDebugType.STEP_UP;
        }

        if (to.getY() < from.getY()) {
            return PathEdgeDebugType.STEP_DOWN;
        }

        return PathEdgeDebugType.SAME_LEVEL;
    }

    private static void markFeatureUsed(@Nullable PathSearchDebugRecorder recorder, PathfindingFeature feature) {
        if (recorder != null) {
            recorder.markFeatureUsed(feature);
        }
    }

    private int getNeighbors(PathNode current, PathNode[] neighbors) {
        if (!features.parentEdgePruning() || current.getParent() == null) {
            return evaluator.getNeighbors(current, neighbors);
        }

        return evaluator.getNeighbors(current, current.getParent(), neighbors);
    }

    private int getNeighbors(SearchRecord current, PathNode[] neighbors) {
        if (!features.parentEdgePruning() || current.parent() == null) {
            return evaluator.getNeighbors(current.node(), neighbors);
        }

        return evaluator.getNeighbors(current.node(), current.parent().node(), neighbors);
    }

    private int getPredecessors(SearchRecord current, PathNode[] predecessors) {
        if (!features.parentEdgePruning() || current.parent() == null) {
            return evaluator.getPredecessors(current.node(), predecessors);
        }

        return evaluator.getPredecessors(current.node(), current.parent().node(), predecessors);
    }

    private float heuristic(PathNode from, PathNode to, SearchConfig searchConfig) {
        var dx = (float) (to.getX() - from.getX());
        var dy = (float) (to.getY() - from.getY()) * searchConfig.elevationWeight();
        var dz = (float) (to.getZ() - from.getZ());

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * searchConfig.heuristicWeight();
    }

    private record BidirectionalExpansion(
        @Nullable SearchRecord forwardRecord,
        @Nullable BidirectionalMeet meet
    ) {}

    private record BidirectionalMeet(
        SearchRecord forwardRecord,
        SearchRecord backwardRecord
    ) {}

    private record SearchRecord(
        PathNode node,
        @Nullable SearchRecord parent,
        float gCost,
        float hCost
    ) implements Comparable<SearchRecord> {

        private float totalCost() {
            return gCost + hCost;
        }

        @Override
        public int compareTo(SearchRecord other) {
            return Float.compare(totalCost(), other.totalCost());
        }
    }

    // --- Path post-processing pipeline ---

    private BLibPath buildPath(PathNode endNode, boolean reached, SearchConfig searchConfig) {
        var nodes = reconstructNodes(endNode, searchConfig);

        return new BLibPath(nodes, reached);
    }

    private List<PathNode> reconstructNodes(PathNode endNode, SearchConfig searchConfig) {
        var nodes = new ArrayList<PathNode>();
        var current = endNode;

        while (current != null && nodes.size() < searchConfig.maxPathLength()) {
            nodes.add(current);
            current = current.getParent();
        }

        Collections.reverse(nodes);

        return nodes;
    }
}
