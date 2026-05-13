package com.blib.api.common.pathfinding.v1.cache;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Section-based cache for terrain classifications. Stores pre-computed {@link TerrainType} results in 16x16x16
 * sections, avoiding redundant classifier calls across pathfind operations.
 * <p>
 * Sections are lazily populated on first access. Block changes invalidate the affected section and its immediate
 * neighbors (since a change can affect adjacent positions' classifications — e.g., breaking a floor block changes the
 * GROUND status of the block above).
 * </p>
 * <p>
 * Caches are shared between entities with the same {@link TerrainClassifier} configuration. Thread-safe via concurrent
 * map.
 * </p>
 */
public final class TerrainClassificationCache {

    private final Long2ObjectMap<TerrainCacheSection> sections;

    private final TerrainClassifier classifier;

    public TerrainClassificationCache(TerrainClassifier classifier) {
        this.sections = new Long2ObjectOpenHashMap<>();
        this.classifier = classifier;
    }

    /**
     * Returns the cached terrain classification for the given position, populating the section lazily if needed.
     */
    public @Nullable TerrainType getClassification(LevelReader level, BlockPos pos) {
        var sectionX = pos.getX() >> 4;
        var sectionY = pos.getY() >> 4;
        var sectionZ = pos.getZ() >> 4;
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        var localX = pos.getX() & 15;
        var localY = pos.getY() & 15;
        var localZ = pos.getZ() & 15;

        return section.get(localX, localY, localZ);
    }

    private TerrainCacheSection getOrPopulateSection(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        var key = packSectionKey(sectionX, sectionY, sectionZ);
        var section = sections.computeIfAbsent(key, (long $) -> new TerrainCacheSection());

        if (!section.isPopulated()) {
            section.populate(level, sectionX, sectionY, sectionZ, classifier);
        }

        return section;
    }

    /**
     * Returns whether the section at the given section coordinates has any passable blocks. Populates the section
     * lazily if needed.
     */
    public boolean isSectionPassable(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.hasPassableBlocks();
    }

    /**
     * Returns the set of terrain types present in the section at the given section coordinates. Populates the section
     * lazily if needed.
     */
    public Set<TerrainType> getSectionTerrainTypes(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.getContainedTerrainTypes();
    }

    /**
     * Invalidates the section containing the given block position and its immediate neighbor sections (a block change
     * can affect adjacent positions' classifications).
     */
    public void invalidateBlock(BlockPos pos) {
        var sectionX = pos.getX() >> 4;
        var sectionY = pos.getY() >> 4;
        var sectionZ = pos.getZ() >> 4;

        var localX = pos.getX() & 15;
        var localY = pos.getY() & 15;
        var localZ = pos.getZ() & 15;

        invalidateSection(sectionX, sectionY, sectionZ);

        if (localX == 0)
            invalidateSection(sectionX - 1, sectionY, sectionZ);
        if (localX == 15)
            invalidateSection(sectionX + 1, sectionY, sectionZ);
        if (localY == 0)
            invalidateSection(sectionX, sectionY - 1, sectionZ);
        if (localY == 15)
            invalidateSection(sectionX, sectionY + 1, sectionZ);
        if (localZ == 0)
            invalidateSection(sectionX, sectionY, sectionZ - 1);
        if (localZ == 15)
            invalidateSection(sectionX, sectionY, sectionZ + 1);
    }

    /**
     * Invalidates a specific section, forcing re-classification on next access.
     */
    public void invalidateSection(int sectionX, int sectionY, int sectionZ) {
        var key = packSectionKey(sectionX, sectionY, sectionZ);
        var section = sections.get(key);

        if (section != null) {
            section.clear();
        }
    }

    /**
     * Clears all cached data.
     */
    public void invalidateAll() {
        sections.clear();
    }

    /**
     * Pre-populates all sections within the block-coordinate bounding box. Call from the main thread before dispatching
     * an async search to ensure the worker thread only reads pre-populated data.
     */
    public void prePopulateArea(LevelReader level, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        var minSX = minX >> 4;
        var minSY = minY >> 4;
        var minSZ = minZ >> 4;
        var maxSX = maxX >> 4;
        var maxSY = maxY >> 4;
        var maxSZ = maxZ >> 4;

        for (int sx = minSX; sx <= maxSX; sx++) {
            for (int sy = minSY; sy <= maxSY; sy++) {
                for (int sz = minSZ; sz <= maxSZ; sz++) {
                    getOrPopulateSection(level, sx, sy, sz);
                }
            }
        }
    }

    /**
     * Returns the cached terrain classification for the given position without populating. Returns null if the section
     * is not populated or the position is impassable. Safe to call from any thread after pre-population.
     */
    public @Nullable TerrainType getClassificationIfCached(BlockPos pos) {
        var sectionX = pos.getX() >> 4;
        var sectionY = pos.getY() >> 4;
        var sectionZ = pos.getZ() >> 4;
        var key = packSectionKey(sectionX, sectionY, sectionZ);
        var section = sections.get(key);

        if (section == null || !section.isPopulated()) {
            return null;
        }

        return section.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    /**
     * Returns the region root for the given block position, or -1 if not passable. Populates the section lazily if
     * needed.
     */
    public int getRegionRoot(LevelReader level, BlockPos pos) {
        var section = getOrPopulateSection(level, pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);

        return section.getRegionRoot(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    /**
     * Returns the number of distinct regions present on the given face of the specified section.
     */
    public int getFaceRegionCount(LevelReader level, int sectionX, int sectionY, int sectionZ, int face) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.getFaceRegionCount(face);
    }

    /**
     * Returns the region root of the i-th region on the given face.
     */
    public int getFaceRegionRoot(LevelReader level, int sectionX, int sectionY, int sectionZ, int face, int index) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.getFaceRegionRoot(face, index);
    }

    /**
     * Returns the Y-level bitmask of the i-th region on the given face.
     */
    public short getFaceRegionYLevels(LevelReader level, int sectionX, int sectionY, int sectionZ, int face, int index) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.getFaceRegionYLevels(face, index);
    }

    /**
     * Checks whether a specific region in one section connects to any region in an adjacent section through their
     * shared face. For vertical faces (±X, ±Z), connectivity requires overlapping passable Y-levels (with ±1 step
     * tolerance). For horizontal faces (±Y), presence on both faces is sufficient.
     *
     * @param fromRegionRoot the region root to check connectivity for (-1 to check any region)
     */
    public boolean isRegionConnected(
        LevelReader level,
        int fromSX,
        int fromSY,
        int fromSZ,
        int fromRegionRoot,
        int dx,
        int dy,
        int dz
    ) {
        var fromSection = getOrPopulateSection(level, fromSX, fromSY, fromSZ);

        int fromFace;
        int toFace;

        if (dx == 1) {
            fromFace = TerrainCacheSection.FACE_EAST;
            toFace = TerrainCacheSection.FACE_WEST;
        } else if (dx == -1) {
            fromFace = TerrainCacheSection.FACE_WEST;
            toFace = TerrainCacheSection.FACE_EAST;
        } else if (dy == 1) {
            fromFace = TerrainCacheSection.FACE_TOP;
            toFace = TerrainCacheSection.FACE_BOTTOM;
        } else if (dy == -1) {
            fromFace = TerrainCacheSection.FACE_BOTTOM;
            toFace = TerrainCacheSection.FACE_TOP;
        } else if (dz == 1) {
            fromFace = TerrainCacheSection.FACE_SOUTH;
            toFace = TerrainCacheSection.FACE_NORTH;
        } else {
            fromFace = TerrainCacheSection.FACE_NORTH;
            toFace = TerrainCacheSection.FACE_SOUTH;
        }

        // Find the Y-level bitmask for the specified region on the exit face.
        short fromMask = 0;
        var fromCount = fromSection.getFaceRegionCount(fromFace);

        for (int i = 0; i < fromCount; i++) {
            if (fromRegionRoot == -1 || fromSection.getFaceRegionRoot(fromFace, i) == fromRegionRoot) {
                fromMask |= fromSection.getFaceRegionYLevels(fromFace, i);
            }
        }

        if (fromMask == 0) {
            return false;
        }

        var toSX = fromSX + dx;
        var toSY = fromSY + dy;
        var toSZ = fromSZ + dz;
        var toSection = getOrPopulateSection(level, toSX, toSY, toSZ);
        var toCount = toSection.getFaceRegionCount(toFace);

        if (toCount == 0) {
            return false;
        }

        // For horizontal faces (±Y), any presence on both faces is sufficient.
        if (fromFace >= TerrainCacheSection.FACE_BOTTOM) {
            return true;
        }

        // For vertical faces, check Y-level overlap with ±1 step tolerance.
        var fromExpanded = (int) (fromMask & 0xFFFF);
        fromExpanded = fromExpanded | (fromExpanded << 1) | (fromExpanded >>> 1);

        for (int i = 0; i < toCount; i++) {
            var toYLevels = (int) (toSection.getFaceRegionYLevels(toFace, i) & 0xFFFF);
            var toExpanded = toYLevels | (toYLevels << 1) | (toYLevels >>> 1);

            if ((fromExpanded & toExpanded) != 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether two adjacent sections are connected at their shared face (any region to any region).
     */
    public boolean areSectionsConnected(
        LevelReader level,
        int fromSX,
        int fromSY,
        int fromSZ,
        int dx,
        int dy,
        int dz
    ) {
        return isRegionConnected(level, fromSX, fromSY, fromSZ, -1, dx, dy, dz);
    }

    public int getSectionCount() {
        return sections.size();
    }

    private static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38 | ((long) sectionY & 0xFFFL) << 26 | ((long) sectionZ & 0x3FFFFFFL);
    }
}
