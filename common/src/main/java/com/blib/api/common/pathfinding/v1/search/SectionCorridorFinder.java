package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Section-level A* search for computing a corridor of 16x16x16 sections between two positions. The corridor acts as a
 * fast reachability check and constrains the block-level search to a narrow band, reducing the search space.
 */
public final class SectionCorridorFinder {

    private static final int MAX_SECTION_SEARCH_NODES = 128;

    private final TerrainClassificationCache classificationCache;

    private final TerrainEvaluator evaluator;

    public SectionCorridorFinder(TerrainClassificationCache classificationCache, TerrainEvaluator evaluator) {
        this.classificationCache = classificationCache;
        this.evaluator = evaluator;
    }

    /**
     * Runs a section-level A* from the start position to the target. Returns a set of section keys forming a corridor
     * (with a 1-section buffer), or null if the target section is unreachable.
     */
    public @Nullable Set<Long> findCorridor(LevelReader level, BlockPos startPos, BlockPos targetPos) {
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

            for (int bufDx = -1; bufDx <= 1; bufDx++) {
                for (int bufDy = -1; bufDy <= 1; bufDy++) {
                    for (int bufDz = -1; bufDz <= 1; bufDz++) {
                        corridor.add(packSectionKey(sx + bufDx, sy + bufDy, sz + bufDz));
                    }
                }
            }
        }

        return corridor;
    }

    /**
     * Returns true if the given node falls within the corridor.
     */
    public static boolean isInCorridor(PathNode node, Set<Long> corridor) {
        var sectionKey = packSectionKey(node.getX() >> 4, node.getY() >> 4, node.getZ() >> 4);

        return corridor.contains(sectionKey);
    }

    /**
     * Packs section coordinates into a single long key.
     */
    public static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38 | ((long) sectionY & 0xFFFL) << 26 | ((long) sectionZ & 0x3FFFFFFL);
    }

    private static float sectionDistance(int ax, int ay, int az, int bx, int by, int bz) {
        int dx = bx - ax, dy = by - ay, dz = bz - az;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
