package com.avp.common.block.entity.resin_node;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResinSpreader {

    final boolean isWorldGeneration;

    private final TagKey<Block> replaceableBlocks;

    private final int growthSpawnCost;

    private final int noGrowthRadius;

    private final int chargeDecayRate;

    private final int additionalDecayRate;

    private List<ChargeCursor> cursors = new ArrayList<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    public ResinSpreader(
        boolean bl,
        TagKey<Block> tagKey,
        int growthSpawnCost,
        int noGrowthRadius,
        int chargeDecayRate,
        int additionalDecayRate
    ) {
        this.isWorldGeneration = bl;
        this.replaceableBlocks = tagKey;
        this.growthSpawnCost = growthSpawnCost;
        this.noGrowthRadius = noGrowthRadius;
        this.chargeDecayRate = chargeDecayRate;
        this.additionalDecayRate = additionalDecayRate;
    }

    public static ResinSpreader createLevelSpreader() {
        return new ResinSpreader(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static ResinSpreader createWorldGenSpreader() {
        return new ResinSpreader(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int growthSpawnCost() {
        return this.growthSpawnCost;
    }

    public int noGrowthRadius() {
        return this.noGrowthRadius;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public int additionalDecayRate() {
        return this.additionalDecayRate;
    }

    public boolean isWorldGeneration() {
        return this.isWorldGeneration;
    }

    @VisibleForTesting
    public List<ChargeCursor> getCursors() {
        return this.cursors;
    }

    public void clear() {
        this.cursors.clear();
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("cursors", 9)) {
            cursors.clear();
            var list = ChargeCursor.CODEC
                .listOf()
                .parse(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getList("cursors", 10)))
                .resultOrPartial(LOGGER::error)
                .orElseGet(ArrayList::new);

            var i = Math.min(list.size(), 32);

            for (var j = 0; j < i; j++) {
                addCursor(list.get(j));
            }
        }
    }

    public void save(CompoundTag compoundTag) {
        ChargeCursor.CODEC
            .listOf()
            .encodeStart(NbtOps.INSTANCE, this.cursors)
            .resultOrPartial(LOGGER::error)
            .ifPresent(tag -> compoundTag.put("cursors", tag));
    }

    public void addCursors(BlockPos blockPos, int i) {
        while (i > 0) {
            var j = Math.min(i, 1000);
            addCursor(new ChargeCursor(blockPos, j));
            i -= j;
        }
    }

    private void addCursor(ChargeCursor chargeCursor) {
        if (this.cursors.size() < 32) {
            this.cursors.add(chargeCursor);
        }
    }

    public void updateCursors(LevelAccessor levelAccessor, BlockPos nodePos, RandomSource randomSource, boolean bl) {
        if (this.cursors.isEmpty()) {
            return;
        }

        var list = new ArrayList<ChargeCursor>();
        var map = new HashMap<BlockPos, ChargeCursor>();
        var object2IntMap = new Object2IntOpenHashMap<BlockPos>();

        for (var chargeCursor : cursors) {
            chargeCursor.update(levelAccessor, nodePos, randomSource, this, bl);

            if (chargeCursor.charge <= 0) {
                // TODO: This spawns a particle effect when the charge cursor expires.
                // levelAccessor.levelEvent(3006, chargeCursor.getPos(), 0);
            } else {
                var blockPos2 = chargeCursor.getPos();
                object2IntMap.computeInt(blockPos2, (blockPosx, integer) -> (integer == null ? 0 : integer) + chargeCursor.charge);
                var chargeCursor2 = map.get(blockPos2);

                if (chargeCursor2 == null) {
                    map.put(blockPos2, chargeCursor);
                    list.add(chargeCursor);
                } else if (!isWorldGeneration() && chargeCursor.charge + chargeCursor2.charge <= 1000) {
                    chargeCursor2.mergeWith(chargeCursor);
                } else {
                    list.add(chargeCursor);

                    if (chargeCursor.charge < chargeCursor2.charge) {
                        map.put(blockPos2, chargeCursor);
                    }
                }
            }
        }

        // TODO: This spawns the numerous particle effects when blocks get transformed into resin.
        // networkEffectsToClientForAffectedBlocks(levelAccessor, object2IntMap, map);

        this.cursors = list;
    }

    private void networkEffectsToClientForAffectedBlocks(
        LevelAccessor levelAccessor,
        Object2IntMap<BlockPos> object2IntMap,
        Map<BlockPos, ChargeCursor> map
    ) {
        for (var entry : object2IntMap.object2IntEntrySet()) {
            var blockPos2 = entry.getKey();
            var i = entry.getIntValue();
            var chargeCursor3 = map.get(blockPos2);
            var collection = chargeCursor3 == null ? null : chargeCursor3.getFacingData();

            if (i > 0 && collection != null) {
                var j = (int) (Math.log1p(i) / 2.3F) + 1;
                var k = (j << 6) + MultifaceBlock.pack(collection);
                levelAccessor.levelEvent(3006, blockPos2, k);
            }
        }
    }
}
