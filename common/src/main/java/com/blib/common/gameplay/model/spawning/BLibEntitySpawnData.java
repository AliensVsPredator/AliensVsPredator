package com.blib.common.gameplay.model.spawning;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import com.blib.common.registry.BLibHolder;

// TODO: Change how this works.
public final class BLibEntitySpawnData<T extends Mob> {

    public static <T extends Mob> Builder<T> builder(BLibHolder<EntityType<T>> entityTypeDeferredHolder) {
        return new Builder<>(entityTypeDeferredHolder);
    }

    private final BLibHolder<EntityType<T>> entityTypeHolder;

    private final BLibEntitySpawnConfigData configData;

    private final BLibEntitySpawnPlacementData<T> placementData;

    private final boolean configDisabled;

    private final boolean placementDisabled;

    private BLibEntitySpawnData(
        BLibHolder<EntityType<T>> entityTypeHolder,
        BLibEntitySpawnConfigData configData,
        BLibEntitySpawnPlacementData<T> placementData,
        boolean configDisabled,
        boolean placementDisabled
    ) {
        this.entityTypeHolder = entityTypeHolder;
        this.configData = configData;
        this.placementData = placementData;
        this.configDisabled = configDisabled;
        this.placementDisabled = placementDisabled;
    }

    public BLibHolder<EntityType<T>> getEntityTypeHolder() {
        return entityTypeHolder;
    }

    public BLibEntitySpawnConfigData getConfigData() {
        return configData;
    }

    public BLibEntitySpawnPlacementData<T> getPlacementData() {
        return placementData;
    }

    public boolean isConfigDisabled() {
        return configDisabled;
    }

    public boolean isPlacementDisabled() {
        return placementDisabled;
    }

    public static class Builder<T extends Mob> {

        private final BLibHolder<EntityType<T>> entityTypeDeferredHolder;

        private TagKey<Biome> biomeTagKey;

        private SpawnSettings spawnSettings;

        private Heightmap.Types heightmapType;

        private SpawnPlacementType spawnPlacementType;

        private SpawnPlacements.SpawnPredicate<T> spawnPredicate;

        private boolean configDisabled;

        private boolean placementDisabled;

        private Builder(BLibHolder<EntityType<T>> entityTypeDeferredHolder) {
            this.entityTypeDeferredHolder = entityTypeDeferredHolder;
            this.biomeTagKey = BiomeTags.IS_OVERWORLD;
            this.heightmapType = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
            this.spawnPlacementType = SpawnPlacementTypes.ON_GROUND;
            this.spawnSettings = new SpawnSettings(true, 1, 3, 10);
            this.spawnPredicate = Mob::checkMobSpawnRules;
            this.configDisabled = false;
            this.placementDisabled = false;
        }

        public Builder<T> withBiomeTagKey(TagKey<Biome> biomeTagKey) {
            this.biomeTagKey = biomeTagKey;
            return this;
        }

        public Builder<T> withHeightmapType(Heightmap.Types heightmapType) {
            this.heightmapType = heightmapType;
            return this;
        }

        public Builder<T> withSpawnPlacement(SpawnPlacementType spawnPlacementType) {
            this.spawnPlacementType = spawnPlacementType;
            return this;
        }

        public Builder<T> withSpawnSettings(SpawnSettings spawnSettings) {
            this.spawnSettings = spawnSettings;
            return this;
        }

        public Builder<T> withSpawnPredicate(SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
            this.spawnPredicate = spawnPredicate;
            return this;
        }

        public Builder<T> disableConfig() {
            this.configDisabled = true;
            return this;
        }

        public Builder<T> disablePlacement() {
            this.placementDisabled = true;
            return this;
        }

        public BLibEntitySpawnData<T> build() {
            SpawnPlacements.SpawnPredicate<T> proxySpawnPredicate = (
                entityType,
                serverLevelAccessor,
                mobSpawnType,
                blockPos,
                randomSource
            ) -> spawnSettings.enabled() && spawnPredicate.test(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);

            return new BLibEntitySpawnData<>(
                entityTypeDeferredHolder,
                new BLibEntitySpawnConfigData(biomeTagKey, spawnSettings),
                new BLibEntitySpawnPlacementData<>(spawnPlacementType, heightmapType, proxySpawnPredicate),
                configDisabled,
                placementDisabled
            );
        }
    }
}
