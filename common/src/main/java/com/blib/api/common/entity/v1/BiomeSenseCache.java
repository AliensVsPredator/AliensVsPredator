package com.blib.api.common.entity.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Set;

public class BiomeSenseCache {

    private final Entity entity;

    private final Set<Holder<Biome>> nearbyBiomes;

    private final int radiusInBlocks;

    private final int tickFrequency;

    private BlockPos lastSensePosition;

    private int lastSenseTick;

    public BiomeSenseCache(Entity entity, int radiusInBlocks, int tickFrequency) {
        this.entity = entity;
        this.nearbyBiomes = new HashSet<>();
        this.radiusInBlocks = radiusInBlocks;
        this.tickFrequency = tickFrequency;
        this.lastSensePosition = entity.blockPosition();
        this.lastSenseTick = 0;
    }

    public boolean isNearby(TagKey<Biome> tagKey) {
        tryPopulateCache();

        for (var biome : nearbyBiomes) {
            if (biome.is(tagKey)) {
                return true;
            }
        }

        return false;
    }

    public boolean isNearby(Holder<Biome> biome) {
        tryPopulateCache();

        return nearbyBiomes.contains(biome);
    }

    private void tryPopulateCache() {
        if (
            entity.tickCount <= lastSenseTick + tickFrequency
                && entity.blockPosition().equals(lastSensePosition)
        ) {
            return;
        }

        nearbyBiomes.clear();

        nearbyBiomes.addAll(sense(entity));

        this.lastSensePosition = entity.blockPosition();
        this.lastSenseTick = entity.tickCount;
    }

    private Set<Holder<Biome>> sense(Entity entity) {
        var level = entity.level();
        var origin = entity.blockPosition();

        // Convert the block radius into a quart radius (ceil division by 4).
        var radiusQuarts = (radiusInBlocks + 3) >> 2;

        var originQx = QuartPos.fromBlock(origin.getX());
        var originQy = QuartPos.fromBlock(origin.getY());
        var originQz = QuartPos.fromBlock(origin.getZ());

        var result = new HashSet<Holder<Biome>>();

        for (var qx = originQx - radiusQuarts; qx <= originQx + radiusQuarts; qx++) {
            for (var qy = originQy - radiusQuarts; qy <= originQy + radiusQuarts; qy++) {
                for (var qz = originQz - radiusQuarts; qz <= originQz + radiusQuarts; qz++) {

                    // Pick the "corner" block of that quart cell.
                    var bx = QuartPos.toBlock(qx);
                    var by = QuartPos.toBlock(qy);
                    var bz = QuartPos.toBlock(qz);

                    var samplePos = new BlockPos(bx, by, bz);

                    result.add(level.getBiome(samplePos));
                }
            }
        }

        return result;
    }
}
