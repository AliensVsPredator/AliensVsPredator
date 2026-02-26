package com.blib.api.common.entity.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ToIntFunction;

public class BiomeSenseCache {

    private final Entity entity;

    private final Set<Holder<Biome>> nearbyBiomes;

    private final RefreshPolicy<BiomeSenseCache> refreshPolicy;

    private final ToIntFunction<BiomeSenseCache> scanRadiusFunction;

    private BlockPos lastSensePosition;

    private int lastSenseTick;

    private BiomeSenseCache(
        Entity entity,
        RefreshPolicy<BiomeSenseCache> refreshPolicy,
        ToIntFunction<BiomeSenseCache> scanRadiusFunction
    ) {
        this.entity = entity;
        this.nearbyBiomes = new HashSet<>();
        this.refreshPolicy = refreshPolicy;
        this.scanRadiusFunction = scanRadiusFunction;
        this.lastSensePosition = entity.blockPosition();
        this.lastSenseTick = 0;
    }

    public static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    public void clear() {
        nearbyBiomes.clear();
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getLastSensePosition() {
        return lastSensePosition;
    }

    public int getLastSenseTick() {
        return lastSenseTick;
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
        if (!refreshPolicy.shouldRefresh(this)) {
            return;
        }

        clear();

        var biomes = sense(entity);

        nearbyBiomes.addAll(biomes);

        this.lastSensePosition = entity.blockPosition();
        this.lastSenseTick = entity.tickCount;
    }

    private Set<Holder<Biome>> sense(Entity entity) {
        var radiusInBlocks = scanRadiusFunction.applyAsInt(this);
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

    public static class Builder {

        private final Entity entity;

        private RefreshPolicy<BiomeSenseCache> refreshPolicy;

        private ToIntFunction<BiomeSenseCache> scanRadiusFunction;

        private Builder(Entity entity) {
            this.entity = entity;

            this.refreshPolicy = context -> context.getEntity().tickCount > context.getLastSenseTick() + 20
                || !context.getEntity().blockPosition().equals(context.getLastSensePosition());
            this.scanRadiusFunction = $ -> 64;
        }

        public Builder withRefreshPolicy(RefreshPolicy<BiomeSenseCache> refreshPolicy) {
            this.refreshPolicy = refreshPolicy;
            return this;
        }

        public Builder withScanRadius(int scanRadius) {
            this.scanRadiusFunction = $ -> scanRadius;
            return this;
        }

        public Builder withScanRadius(ToIntFunction<BiomeSenseCache> scanRadiusFunction) {
            this.scanRadiusFunction = scanRadiusFunction;
            return this;
        }

        public BiomeSenseCache build() {
            return new BiomeSenseCache(entity, refreshPolicy, scanRadiusFunction);
        }
    }
}
