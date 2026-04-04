package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A* pathfinding with optional two-level hierarchical search. When a {@link TerrainClassificationCache} is provided,
 * the pathfinder first runs a fast section-level A* to identify a corridor of 16x16x16 sections, then runs the
 * block-level A* restricted to that corridor. This prevents budget waste on irrelevant areas.
 */
public final class BLibPathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibPathFinder.class);

    private static final int MAX_NEIGHBORS = 40;

    private static final int MAX_SECTION_SEARCH_NODES = 128;

    private final TerrainEvaluator evaluator;

    private final SearchConfig config;

    private final @Nullable TerrainClassificationCache classificationCache;

    private @Nullable PathSearchSnapshot lastSearchSnapshot;

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

            if (
                bestEntry == null || sectionDistance(current.x(), current.y(), current.z(), goalSX, goalSY, goalSZ) < sectionDistance(
                    bestEntry.x(),
                    bestEntry.y(),
                    bestEntry.z(),
                    goalSX,
                    goalSY,
                    goalSZ
                )
            ) {
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
                        var supportsClimbable = evaluator.getTerrainCost(TerrainType.CLIMBABLE) < Float.MAX_VALUE;

                        if (!sectionPassable && !supportsBreakable && !supportsClimbable) {
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

                        if (supportsClimbable) {
                            var climbableCost = evaluator.getTerrainCost(TerrainType.CLIMBABLE);

                            if (climbableCost < cheapestCost) {
                                cheapestCost = climbableCost;
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

        // Build corridor from path sections with a 1-section buffer.
        // The buffer ensures the block-level A* can reach climbable structures
        // slightly off the direct section path.
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

        var closedNodes = new ArrayList<PathNode>();
        var neighbors = new PathNode[MAX_NEIGHBORS];
        var visitedCount = 0;
        PathNode bestNode = startNode;

        while (!openSet.isEmpty() && visitedCount < config.maxSearchNodes()) {
            var current = openSet.poll();

            if (current.isClosed()) {
                continue;
            }

            current.setClosed(true);
            closedNodes.add(current);
            visitedCount++;

            if (current.equals(goalNode)) {
                var path = reconstructPath(current, true);

                assignSurfaceDirections(path);
                lastSearchSnapshot = buildSnapshot(closedNodes, path, corridor, visitedCount);

                return path;
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

        LOGGER.info(
            "[A*] Visited {}/{} nodes | budget exhausted={} | corridor={}",
            visitedCount,
            config.maxSearchNodes(),
            visitedCount >= config.maxSearchNodes(),
            corridor != null ? corridor.size() + " sections" : "none"
        );

        BLibPath path = null;

        if (bestNode != startNode) {
            path = reconstructPath(bestNode, false);
            assignSurfaceDirections(path);
        }

        lastSearchSnapshot = buildSnapshot(closedNodes, path, corridor, visitedCount);

        return path;
    }

    private PathSearchSnapshot buildSnapshot(
        List<PathNode> closedNodes,
        @Nullable BLibPath path,
        @Nullable Set<Long> corridor,
        int visitedCount
    ) {
        var pathNodeSet = new HashSet<PathNode>();

        if (path != null) {
            for (int i = 0; i < path.getNodeCount(); i++) {
                pathNodeSet.add(path.getNode(i));
            }
        }

        var entries = new ArrayList<DebugNodeEntry>(closedNodes.size());

        for (var node : closedNodes) {
            entries.add(
                new DebugNodeEntry(
                    node.getX(),
                    node.getY(),
                    node.getZ(),
                    node.getTerrainType().ordinal(),
                    node.getPostureIndex(),
                    node.getSurfaceDirection(),
                    node.getAvailableSurfaces(),
                    pathNodeSet.contains(node)
                )
            );
        }

        var corridorKeys = corridor != null ? List.copyOf(corridor) : List.<Long>of();

        return new PathSearchSnapshot(entries, corridorKeys, visitedCount, config.maxSearchNodes());
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

    // --- TPO: Surface direction assignment ---

    private void assignSurfaceDirections(BLibPath path) {
        var previousSurface = -1;

        for (int i = 0; i < path.getNodeCount(); i++) {
            var node = path.getNode(i);

            if (node.getTerrainType() != TerrainType.CLIMBABLE) {
                node.setSurfaceDirection(0);
                previousSurface = -1;
                continue;
            }

            var available = node.getAvailableSurfaces();

            if (previousSurface >= 0 && (available & (1 << previousSurface)) != 0) {
                node.setSurfaceDirection(previousSurface);
            } else {
                node.setSurfaceDirection(pickBestSurface(path, i, available));
            }

            previousSurface = node.getSurfaceDirection();
        }
    }

    private static int pickBestSurface(BLibPath path, int index, int availableMask) {
        if (index > 0) {
            var prev = path.getNode(index - 1);
            var current = path.getNode(index);
            var dx = current.getX() - prev.getX();
            var dy = current.getY() - prev.getY();
            var dz = current.getZ() - prev.getZ();

            Direction.Axis movementAxis = null;

            if (dx != 0 && dy == 0 && dz == 0) {
                movementAxis = Direction.Axis.X;
            } else if (dy != 0 && dx == 0 && dz == 0) {
                movementAxis = Direction.Axis.Y;
            } else if (dz != 0 && dx == 0 && dy == 0) {
                movementAxis = Direction.Axis.Z;
            }

            if (movementAxis != null) {
                for (var direction : Direction.values()) {
                    if (direction.getAxis() != movementAxis && (availableMask & (1 << direction.ordinal())) != 0) {
                        return direction.ordinal();
                    }
                }
            }
        }

        return Integer.numberOfTrailingZeros(availableMask);
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
        return ((long) sectionX & 0x3FFFFFFL) << 38 | ((long) sectionY & 0xFFFL) << 26 | ((long) sectionZ & 0x3FFFFFFL);
    }
}
