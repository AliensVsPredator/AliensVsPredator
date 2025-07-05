package com.alien.common.gameplay.hive.vent;

import com.lib.common.gameplay.util.Cache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HiveVentCache extends Cache<BlockPos, Void> {

    private final Map<ChunkPos, Set<BlockPos>> ventPositionsByChunkPos;

    public HiveVentCache() {
        this.ventPositionsByChunkPos = new HashMap<>();
    }

    @Override
    protected void onAddToCache(BlockPos id, @Nullable Void oldValue, Void newValue) {
        var chunkPos = new ChunkPos(id);
        ventPositionsByChunkPos.computeIfAbsent(chunkPos, $ -> new HashSet<>()).add(id);

        super.onAddToCache(id, oldValue, newValue);
    }

    @Override
    protected void onRemoveFromCache(BlockPos id, Void value) {
        var chunkPos = new ChunkPos(id);
        ventPositionsByChunkPos.compute(chunkPos, ($1, ventPositions) -> {
            if (ventPositions == null) {
                return null;
            }

            ventPositions.remove(id);

            if (ventPositions.isEmpty()) {
                return null;
            }

            return ventPositions;
        });

        super.onRemoveFromCache(id, value);
    }

    public Set<BlockPos> getVentsForChunk(BlockPos blockPos) {
        return getVentsForChunk(new ChunkPos(blockPos));
    }

    public Set<BlockPos> getVentsForChunk(ChunkPos chunkPos) {
        return ventPositionsByChunkPos.getOrDefault(chunkPos, Set.of());
    }
}
