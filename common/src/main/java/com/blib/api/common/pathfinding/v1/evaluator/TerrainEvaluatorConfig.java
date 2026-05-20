package com.blib.api.common.pathfinding.v1.evaluator;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifiers;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Describes an entity's pathfinding capabilities: supported terrain types with costs and physical constraints.
 */
public final class TerrainEvaluatorConfig {

    private static final float DEFAULT_COST = 1.0f;

    private static final int DEFAULT_MAX_FALL_DISTANCE = 3;

    private static final int DEFAULT_MAX_STEP_HEIGHT = 1;

    private static final int DEFAULT_ENTITY_WIDTH = 1;

    private static final int DEFAULT_ENTITY_HEIGHT = 2;

    private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

    private final TerrainClassifier terrainClassifier;

    private final int entityWidth;

    private final int entityHeight;

    private final int maxFallDistance;

    private final int maxStepHeight;

    private final boolean canOpenDoors;

    private final boolean canWalkOverFences;

    private final boolean canFly;

    private final PathCrawlConfig crawlConfig;

    private final PathWaterConfig waterConfig;

    private final PathBlockBreakingConfig blockBreakingConfig;

    private TerrainEvaluatorConfig(
        Map<TerrainType, Supplier<Float>> terrainCostSuppliers,
        TerrainClassifier terrainClassifier,
        int entityWidth,
        int entityHeight,
        int maxFallDistance,
        int maxStepHeight,
        boolean canOpenDoors,
        boolean canWalkOverFences,
        boolean canFly,
        PathCrawlConfig crawlConfig,
        PathWaterConfig waterConfig,
        PathBlockBreakingConfig blockBreakingConfig
    ) {
        this.terrainCostSuppliers = Map.copyOf(terrainCostSuppliers);
        this.terrainClassifier = terrainClassifier;
        this.entityWidth = entityWidth;
        this.entityHeight = entityHeight;
        this.maxFallDistance = maxFallDistance;
        this.maxStepHeight = maxStepHeight;
        this.canOpenDoors = canOpenDoors;
        this.canWalkOverFences = canWalkOverFences;
        this.canFly = canFly;
        this.crawlConfig = crawlConfig != null ? crawlConfig : PathCrawlConfig.DISABLED;
        this.waterConfig = waterConfig != null ? waterConfig : PathWaterConfig.DISABLED;
        this.blockBreakingConfig = blockBreakingConfig != null ? blockBreakingConfig : PathBlockBreakingConfig.DISABLED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public float getCost(TerrainType terrainType) {
        var supplier = terrainCostSuppliers.get(terrainType);

        if (supplier == null) {
            return Float.MAX_VALUE;
        }

        return supplier.get();
    }

    public Set<TerrainType> getSupportedTerrains() {
        return terrainCostSuppliers.keySet();
    }

    public TerrainClassifier getTerrainClassifier() {
        return terrainClassifier;
    }

    public int getEntityWidth() {
        return entityWidth;
    }

    public int getEntityHeight() {
        return entityHeight;
    }

    public int getMaxFallDistance() {
        return maxFallDistance;
    }

    public int getMaxStepHeight() {
        return maxStepHeight;
    }

    public boolean canOpenDoors() {
        return canOpenDoors;
    }

    public boolean canWalkOverFences() {
        return canWalkOverFences;
    }

    public boolean canFly() {
        return canFly;
    }

    public PathCrawlConfig getCrawlConfig() {
        return crawlConfig;
    }

    public PathWaterConfig getWaterConfig() {
        return waterConfig;
    }

    public PathBlockBreakingConfig getBlockBreakingConfig() {
        return blockBreakingConfig;
    }

    public static final class Builder {

        private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

        private TerrainClassifier terrainClassifier;

        private int entityWidth;

        private int entityHeight;

        private int maxFallDistance;

        private int maxStepHeight;

        private boolean canOpenDoors;

        private boolean canWalkOverFences;

        private boolean canFly;

        private PathCrawlConfig crawlConfig;

        private PathWaterConfig waterConfig;

        private PathBlockBreakingConfig blockBreakingConfig;

        private Builder() {
            this.terrainCostSuppliers = new EnumMap<>(TerrainType.class);
            this.terrainClassifier = TerrainClassifiers.GROUND_ONLY;
            this.entityWidth = DEFAULT_ENTITY_WIDTH;
            this.entityHeight = DEFAULT_ENTITY_HEIGHT;
            this.maxFallDistance = DEFAULT_MAX_FALL_DISTANCE;
            this.maxStepHeight = DEFAULT_MAX_STEP_HEIGHT;
            this.crawlConfig = PathCrawlConfig.DISABLED;
            this.waterConfig = PathWaterConfig.DISABLED;
            this.blockBreakingConfig = PathBlockBreakingConfig.DISABLED;
        }

        public Builder addTerrain(TerrainType type, float cost) {
            terrainCostSuppliers.put(type, () -> cost);
            return this;
        }

        public Builder addTerrain(TerrainType type, Supplier<Float> costSupplier) {
            terrainCostSuppliers.put(type, costSupplier);
            return this;
        }

        public Builder addTerrainFromSpeedRatio(TerrainType type, float groundSpeed, float terrainSpeed) {
            terrainCostSuppliers.put(type, () -> groundSpeed / terrainSpeed);
            return this;
        }

        public Builder addTerrainFromSpeedRatio(
            TerrainType type,
            Supplier<Float> groundSpeedSupplier,
            Supplier<Float> terrainSpeedSupplier
        ) {
            terrainCostSuppliers.put(type, () -> groundSpeedSupplier.get() / terrainSpeedSupplier.get());
            return this;
        }

        public Builder withEntitySize(int width, int height) {
            this.entityWidth = width;
            this.entityHeight = height;
            return this;
        }

        public Builder withTerrainClassifier(TerrainClassifier classifier) {
            this.terrainClassifier = classifier;
            return this;
        }

        public Builder withMaxFallDistance(int distance) {
            this.maxFallDistance = distance;
            return this;
        }

        public Builder withMaxStepHeight(int height) {
            this.maxStepHeight = height;
            return this;
        }

        public Builder withCanOpenDoors(boolean canOpenDoors) {
            this.canOpenDoors = canOpenDoors;
            return this;
        }

        public Builder withCanWalkOverFences(boolean canWalkOverFences) {
            this.canWalkOverFences = canWalkOverFences;
            return this;
        }

        public Builder withCanFly(boolean canFly) {
            this.canFly = canFly;
            return this;
        }

        public Builder withCrawlConfig(PathCrawlConfig crawlConfig) {
            this.crawlConfig = crawlConfig != null ? crawlConfig : PathCrawlConfig.DISABLED;
            return this;
        }

        public Builder withCrawling(int crawlHeight) {
            this.crawlConfig = PathCrawlConfig.enabled(crawlHeight);
            return this;
        }

        public Builder withWaterConfig(PathWaterConfig waterConfig) {
            this.waterConfig = waterConfig != null ? waterConfig : PathWaterConfig.DISABLED;
            return this;
        }

        public Builder withWaterSwimming(int swimHeight) {
            this.waterConfig = PathWaterConfig.enabled(swimHeight);
            return this;
        }

        public Builder withBlockBreakingConfig(PathBlockBreakingConfig blockBreakingConfig) {
            this.blockBreakingConfig = blockBreakingConfig != null ? blockBreakingConfig : PathBlockBreakingConfig.DISABLED;
            return this;
        }

        public Builder withBlockBreaking(PathBlockBreakPolicy breakPolicy) {
            this.blockBreakingConfig = PathBlockBreakingConfig.enabled(breakPolicy);
            return this;
        }

        public TerrainEvaluatorConfig build() {
            if (terrainCostSuppliers.isEmpty()) {
                terrainCostSuppliers.put(TerrainType.GROUND, () -> DEFAULT_COST);
            }

            return new TerrainEvaluatorConfig(
                terrainCostSuppliers,
                terrainClassifier,
                entityWidth,
                entityHeight,
                maxFallDistance,
                maxStepHeight,
                canOpenDoors,
                canWalkOverFences,
                canFly,
                crawlConfig,
                waterConfig,
                blockBreakingConfig
            );
        }
    }
}
