package com.avp.common.entity.spawning;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import com.avp.common.config.AVPConfig;
import com.avp.common.registry.AVPDeferredHolder;

public final class AVPEntitySpawnData<T extends Mob> {

    public static <T extends Mob> Builder<T> builder(AVPDeferredHolder<EntityType<T>> entityTypeDeferredHolder) {
        return new Builder<>(entityTypeDeferredHolder);
    }

    private final AVPDeferredHolder<EntityType<T>> entityTypeDeferredHolder;

    private final AVPEntitySpawnConfigData configData;

    private final AVPEntitySpawnPlacementData<T> placementData;

    private final boolean configDisabled;

    private final boolean placementDisabled;

    private AVPEntitySpawnData(
        AVPDeferredHolder<EntityType<T>> entityTypeDeferredHolder,
        AVPEntitySpawnConfigData configData,
        AVPEntitySpawnPlacementData<T> placementData,
        boolean configDisabled,
        boolean placementDisabled
    ) {
        this.entityTypeDeferredHolder = entityTypeDeferredHolder;
        this.configData = configData;
        this.placementData = placementData;
        this.configDisabled = configDisabled;
        this.placementDisabled = placementDisabled;
    }

    public EntityType<T> getEntityType() {
        return entityTypeDeferredHolder.get();
    }

    public AVPEntitySpawnConfigData getConfigData() {
        return configData;
    }

    public AVPEntitySpawnPlacementData<T> getPlacementData() {
        return placementData;
    }

    public boolean isConfigDisabled() {
        return configDisabled;
    }

    public boolean isPlacementDisabled() {
        return placementDisabled;
    }

    public static class Builder<T extends Mob> {

        private final AVPDeferredHolder<EntityType<T>> entityTypeDeferredHolder;

        private TagKey<Biome> biomeTagKey;

        private AVPConfig.SpawnConfigs.SpawnSettings spawnSettings;

        private Heightmap.Types heightmapType;

        private SpawnPlacementType spawnPlacementType;

        private SpawnPlacements.SpawnPredicate<T> spawnPredicate;

        private boolean configDisabled;

        private boolean placementDisabled;

        public Builder(AVPDeferredHolder<EntityType<T>> entityTypeDeferredHolder) {
            this.entityTypeDeferredHolder = entityTypeDeferredHolder;
            this.biomeTagKey = BiomeTags.IS_OVERWORLD;
            this.heightmapType = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
            this.spawnPlacementType = SpawnPlacementTypes.ON_GROUND;
            this.spawnSettings = new AVPConfig.SpawnConfigs.SpawnSettings(true, 1, 3, 10);
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

        public Builder<T> withSpawnSettings(AVPConfig.SpawnConfigs.SpawnSettings spawnSettings) {
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

        public AVPEntitySpawnData<T> build() {
            SpawnPlacements.SpawnPredicate<T> proxySpawnPredicate = (
                entityType,
                serverLevelAccessor,
                mobSpawnType,
                blockPos,
                randomSource
            ) -> spawnSettings.enabled && spawnPredicate.test(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);

            return new AVPEntitySpawnData<>(
                entityTypeDeferredHolder,
                new AVPEntitySpawnConfigData(biomeTagKey, spawnSettings),
                new AVPEntitySpawnPlacementData<>(spawnPlacementType, heightmapType, proxySpawnPredicate),
                configDisabled,
                placementDisabled
            );
        }
    }
}
