package com.blib.api.common.pathfinding.v1.cache;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Section-based cache for terrain classifications. Stores pre-computed {@link TerrainType}
 * results in 16x16x16 sections, avoiding redundant classifier calls across pathfind operations.
 *
 * <p>Sections are lazily populated on first access. Block changes invalidate the affected
 * section and its immediate neighbors (since a change can affect adjacent positions'
 * classifications — e.g., breaking a floor block changes the GROUND status of the block above).</p>
 *
 * <p>Caches are shared between entities with the same {@link TerrainClassifier} configuration.
 * Thread-safe via concurrent map.</p>
 */
public final class TerrainClassificationCache {

    private final Map<Long, TerrainCacheSection> sections;

    private final TerrainClassifier classifier;

    public TerrainClassificationCache(TerrainClassifier classifier) {
        this.sections = new ConcurrentHashMap<>();
        this.classifier = classifier;
    }

    /**
     * Returns the cached terrain classification for the given position,
     * populating the section lazily if needed.
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
        var section = sections.computeIfAbsent(key, $ -> new TerrainCacheSection());

        if (!section.isPopulated()) {
            section.populate(level, sectionX, sectionY, sectionZ, classifier);
        }

        return section;
    }

    /**
     * Returns whether the section at the given section coordinates has any passable blocks.
     * Populates the section lazily if needed.
     */
    public boolean isSectionPassable(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.hasPassableBlocks();
    }

    /**
     * Returns the set of terrain types present in the section at the given section coordinates.
     * Populates the section lazily if needed.
     */
    public java.util.Set<TerrainType> getSectionTerrainTypes(LevelReader level, int sectionX, int sectionY, int sectionZ) {
        var section = getOrPopulateSection(level, sectionX, sectionY, sectionZ);

        return section.getContainedTerrainTypes();
    }

    /**
     * Invalidates the section containing the given block position and its
     * immediate neighbor sections (a block change can affect adjacent positions'
     * classifications).
     */
    public void invalidateBlock(BlockPos pos) {
        var sectionX = pos.getX() >> 4;
        var sectionY = pos.getY() >> 4;
        var sectionZ = pos.getZ() >> 4;

        var localX = pos.getX() & 15;
        var localY = pos.getY() & 15;
        var localZ = pos.getZ() & 15;

        invalidateSection(sectionX, sectionY, sectionZ);

        if (localX == 0) invalidateSection(sectionX - 1, sectionY, sectionZ);
        if (localX == 15) invalidateSection(sectionX + 1, sectionY, sectionZ);
        if (localY == 0) invalidateSection(sectionX, sectionY - 1, sectionZ);
        if (localY == 15) invalidateSection(sectionX, sectionY + 1, sectionZ);
        if (localZ == 0) invalidateSection(sectionX, sectionY, sectionZ - 1);
        if (localZ == 15) invalidateSection(sectionX, sectionY, sectionZ + 1);
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

    public int getSectionCount() {
        return sections.size();
    }

    private static long packSectionKey(int sectionX, int sectionY, int sectionZ) {
        return ((long) sectionX & 0x3FFFFFFL) << 38
            | ((long) sectionY & 0xFFFL) << 26
            | ((long) sectionZ & 0x3FFFFFFL);
    }
}
