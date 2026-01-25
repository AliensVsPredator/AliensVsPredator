package com.blib.api.common.spatial.v1.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class ChunkPosUtil {

    public static Set<ChunkPos> getChunksAround(BlockPos centerBlockPos, int radiusInChunks) {
        var chunkPositions = new HashSet<ChunkPos>();

        // Convert block position to center chunk position
        var centerChunk = new ChunkPos(centerBlockPos);

        var centerX = centerChunk.x;
        var centerZ = centerChunk.z;

        for (var dx = -radiusInChunks; dx <= radiusInChunks; dx++) {
            for (var dz = -radiusInChunks; dz <= radiusInChunks; dz++) {
                chunkPositions.add(new ChunkPos(centerX + dx, centerZ + dz));
            }
        }

        return chunkPositions;
    }
}
