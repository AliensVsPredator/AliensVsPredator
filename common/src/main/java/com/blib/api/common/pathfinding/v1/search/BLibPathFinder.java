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
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathDebugBlockPos;
import com.blib.api.common.pathfinding.v1.debug.PathRejectionReason;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugData;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugRecorder;
import com.blib.api.common.pathfinding.v1.debug.PathSearchMode;
import com.blib.api.common.pathfinding.v1.debug.PathSearchOutcome;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.debug.PathSearchTermination;
import com.blib.api.common.pathfinding.v1.debug.StableGroundDebugEntry;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A* pathfinding with optional two-level hierarchical search. When a {@link TerrainClassificationCache} is provided,
 * the pathfinder first runs a fast section-level A* via {@link SectionCorridorFinder} to identify a corridor of
 * 16x16x16 sections, then runs the block-level A* restricted to that corridor.
 */
public final class BLibPathFinder {

    private static final int MAX_NEIGHBORS = 40;

    private static final int CORRIDOR_DISTANCE_THRESHOLD = 48;

    private static final float MIN_IMPROVEMENT = 0.01f;

    private static final int ASYNC_CHUNK_MARGIN = 2;

    private static final ExecutorService PATHFINDING_EXECUTOR = Executors.newFixedThreadPool(
        Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
        runnable -> {
            var thread = new Thread(runnable, "BLib-Pathfinding");
            thread.setDaemon(true);
            return thread;
        }
    );

    private final TerrainEvaluator evaluator;

    private final SearchConfig config;

    private final @Nullable TerrainClassificationCache classificationCache;

    private final @Nullable SectionCorridorFinder corridorFinder;

    private @Nullable PathSearchSnapshot lastSearchSnapshot;

    // --- Pooled search data structures ---
    private final PriorityQueue<PathNode> openSet = new PriorityQueue<>();

    private final ArrayList<PathNode> closedNodes = new ArrayList<>();

    private final PathNode[] neighborBuffer = new PathNode[MAX_NEIGHBORS];

    private boolean debugEnabled;

    private boolean debugCaptureEnabled;

    private @Nullable Set<TerrainType> excludedTerrains;

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config) {
        this(evaluator, config, null);
    }

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.evaluator = evaluator;
        this.config = config;
        this.classificationCache = classificationCache;
        this.corridorFinder = classificationCache != null
            ? new SectionCorridorFinder(classificationCache, evaluator)
            : null;
    }

    public void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains) {
        this.excludedTerrains = excludedTerrains;
    }

    public void setDebugCaptureEnabled(boolean debugCaptureEnabled) {
        this.debugCaptureEnabled = debugCaptureEnabled;
    }

    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshot;
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        debugEnabled = debugCaptureEnabled;

        evaluator.prepare(level);
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
        if (!(evaluator instanceof UnifiedTerrainEvaluator unifiedEvaluator)) {
            return CompletableFuture.completedFuture(findPath(level, startPos, targetPos));
        }

        debugEnabled = debugCaptureEnabled;

        // --- Main thread: snapshot chunks and pre-populate terrain cache ---

        var minCX = Math.min(startPos.getX(), targetPos.getX()) >> 4;
        var minCZ = Math.min(startPos.getZ(), targetPos.getZ()) >> 4;
        var maxCX = Math.max(startPos.getX(), targetPos.getX()) >> 4;
        var maxCZ = Math.max(startPos.getZ(), targetPos.getZ()) >> 4;

        unifiedEvaluator.prepareAsync();
        applyExcludedTerrains();

        for (int cx = minCX - ASYNC_CHUNK_MARGIN; cx <= maxCX + ASYNC_CHUNK_MARGIN; cx++) {
            for (int cz = minCZ - ASYNC_CHUNK_MARGIN; cz <= maxCZ + ASYNC_CHUNK_MARGIN; cz++) {
                unifiedEvaluator.preloadChunk(cx, cz, level.getChunk(cx, cz));
            }
        }

        if (classificationCache != null) {
            var margin = ASYNC_CHUNK_MARGIN * 16;
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
        if (corridorFinder == null) {
            return null;
        }

        evaluator.prepare(level);
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
        applyExcludedTerrains();

        try {
            return searchBlocks(startPos, targetPos, corridor, corridor != null ? PathSearchMode.CORRIDOR : PathSearchMode.DIRECT);
        } finally {
            evaluator.cleanup();
        }
    }

    private void applyExcludedTerrains() {
        if (excludedTerrains != null && !excludedTerrains.isEmpty() && evaluator instanceof UnifiedTerrainEvaluator unified) {
            unified.excludeTerrains(excludedTerrains);
        }
    }

    private boolean shouldUseCorridor(BlockPos startPos, BlockPos targetPos) {
        return corridorFinder != null && startPos.distManhattan(targetPos) > CORRIDOR_DISTANCE_THRESHOLD;
    }

    private @Nullable BLibPath searchBlocks(
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor,
        PathSearchMode mode
    ) {
        var recorder = debugEnabled ? new PathSearchDebugRecorder() : null;
        setEvaluatorDebugRecorder(recorder);

        try {
            return searchBlocksWithDiagnostics(startPos, targetPos, corridor, mode, recorder);
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
        @Nullable PathSearchDebugRecorder recorder
    ) {
        var startNode = evaluator.getStartNode(startPos);
        var goalNode = evaluator.getGoalNode(targetPos);

        startNode.setGCost(0);
        startNode.setHCost(heuristic(startNode, goalNode));

        openSet.clear();
        closedNodes.clear();
        openSet.add(startNode);

        var visitedCount = 0;
        PathNode bestNode = startNode;

        while (!openSet.isEmpty() && visitedCount < config.maxSearchNodes()) {
            var current = openSet.poll();

            if (current.isClosed()) {
                reject(recorder, PathRejectionReason.ALREADY_CLOSED, current);
                continue;
            }

            current.setClosed(true);

            if (debugEnabled) {
                closedNodes.add(current);
            }

            visitedCount++;

            if (current.equals(goalNode)) {
                var path = buildPath(current, true);
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
                        recorder
                    )
                    : null;

                return path;
            }

            if (current.distanceSquaredTo(goalNode) < bestNode.distanceSquaredTo(goalNode)) {
                bestNode = current;
            }

            var neighborCount = evaluator.getNeighbors(current, neighborBuffer);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighborBuffer[i];

                if (neighbor.isClosed()) {
                    reject(recorder, PathRejectionReason.ALREADY_CLOSED, neighbor);
                    continue;
                }

                if (corridor != null && !SectionCorridorFinder.isInCorridor(neighbor, corridor)) {
                    reject(recorder, PathRejectionReason.OUTSIDE_CORRIDOR, neighbor);
                    continue;
                }

                var edgeCost = current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType())
                    + neighbor.getPendingCostMalus();
                var tentativeG = current.getGCost() + edgeCost;

                if (neighbor.getGCost() > 0 && tentativeG >= neighbor.getGCost() - MIN_IMPROVEMENT) {
                    reject(recorder, PathRejectionReason.NOT_BETTER, neighbor);
                    continue;
                }

                neighbor.setParent(current);
                neighbor.commitPendingTraversal();
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, goalNode));
                openSet.add(neighbor);
            }
        }

        BLibPath path = null;

        if (bestNode != startNode) {
            path = buildPath(bestNode, false);
        }

        var termination = visitedCount >= config.maxSearchNodes()
            ? PathSearchTermination.BUDGET_EXHAUSTED
            : PathSearchTermination.OPEN_SET_EXHAUSTED;
        if (path == null && bestNode == startNode) {
            termination = PathSearchTermination.START_ONLY;
        }

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
                recorder
            )
            : null;

        return path;
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
        @Nullable PathSearchDebugRecorder recorder
    ) {
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
        var diagnostics = new PathSearchDebugData(
            mode,
            path == null ? PathSearchOutcome.FAILED : reached ? PathSearchOutcome.COMPLETE : PathSearchOutcome.PARTIAL,
            termination,
            PathDebugBlockPos.of(startPos),
            PathDebugBlockPos.of(targetPos),
            new PathDebugBlockPos(goalNode.getX(), goalNode.getY(), goalNode.getZ()),
            new PathDebugBlockPos(bestNode.getX(), bestNode.getY(), bestNode.getZ()),
            visitedCount,
            config.maxSearchNodes(),
            path != null ? path.getNodeCount() : 0,
            reached,
            corridor != null,
            corridor != null ? corridor.size() : 0,
            recorder != null ? recorder.rejectionSummary() : List.of()
        );

        return new PathSearchSnapshot(
            entries,
            collectStableGroundEntries(path),
            corridor != null ? List.copyOf(corridor) : List.<Long>of(),
            visitedCount,
            config.maxSearchNodes(),
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

            var pos = new PathDebugBlockPos(node.getStableGroundX(), node.getStableGroundY(), node.getStableGroundZ());
            stableGround.putIfAbsent(pos, new StableGroundDebugEntry(pos.x(), pos.y(), pos.z(), i));
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
            parent
        );
    }

    private static void reject(@Nullable PathSearchDebugRecorder recorder, PathRejectionReason reason, PathNode node) {
        if (recorder != null) {
            recorder.reject(reason, node.getX(), node.getY(), node.getZ());
        }
    }

    private float heuristic(PathNode from, PathNode to) {
        var dx = (float) (to.getX() - from.getX());
        var dy = (float) (to.getY() - from.getY()) * config.elevationWeight();
        var dz = (float) (to.getZ() - from.getZ());

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * config.heuristicWeight();
    }

    // --- Path post-processing pipeline ---

    private BLibPath buildPath(PathNode endNode, boolean reached) {
        var nodes = reconstructNodes(endNode);

        return new BLibPath(nodes, reached);
    }

    private List<PathNode> reconstructNodes(PathNode endNode) {
        var nodes = new ArrayList<PathNode>();
        var current = endNode;

        while (current != null && nodes.size() < config.maxPathLength()) {
            nodes.add(current);
            current = current.getParent();
        }

        Collections.reverse(nodes);

        return nodes;
    }
}
