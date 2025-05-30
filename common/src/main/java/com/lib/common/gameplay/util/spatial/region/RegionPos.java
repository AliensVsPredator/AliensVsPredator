package com.lib.common.gameplay.util.spatial.region;

import net.minecraft.world.level.ChunkPos;

/**
 * Represents a region of chunks (e.g., 128x128 chunks). Used as a key for spatial partitioning of large-scale data like
 * spawn tracking.
 */
public record RegionPos(
    int x,
    int z
) {

    public static final int REGION_SIZE = 128;

    public static RegionPos fromChunkPos(ChunkPos pos) {
        return new RegionPos(pos.x >> 7, pos.z >> 7);
    }

    public static RegionPos fromChunkCoords(int chunkX, int chunkZ) {
        return new RegionPos(chunkX >> 7, chunkZ >> 7);
    }

    public ChunkPos getChunkOrigin() {
        return new ChunkPos(x << 7, z << 7);
    }

    public long toLong() {
        return (((long) x) << 32) | (z & 0xFFFFFFFFL);
    }

    public static RegionPos fromLong(long packed) {
        int x = (int) (packed >> 32);
        int z = (int) packed;
        return new RegionPos(x, z);
    }
}
