package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifiers;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Describes an entity's pathfinding capabilities: which terrain types it can traverse,
 * the cost of each, and physical dimensions for clearance checks.
 */
public final class TerrainEvaluatorConfig {

    private static final float DEFAULT_COST = 1.0f;

    private static final int DEFAULT_MAX_FALL_DISTANCE = 3;

    private static final int DEFAULT_MAX_STEP_HEIGHT = 1;

    private final Map<TerrainType, Float> terrainCosts;

    private final TerrainClassifier terrainClassifier;

    private final int entityWidth;

    private final int entityHeight;

    private final int maxFallDistance;

    private final int maxStepHeight;

    private final boolean canOpenDoors;

    private final boolean canWalkOverFences;

    private TerrainEvaluatorConfig(
        Map<TerrainType, Float> terrainCosts,
        TerrainClassifier terrainClassifier,
        int entityWidth,
        int entityHeight,
        int maxFallDistance,
        int maxStepHeight,
        boolean canOpenDoors,
        boolean canWalkOverFences
    ) {
        this.terrainCosts = Map.copyOf(terrainCosts);
        this.terrainClassifier = terrainClassifier;
        this.entityWidth = entityWidth;
        this.entityHeight = entityHeight;
        this.maxFallDistance = maxFallDistance;
        this.maxStepHeight = maxStepHeight;
        this.canOpenDoors = canOpenDoors;
        this.canWalkOverFences = canWalkOverFences;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean supportsTerrain(TerrainType terrainType) {
        return terrainCosts.containsKey(terrainType);
    }

    public float getCost(TerrainType terrainType) {
        return terrainCosts.getOrDefault(terrainType, Float.MAX_VALUE);
    }

    public Set<TerrainType> getSupportedTerrains() {
        return terrainCosts.keySet();
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

    public static final class Builder {

        private final Map<TerrainType, Float> terrainCosts;

        private TerrainClassifier terrainClassifier;

        private int entityWidth;

        private int entityHeight;

        private int maxFallDistance;

        private int maxStepHeight;

        private boolean canOpenDoors;

        private boolean canWalkOverFences;

        private Builder() {
            this.terrainCosts = new EnumMap<>(TerrainType.class);
            this.terrainClassifier = TerrainClassifiers.GROUND_ONLY;
            this.entityWidth = 1;
            this.entityHeight = 2;
            this.maxFallDistance = DEFAULT_MAX_FALL_DISTANCE;
            this.maxStepHeight = DEFAULT_MAX_STEP_HEIGHT;
        }

        public Builder addTerrain(TerrainType type, float cost) {
            terrainCosts.put(type, cost);
            return this;
        }

        public Builder withTerrainClassifier(TerrainClassifier classifier) {
            this.terrainClassifier = classifier;
            return this;
        }

        public Builder withEntityDimensions(int width, int height) {
            this.entityWidth = width;
            this.entityHeight = height;
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

        public TerrainEvaluatorConfig build() {
            if (terrainCosts.isEmpty()) {
                terrainCosts.put(TerrainType.GROUND, DEFAULT_COST);
            }

            return new TerrainEvaluatorConfig(
                terrainCosts,
                terrainClassifier,
                entityWidth,
                entityHeight,
                maxFallDistance,
                maxStepHeight,
                canOpenDoors,
                canWalkOverFences
            );
        }
    }
}
