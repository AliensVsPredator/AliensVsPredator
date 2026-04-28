package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.mod.common.property.BLibModProperties;
import com.blib.mod.common.property.BLibModPropertyAccess;

/**
 * A* pathfinding with optional two-level hierarchical search. When a {@link TerrainClassificationCache} is provided,
 * the pathfinder first runs a fast section-level A* via {@link SectionCorridorFinder} to identify a corridor of 16x16x16
 * sections, then runs the block-level A* restricted to that corridor.
 */
public final class BLibPathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibPathFinder.class);

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

    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshot;
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        var start = System.nanoTime();

        debugEnabled = BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)
            && BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED);

        evaluator.prepare(level);
        applyExcludedTerrains();

        try {
            Set<Long> corridor = null;

            if (corridorFinder != null) {
                var result = corridorFinder.findCorridor(level, startPos, targetPos);

                // Section search couldn't reach the goal — target is unreachable.
                if (result == null) {
                    lastSearchSnapshot = null;
                    return null;
                }

                corridor = result.corridor();

                // For short distances, skip the corridor constraint (let block search expand freely)
                // but still benefit from the reachability check above.
                if (startPos.distManhattan(targetPos) <= CORRIDOR_DISTANCE_THRESHOLD) {
                    corridor = null;
                }
            }

            var path = searchBlocks(startPos, targetPos, corridor);
            var ms = (System.nanoTime() - start) / 1_000_000.0;

            LOGGER.info(
                "[Pathfinding] {}ms | {} -> {} dist={} result={} nodes={}",
                "%.3f".formatted(ms),
                startPos,
                targetPos,
                startPos.distManhattan(targetPos),
                path != null ? (path.isReached() ? "REACHED" : "PARTIAL") : "NONE",
                path != null ? path.getNodeCount() : 0
            );

            return path;
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

        debugEnabled = BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)
            && BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED);

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

        if (corridorFinder != null) {
            var result = corridorFinder.findCorridor(level, startPos, targetPos);

            if (result == null) {
                unifiedEvaluator.cleanup();
                return CompletableFuture.completedFuture(null);
            }

            corridor = result.corridor();

            if (startPos.distManhattan(targetPos) <= CORRIDOR_DISTANCE_THRESHOLD) {
                corridor = null;
            }
        }

        // --- Background thread: run the search ---

        var capturedCorridor = corridor;

        return CompletableFuture.supplyAsync(() -> {
            try {
                return searchBlocks(startPos, targetPos, capturedCorridor);
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
     * Runs a block-level A* search using a pre-computed corridor constraint. Use this with corridors obtained from
     * {@link #computeCorridor} for segmented long-distance pathfinding.
     */
    public @Nullable BLibPath findPathInCorridor(
        LevelReader level,
        BlockPos startPos,
        BlockPos targetPos,
        @Nullable Set<Long> corridor
    ) {
        var start = System.nanoTime();

        debugEnabled = BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)
            && BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED);

        evaluator.prepare(level);
        applyExcludedTerrains();

        try {
            var path = searchBlocks(startPos, targetPos, corridor);
            var ms = (System.nanoTime() - start) / 1_000_000.0;

            LOGGER.info(
                "[Pathfinding/Segment] {}ms | {} -> {} dist={} result={} nodes={}",
                "%.3f".formatted(ms),
                startPos,
                targetPos,
                startPos.distManhattan(targetPos),
                path != null ? (path.isReached() ? "REACHED" : "PARTIAL") : "NONE",
                path != null ? path.getNodeCount() : 0
            );

            return path;
        } finally {
            evaluator.cleanup();
        }
    }

    private void applyExcludedTerrains() {
        if (excludedTerrains != null && !excludedTerrains.isEmpty() && evaluator instanceof UnifiedTerrainEvaluator unified) {
            unified.excludeTerrains(excludedTerrains);
        }
    }

    private @Nullable BLibPath searchBlocks(BlockPos startPos, BlockPos targetPos, @Nullable Set<Long> corridor) {
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
                continue;
            }

            current.setClosed(true);

            if (debugEnabled) {
                closedNodes.add(current);
            }

            visitedCount++;

            if (current.equals(goalNode)) {
                var path = buildPath(current, true);
                lastSearchSnapshot = debugEnabled ? buildSnapshot(closedNodes, path, corridor, visitedCount) : null;

                return path;
            }

            if (current.distanceSquaredTo(goalNode) < bestNode.distanceSquaredTo(goalNode)) {
                bestNode = current;
            }

            var neighborCount = evaluator.getNeighbors(current, neighborBuffer);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighborBuffer[i];

                if (neighbor.isClosed()) {
                    continue;
                }

                if (corridor != null && !SectionCorridorFinder.isInCorridor(neighbor, corridor)) {
                    continue;
                }

                var edgeCost = current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType())
                    + neighbor.getCostMalus();
                var tentativeG = current.getGCost() + edgeCost;

                if (neighbor.getGCost() > 0 && tentativeG >= neighbor.getGCost() - MIN_IMPROVEMENT) {
                    continue;
                }

                neighbor.setParent(current);
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, goalNode));
                openSet.add(neighbor);
            }
        }

        BLibPath path = null;

        if (bestNode != startNode) {
            path = buildPath(bestNode, false);
        }

        lastSearchSnapshot = debugEnabled ? buildSnapshot(closedNodes, path, corridor, visitedCount) : null;

        return path;
    }

    private PathSearchSnapshot buildSnapshot(
        List<PathNode> closedNodes,
        @Nullable BLibPath path,
        @Nullable Set<Long> corridor,
        int visitedCount
    ) {
        var pathIndexByNode = new HashMap<PathNode, Integer>();

        if (path != null) {
            for (int i = 0; i < path.getNodeCount(); i++) {
                pathIndexByNode.put(path.getNode(i), i);
            }
        }

        var entries = new ArrayList<DebugNodeEntry>(closedNodes.size());
        var closedNodeSet = new HashSet<>(closedNodes);

        for (var node : closedNodes) {
            entries.add(
                new DebugNodeEntry(
                    node.getX(),
                    node.getY(),
                    node.getZ(),
                    node.getTerrainType().ordinal(),
                    pathIndexByNode.getOrDefault(node, -1)
                )
            );
        }

        if (path != null) {
            for (int i = 0; i < path.getNodeCount(); i++) {
                var node = path.getNode(i);

                if (!closedNodeSet.contains(node)) {
                    entries.add(
                        new DebugNodeEntry(
                            node.getX(),
                            node.getY(),
                            node.getZ(),
                            node.getTerrainType().ordinal(),
                            i
                        )
                    );
                }
            }
        }

        return new PathSearchSnapshot(
            entries,
            corridor != null ? List.copyOf(corridor) : List.<Long>of(),
            visitedCount,
            config.maxSearchNodes()
        );
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
