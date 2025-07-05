package com.alien.common.gameplay.hive.vent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

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

    public Set<BlockPos> getVentsWithinSection(BlockPos blockPos) {
        return hiveVentCache.getVentsForSection(blockPos);
    }

    public Set<BlockPos> getVentsWithinSection(SectionPos sectionPos) {
        return hiveVentCache.getVentsForSection(sectionPos);
    }
}
