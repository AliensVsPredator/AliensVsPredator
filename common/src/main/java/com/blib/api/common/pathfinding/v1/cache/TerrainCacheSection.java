package com.blib.api.common.pathfinding.v1.cache;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Caches terrain classifications for a 16x16x16 block section.
 * Lazily populated on first access. Each position stores a TerrainType
 * or null (impassable).
 */
final class TerrainCacheSection {

    private static final int SIZE = 16;

    private static final int VOLUME = SIZE * SIZE * SIZE;

    private static final byte UNCLASSIFIED = -1;

    private static final byte IMPASSABLE = -2;

    private final byte[] classifications;

    private final Set<TerrainType> containedTerrainTypes;

    private boolean populated;

    private boolean hasPassableBlocks;

    TerrainCacheSection() {
        this.classifications = new byte[VOLUME];
        this.containedTerrainTypes = EnumSet.noneOf(TerrainType.class);
        clear();
    }

    void populate(LevelReader level, int sectionX, int sectionY, int sectionZ, TerrainClassifier classifier) {
        var baseX = sectionX << 4;
        var baseY = sectionY << 4;
        var baseZ = sectionZ << 4;
        var pos = new BlockPos.MutableBlockPos();

        containedTerrainTypes.clear();
        hasPassableBlocks = false;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    pos.set(baseX + x, baseY + y, baseZ + z);
                    var terrainType = classifier.classify(level, pos);
                    var index = packLocal(x, y, z);

                    if (terrainType != null) {
                        classifications[index] = (byte) terrainType.ordinal();
                        containedTerrainTypes.add(terrainType);
                        hasPassableBlocks = true;
                    } else {
                        classifications[index] = IMPASSABLE;
                    }
                }
            }
        }

        this.populated = true;
    }

    @Nullable TerrainType get(int localX, int localY, int localZ) {
        var value = classifications[packLocal(localX, localY, localZ)];

        if (value == UNCLASSIFIED || value == IMPASSABLE) {
            return null;
        }

        return TerrainType.values()[value];
    }

    boolean isPopulated() {
        return populated;
    }

    boolean hasPassableBlocks() {
        return hasPassableBlocks;
    }

    Set<TerrainType> getContainedTerrainTypes() {
        return containedTerrainTypes;
    }

    void clear() {
        for (int i = 0; i < VOLUME; i++) {
            classifications[i] = UNCLASSIFIED;
        }

        containedTerrainTypes.clear();
        this.hasPassableBlocks = false;
        this.populated = false;
    }

    private static int packLocal(int x, int y, int z) {
        return (y << 8) | (z << 4) | x;
    }
}
