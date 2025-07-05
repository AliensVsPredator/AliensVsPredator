package com.alien.common.gameplay.hive.vent;

import com.lib.common.gameplay.util.Cache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HiveVentCache extends Cache<BlockPos, Void> {

    private final Map<SectionPos, Set<BlockPos>> ventPositionsBySectionPos;

    public HiveVentCache() {
        this.ventPositionsBySectionPos = new HashMap<>();
    }

    @Override
    protected void onAddToCache(BlockPos id, @Nullable Void oldValue, Void newValue) {
        var sectionPos = SectionPos.of(id);
        ventPositionsBySectionPos.computeIfAbsent(sectionPos, $ -> new HashSet<>()).add(id);

        super.onAddToCache(id, oldValue, newValue);
    }

    @Override
    protected void onRemoveFromCache(BlockPos id, Void value) {
        var sectionPos = SectionPos.of(id);

        ventPositionsBySectionPos.compute(sectionPos, ($1, ventPositions) -> {
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

    public Set<BlockPos> getVentsForSection(BlockPos blockPos) {
        return getVentsForSection(SectionPos.of(blockPos));
    }

    public Set<BlockPos> getVentsForSection(SectionPos sectionPos) {
        return ventPositionsBySectionPos.getOrDefault(sectionPos, Set.of());
    }
}
