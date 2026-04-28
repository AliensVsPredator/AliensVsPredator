package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Region-aware section-level A* search for computing a corridor of 16x16x16 sections between two positions. Each A*
 * node is a (section, region) pair, ensuring the corridor only includes section transitions where passable terrain is
 * internally connected and meets at shared face boundaries.
 */
public final class SectionCorridorFinder {

    private static final int MAX_SECTION_SEARCH_NODES = 128;

    // Face indices (must match TerrainCacheSection constants).
    private static final int FACE_WEST = 0;

    private static final int FACE_EAST = 1;

    private static final int FACE_NORTH = 2;

    private static final int FACE_SOUTH = 3;

    private static final int FACE_BOTTOM = 4;

    private static final int FACE_TOP = 5;

    private final TerrainClassificationCache classificationCache;

    private final TerrainEvaluator evaluator;

    public SectionCorridorFinder(TerrainClassificationCache classificationCache, TerrainEvaluator evaluator) {
        this.classificationCache = classificationCache;
        this.evaluator = evaluator;
    }

    private record RegionNode(
        long key,
        int x,
        int y,
        int z,
        int regionRoot,
        float gCost,
        float fCost,
        @Nullable RegionNode parent
    ) {}

    /**
     * Runs a region-aware section-level A* from the start position to the target. Returns a {@link CorridorResult}
     * containing the buffered corridor set and ordered section waypoints, or null if the target is unreachable.
     */
    public @Nullable CorridorResult findCorridor(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        var startSX = startPos.getX() >> 4;
        var startSY = startPos.getY() >> 4;
        var startSZ = startPos.getZ() >> 4;
        var goalSX = targetPos.getX() >> 4;
        var goalSY = targetPos.getY() >> 4;
        var goalSZ = targetPos.getZ() >> 4;

        var startRegion = classificationCache.getRegionRoot(level, startPos);

        if (startRegion == -1) {
            startRegion = findAnyRegionInSection(level, startSX, startSY, startSZ);

            if (startRegion == -1) {
                return null;
            }
        }

        var goalSectionKey = packSectionKey(goalSX, goalSY, goalSZ);
        var startNodeKey = packRegionNodeKey(startSX, startSY, startSZ, startRegion);

        var openSet = new PriorityQueue<RegionNode>(Comparator.comparingDouble(RegionNode::fCost));
        var closedSet = new HashSet<Long>();

        var startH = sectionDistance(startSX, startSY, startSZ, goalSX, goalSY, goalSZ);
        openSet.add(new RegionNode(startNodeKey, startSX, startSY, startSZ, startRegion, 0, startH, null));

        RegionNode bestEntry = null;
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

            // Goal check: reached the goal section (any region — block A* handles final routing).
            if (packSectionKey(current.x(), current.y(), current.z()) == goalSectionKey) {
                bestEntry = current;
                break;
            }

            // Expand cardinal neighbors.
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != 1) {
                            continue;
                        }

                        var nx = current.x() + dx;
                        var ny = current.y() + dy;
                        var nz = current.z() + dz;

                        // Check if current region can exit through the shared face.
                        if (!classificationCache.isRegionConnected(
                            level, current.x(), current.y(), current.z(), current.regionRoot(), dx, dy, dz
                        )) {
                            // If breakable terrain is supported, the entity can break through even without a
                            // natural connection.
                            if (evaluator.getTerrainCost(TerrainType.BREAKABLE) >= Float.MAX_VALUE) {
                                continue;
                            }
                        }

                        // Find which regions in the neighbor section are reachable from the shared face.
                        var toFace = getEntryFace(dx, dy, dz);
                        var neighborRegionCount = classificationCache.getFaceRegionCount(level, nx, ny, nz, toFace);

                        if (neighborRegionCount == 0 && evaluator.getTerrainCost(TerrainType.BREAKABLE) >= Float.MAX_VALUE) {
                            continue;
                        }

                        // Determine cheapest terrain cost for this transition.
                        var cheapestCost = Float.MAX_VALUE;
                        var terrainTypes = classificationCache.getSectionTerrainTypes(level, nx, ny, nz);

                        for (var terrainType : terrainTypes) {
                            var cost = evaluator.getTerrainCost(terrainType);

                            if (cost < cheapestCost) {
                                cheapestCost = cost;
                            }
                        }

                        if (evaluator.getTerrainCost(TerrainType.BREAKABLE) < Float.MAX_VALUE) {
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

                        // Add an entry for each reachable region in the neighbor section.
                        if (neighborRegionCount > 0) {
                            for (int i = 0; i < neighborRegionCount; i++) {
                                var neighborRoot = classificationCache.getFaceRegionRoot(
                                    level, nx, ny, nz, toFace, i
                                );
                                var nKey = packRegionNodeKey(nx, ny, nz, neighborRoot);

                                if (!closedSet.contains(nKey)) {
                                    openSet.add(new RegionNode(nKey, nx, ny, nz, neighborRoot, g, g + h, current));
                                }
                            }
                        } else {
                            // No natural regions on the entry face — breakable-only transition.
                            // Use a sentinel region root (the face index) to distinguish from real regions.
                            var nKey = packRegionNodeKey(nx, ny, nz, 4096 + toFace);

                            if (!closedSet.contains(nKey)) {
                                openSet.add(new RegionNode(nKey, nx, ny, nz, 4096 + toFace, g, g + h, current));
                            }
                        }
                    }
                }
            }
        }

        if (bestEntry == null || packSectionKey(bestEntry.x(), bestEntry.y(), bestEntry.z()) != goalSectionKey) {
            return null;
        }

        // Build ordered section path and buffered corridor.
        var sectionCoords = new ArrayList<int[]>();
        var entry = bestEntry;

        while (entry != null) {
            sectionCoords.add(new int[] { entry.x(), entry.y(), entry.z() });
            entry = entry.parent();
        }

        Collections.reverse(sectionCoords);

        var waypoints = new ArrayList<BlockPos>(sectionCoords.size());
        var corridor = new HashSet<Long>();

        for (var coords : sectionCoords) {
            waypoints.add(new BlockPos(coords[0] * 16 + 8, coords[1] * 16 + 8, coords[2] * 16 + 8));

            for (int bufDx = -1; bufDx <= 1; bufDx++) {
                for (int bufDy = -1; bufDy <= 1; bufDy++) {
                    for (int bufDz = -1; bufDz <= 1; bufDz++) {
                        corridor.add(packSectionKey(coords[0] + bufDx, coords[1] + bufDy, coords[2] + bufDz));
                    }
                }
            }
        }

        return new CorridorResult(corridor, List.copyOf(waypoints));
    }

    /**
     * Returns the face index of the entry face when moving in the given direction.
     */
    private static int getEntryFace(int dx, int dy, int dz) {
        if (dx == 1) return FACE_WEST;
        if (dx == -1) return FACE_EAST;
        if (dy == 1) return FACE_BOTTOM;
        if (dy == -1) return FACE_TOP;
        if (dz == 1) return FACE_NORTH;
        return FACE_SOUTH;
    }

    /**
     * Returns true if the given node falls within the corridor.
     */
    public static boolean isInCorridor(PathNode node, Set<Long> corridor) {
        var sectionKey = packSectionKey(node.getX() >> 4, node.getY() >> 4, node.getZ() >> 4);

        return corridor.contains(sectionKey);
    }

    /**
     * Packs section coordinates into a single long key (for corridor sets and block-level corridor checks).
     */
    public static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38 | ((long) sectionY & 0xFFFL) << 26 | ((long) sectionZ & 0x3FFFFFFL);
    }

    private static long packRegionNodeKey(int sectionX, int sectionY, int sectionZ, int regionRoot) {
        return ((long) sectionX & 0x3FFFFFL) << 42
            | ((long) sectionZ & 0x3FFFFFL) << 20
            | ((long) (sectionY & 0xFF)) << 12
            | ((long) regionRoot & 0xFFFL);
    }

    private static float sectionDistance(int ax, int ay, int az, int bx, int by, int bz) {
        int dx = bx - ax, dy = by - ay, dz = bz - az;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private int findAnyRegionInSection(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        for (int face = 0; face < 6; face++) {
            var count = classificationCache.getFaceRegionCount(level, sectionX, sectionY, sectionZ, face);

            if (count > 0) {
                return classificationCache.getFaceRegionRoot(level, sectionX, sectionY, sectionZ, face, 0);
            }
        }

        return -1;
    }
}
