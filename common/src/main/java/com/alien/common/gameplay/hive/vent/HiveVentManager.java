package com.alien.common.gameplay.hive.vent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public class HiveVentManager {

    private final HiveVentCache hiveVentCache;

    public HiveVentManager() {
        this.hiveVentCache = new HiveVentCache();
    }

    public void addVent(BlockPos blockPos) {
        hiveVentCache.add(blockPos, null);
    }

    public void removeVent(BlockPos blockPos) {
        hiveVentCache.remove(blockPos);
    }

    public Set<BlockPos> getVentsWithinChunk(BlockPos blockPos) {
        return hiveVentCache.getVentsForChunk(blockPos);
    }

    public Set<BlockPos> getVentsWithinChunk(ChunkPos chunkPos) {
        return hiveVentCache.getVentsForChunk(chunkPos);
    }
}
