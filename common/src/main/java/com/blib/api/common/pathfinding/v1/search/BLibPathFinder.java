package com.blib.api.common.pathfinding.v1.search;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A* pathfinding with optional two-level hierarchical search.
 * When a {@link TerrainClassificationCache} is provided, the pathfinder first runs
 * a fast section-level A* to identify a corridor of 16x16x16 sections, then runs
 * the block-level A* restricted to that corridor. This prevents budget waste on
 * irrelevant areas.
 */
public final class BLibPathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibPathFinder.class);

    private static final int MAX_NEIGHBORS = 30;

    private static final int MAX_SECTION_SEARCH_NODES = 128;

    private final TerrainEvaluator evaluator;

    private final SearchConfig config;

    private final @Nullable TerrainClassificationCache classificationCache;

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config) {
        this(evaluator, config, null);
    }

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.evaluator = evaluator;
        this.config = config;
        this.classificationCache = classificationCache;
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        evaluator.prepare(level);

        try {
            Set<Long> corridor = null;

            if (classificationCache != null) {
                corridor = findSectionCorridor(level, startPos, targetPos);
            }

            return searchBlocks(startPos, targetPos, corridor);
        } finally {
            evaluator.cleanup();
        }
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

        record SectionEntry(long key, int x, int y, int z, float gCost, float fCost, @Nullable SectionEntry parent) {}

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

            if (bestEntry == null || sectionDistance(current.x(), current.y(), current.z(), goalSX, goalSY, goalSZ)
                < sectionDistance(bestEntry.x(), bestEntry.y(), bestEntry.z(), goalSX, goalSY, goalSZ)) {
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

        // Build corridor from exact path sections only — no buffer.
        var corridor = new HashSet<Long>();
        var entry = bestEntry;

        while (entry != null) {
            corridor.add(packSectionKey(entry.x(), entry.y(), entry.z()));
            entry = entry.parent();
        }

        corridor.add(startKey);
        corridor.add(goalKey);

        return corridor;
    }

    private static float sectionDistance(int ax, int ay, int az, int bx, int by, int bz) {
        var dx = bx - ax;
        var dy = by - ay;
        var dz = bz - az;

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // --- Block-level A* ---

    private @Nullable BLibPath searchBlocks(BlockPos startPos, BlockPos targetPos, @Nullable Set<Long> corridor) {
        var startNode = evaluator.getStartNode(startPos);
        var goalNode = evaluator.getGoalNode(targetPos);

        startNode.setGCost(0);
        startNode.setHCost(heuristic(startNode, goalNode));

        var openSet = new PriorityQueue<PathNode>(Comparator.comparingDouble(PathNode::totalCost));
        openSet.add(startNode);

        var neighbors = new PathNode[MAX_NEIGHBORS];
        var visitedCount = 0;
        PathNode bestNode = startNode;

        while (!openSet.isEmpty() && visitedCount < config.maxSearchNodes()) {
            var current = openSet.poll();

            if (current.isClosed()) {
                continue;
            }

            current.setClosed(true);
            visitedCount++;

            if (current.equals(goalNode)) {
                return reconstructPath(current, true);
            }

            if (current.distanceSquaredTo(goalNode) < bestNode.distanceSquaredTo(goalNode)) {
                bestNode = current;
            }

            var neighborCount = evaluator.getNeighbors(current, neighbors);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighbors[i];

                if (neighbor.isClosed()) {
                    continue;
                }

                if (corridor != null && !isInCorridor(neighbor, corridor)) {
                    continue;
                }

                var edgeCost = current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType()) + neighbor.getCostMalus();
                var tentativeG = current.getGCost() + edgeCost;

                if (tentativeG >= neighbor.getGCost() && neighbor.getGCost() > 0) {
                    continue;
                }

                neighbor.setParent(current);
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, goalNode));
                openSet.add(neighbor);
            }
        }

        LOGGER.info("[A*] Visited {}/{} nodes | budget exhausted={} | corridor={}",
            visitedCount, config.maxSearchNodes(), visitedCount >= config.maxSearchNodes(),
            corridor != null ? corridor.size() + " sections" : "none");

        if (bestNode != startNode) {
            return reconstructPath(bestNode, false);
        }

        return null;
    }

    private static boolean isInCorridor(PathNode node, Set<Long> corridor) {
        var sectionKey = packSectionKey(node.getX() >> 4, node.getY() >> 4, node.getZ() >> 4);

        return corridor.contains(sectionKey);
    }

    private float heuristic(PathNode from, PathNode to) {
        return from.distanceTo(to) * config.heuristicWeight();
    }

    private BLibPath reconstructPath(PathNode endNode, boolean reached) {
        var nodes = new ArrayList<PathNode>();
        var current = endNode;

        while (current != null && nodes.size() < config.maxPathLength()) {
            nodes.add(current);
            current = current.getParent();
        }

        Collections.reverse(nodes);

        return new BLibPath(nodes, reached);
    }

    private static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38
            | ((long) sectionY & 0xFFFL) << 26
            | ((long) sectionZ & 0x3FFFFFFL);
    }
}
