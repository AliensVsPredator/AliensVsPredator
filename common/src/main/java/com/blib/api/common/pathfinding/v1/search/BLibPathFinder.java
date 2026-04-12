package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * the pathfinder first runs a fast section-level A* to identify a corridor of 16x16x16 sections, then runs the
 * block-level A* restricted to that corridor.
 */
public final class BLibPathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibPathFinder.class);

    private static final int MAX_NEIGHBORS = 40;

    private static final int MAX_SECTION_SEARCH_NODES = 128;

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

    private @Nullable PathSearchSnapshot lastSearchSnapshot;

    // --- Pooled search data structures (#6) ---
    private final PriorityQueue<PathNode> openSet = new PriorityQueue<>();

    private final ArrayList<PathNode> closedNodes = new ArrayList<>();

    private final PathNode[] neighborBuffer = new PathNode[MAX_NEIGHBORS];

    private boolean debugEnabled;

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config) {
        this(evaluator, config, null);
    }

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.evaluator = evaluator;
        this.config = config;
        this.classificationCache = classificationCache;
    }

    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshot;
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        var start = System.nanoTime();

        debugEnabled = BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)
            && BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED);

        evaluator.prepare(level);

        try {
            Set<Long> corridor = null;

            if (classificationCache != null) {
                corridor = findSectionCorridor(level, startPos, targetPos);

                // Section search couldn't reach the goal — target is unreachable.
                if (corridor == null) {
                    lastSearchSnapshot = null;
                    return null;
                }

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

        if (classificationCache != null) {
            corridor = findSectionCorridor(level, startPos, targetPos);

            if (corridor == null) {
                unifiedEvaluator.cleanup();
                return CompletableFuture.completedFuture(null);
            }

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

    // --- Section-level A* ---

    private Set<Long> findSectionCorridor(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        var startSX = startPos.getX() >> 4;
        var startSY = startPos.getY() >> 4;
        var startSZ = startPos.getZ() >> 4;
        var goalSX = targetPos.getX() >> 4;
        var goalSY = targetPos.getY() >> 4;
        var goalSZ = targetPos.getZ() >> 4;

        var startKey = packSectionKey(startSX, startSY, startSZ);
        var goalKey = packSectionKey(goalSX, goalSY, goalSZ);

        record SectionEntry(
            long key,
            int x,
            int y,
            int z,
            float gCost,
            float fCost,
            @Nullable SectionEntry parent
        ) {}

        var openSet = new PriorityQueue<SectionEntry>(Comparator.comparingDouble(SectionEntry::fCost));
        var closedSet = new HashSet<Long>();

        var startH = sectionDistance(startSX, startSY, startSZ, goalSX, goalSY, goalSZ);
        openSet.add(new SectionEntry(startKey, startSX, startSY, startSZ, 0, startH, null));

        SectionEntry bestEntry = null;
        var visitedCount = 0;

        while (!openSet.isEmpty() && visitedCount < MAX_SECTION_SEARCH_NODES) {
            var current = openSet.poll();

            if (closedSet.contains(current.key())) {
                continue;
            }

            closedSet.add(current.key());
            visitedCount++;

            var currentDist = sectionDistance(current.x(), current.y(), current.z(), goalSX, goalSY, goalSZ);
            var bestDist = bestEntry == null
                ? Float.MAX_VALUE
                : sectionDistance(bestEntry.x(), bestEntry.y(), bestEntry.z(), goalSX, goalSY, goalSZ);

            if (currentDist < bestDist) {
                bestEntry = current;
            }

            if (current.key() == goalKey) {
                bestEntry = current;
                break;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        // Only cardinal directions for sections (no diagonals).
                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != 1) {
                            continue;
                        }

                        var nx = current.x() + dx;
                        var ny = current.y() + dy;
                        var nz = current.z() + dz;
                        var nKey = packSectionKey(nx, ny, nz);

                        if (closedSet.contains(nKey)) {
                            continue;
                        }

                        var sectionPassable = classificationCache.isSectionPassable(level, nx, ny, nz);
                        var supportsBreakable = evaluator.getTerrainCost(TerrainType.BREAKABLE) < Float.MAX_VALUE;

                        if (!sectionPassable && !supportsBreakable) {
                            continue;
                        }

                        var cheapestCost = Float.MAX_VALUE;

                        if (sectionPassable) {
                            var terrainTypes = classificationCache.getSectionTerrainTypes(level, nx, ny, nz);

                            for (var terrainType : terrainTypes) {
                                var cost = evaluator.getTerrainCost(terrainType);

                                if (cost < cheapestCost) {
                                    cheapestCost = cost;
                                }
                            }
                        }

                        if (supportsBreakable) {
                            var breakableCost = evaluator.getTerrainCost(TerrainType.BREAKABLE);

                            if (breakableCost < cheapestCost) {
                                cheapestCost = breakableCost;
                            }
                        }

                        if (cheapestCost == Float.MAX_VALUE) {
                            continue;
                        }

                        var g = current.gCost() + cheapestCost;
                        var h = sectionDistance(nx, ny, nz, goalSX, goalSY, goalSZ);

                        openSet.add(new SectionEntry(nKey, nx, ny, nz, g, g + h, current));
                    }
                }
            }
        }

        // If the section search couldn't reach the goal section, the target is unreachable.
        if (bestEntry == null || bestEntry.key() != goalKey) {
            return null;
        }

        // Build corridor from path sections with a 1-section buffer.
        var corePath = new ArrayList<long[]>();
        var entry = bestEntry;

        while (entry != null) {
            corePath.add(new long[] { entry.x(), entry.y(), entry.z() });
            entry = entry.parent();
        }

        corePath.add(new long[] { startSX, startSY, startSZ });
        corePath.add(new long[] { goalSX, goalSY, goalSZ });

        var corridor = new HashSet<Long>();

        for (var sectionCoords : corePath) {
            var sx = (int) sectionCoords[0];
            var sy = (int) sectionCoords[1];
            var sz = (int) sectionCoords[2];

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        corridor.add(packSectionKey(sx + dx, sy + dy, sz + dz));
                    }
                }
            }
        }

        return corridor;
    }

    private static float sectionDistance(int ax, int ay, int az, int bx, int by, int bz) {
        int dx = bx - ax, dy = by - ay, dz = bz - az;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
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

                if (corridor != null && !isInCorridor(neighbor, corridor)) {
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

    private static boolean isInCorridor(PathNode node, Set<Long> corridor) {
        var sectionKey = packSectionKey(node.getX() >> 4, node.getY() >> 4, node.getZ() >> 4);

        return corridor.contains(sectionKey);
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

    private static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38 | ((long) sectionY & 0xFFFL) << 26 | ((long) sectionZ & 0x3FFFFFFL);
    }
}
