package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.terrain.BlockBreakabilityEvaluator;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifiers;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Describes an entity's pathfinding capabilities: which terrain types it can traverse,
 * the cost of each, and physical dimensions for clearance checks.
 * Terrain costs can be static or dynamic (re-evaluated each pathfind call).
 */
public final class TerrainEvaluatorConfig {

    private static final float DEFAULT_COST = 1.0f;

    private static final int DEFAULT_MAX_FALL_DISTANCE = 3;

    private static final int DEFAULT_MAX_STEP_HEIGHT = 1;

    private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

    private final TerrainClassifier terrainClassifier;

    private final @Nullable BlockBreakabilityEvaluator breakabilityEvaluator;

    private final int entityWidth;

    private final int entityHeight;

    private final int maxFallDistance;

    private final int maxStepHeight;

    private final boolean canOpenDoors;

    private final boolean canWalkOverFences;

    private TerrainEvaluatorConfig(
        Map<TerrainType, Supplier<Float>> terrainCostSuppliers,
        TerrainClassifier terrainClassifier,
        @Nullable BlockBreakabilityEvaluator breakabilityEvaluator,
        int entityWidth,
        int entityHeight,
        int maxFallDistance,
        int maxStepHeight,
        boolean canOpenDoors,
        boolean canWalkOverFences
    ) {
        this.terrainCostSuppliers = Map.copyOf(terrainCostSuppliers);
        this.terrainClassifier = terrainClassifier;
        this.breakabilityEvaluator = breakabilityEvaluator;
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
        return terrainCostSuppliers.containsKey(terrainType);
    }

    /**
     * Returns the current cost for the given terrain type.
     * Dynamic costs are re-evaluated on each call.
     */
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

    public @Nullable BlockBreakabilityEvaluator getBreakabilityEvaluator() {
        return breakabilityEvaluator;
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

        private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

        private TerrainClassifier terrainClassifier;

        private int entityWidth;

        private int entityHeight;

        private int maxFallDistance;

        private int maxStepHeight;

        private @Nullable BlockBreakabilityEvaluator breakabilityEvaluator;

        private boolean canOpenDoors;

        private boolean canWalkOverFences;

        private Builder() {
            this.terrainCostSuppliers = new EnumMap<>(TerrainType.class);
            this.terrainClassifier = TerrainClassifiers.GROUND_ONLY;
            this.entityWidth = 1;
            this.entityHeight = 2;
            this.maxFallDistance = DEFAULT_MAX_FALL_DISTANCE;
            this.maxStepHeight = DEFAULT_MAX_STEP_HEIGHT;
        }

        /**
         * Adds a terrain type with a fixed cost.
         */
        public Builder addTerrain(TerrainType type, float cost) {
            terrainCostSuppliers.put(type, () -> cost);
            return this;
        }

        /**
         * Adds a terrain type with a dynamic cost supplier.
         * The supplier is evaluated each time the pathfinder needs the cost,
         * allowing costs to change at runtime (e.g., based on status effects or entity state).
         */
        public Builder addTerrain(TerrainType type, Supplier<Float> costSupplier) {
            terrainCostSuppliers.put(type, costSupplier);
            return this;
        }

        /**
         * Adds a terrain type with a fixed cost derived from the speed ratio between ground
         * and the target terrain. Cost = groundSpeed / terrainSpeed.
         */
        public Builder addTerrainFromSpeedRatio(TerrainType type, float groundSpeed, float terrainSpeed) {
            terrainCostSuppliers.put(type, () -> groundSpeed / terrainSpeed);
            return this;
        }

        /**
         * Adds a terrain type with a dynamic cost derived from speed suppliers.
         * Re-evaluated each pathfind call, so speed changes from effects or damage
         * are reflected automatically.
         */
        public Builder addTerrainFromSpeedRatio(TerrainType type, Supplier<Float> groundSpeedSupplier, Supplier<Float> terrainSpeedSupplier) {
            terrainCostSuppliers.put(type, () -> groundSpeedSupplier.get() / terrainSpeedSupplier.get());
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

        public Builder withBreakabilityEvaluator(BlockBreakabilityEvaluator evaluator) {
            this.breakabilityEvaluator = evaluator;
            return this;
        }

        public TerrainEvaluatorConfig build() {
            if (terrainCostSuppliers.isEmpty()) {
                terrainCostSuppliers.put(TerrainType.GROUND, () -> DEFAULT_COST);
            }

            return new TerrainEvaluatorConfig(
                terrainCostSuppliers,
                terrainClassifier,
                breakabilityEvaluator,
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
