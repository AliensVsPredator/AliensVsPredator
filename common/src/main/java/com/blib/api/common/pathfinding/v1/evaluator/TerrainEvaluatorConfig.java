package com.blib.api.common.pathfinding.v1.evaluator;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.terrain.BlockBreakabilityEvaluator;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifiers;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Describes an entity's pathfinding capabilities: supported terrain types with costs, postures with dimensions, and
 * physical constraints.
 */
public final class TerrainEvaluatorConfig {

    private static final float DEFAULT_COST = 1.0f;

    private static final int DEFAULT_MAX_FALL_DISTANCE = 3;

    private static final int DEFAULT_MAX_STEP_HEIGHT = 1;

    private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

    private final TerrainClassifier terrainClassifier;

    private final @Nullable BlockBreakabilityEvaluator breakabilityEvaluator;

    private final List<Posture> postures;

    private final Map<Integer, Float> postureTransitionCosts;

    private final int maxFallDistance;

    private final int maxStepHeight;

    private final boolean canOpenDoors;

    private final boolean canWalkOverFences;

    private final int climbingPostureIndex;

    private TerrainEvaluatorConfig(
        Map<TerrainType, Supplier<Float>> terrainCostSuppliers,
        TerrainClassifier terrainClassifier,
        @Nullable BlockBreakabilityEvaluator breakabilityEvaluator,
        List<Posture> postures,
        Map<Integer, Float> postureTransitionCosts,
        int maxFallDistance,
        int maxStepHeight,
        boolean canOpenDoors,
        boolean canWalkOverFences,
        int climbingPostureIndex
    ) {
        this.terrainCostSuppliers = Map.copyOf(terrainCostSuppliers);
        this.terrainClassifier = terrainClassifier;
        this.breakabilityEvaluator = breakabilityEvaluator;
        this.postures = List.copyOf(postures);
        this.postureTransitionCosts = Map.copyOf(postureTransitionCosts);
        this.maxFallDistance = maxFallDistance;
        this.maxStepHeight = maxStepHeight;
        this.canOpenDoors = canOpenDoors;
        this.canWalkOverFences = canWalkOverFences;
        this.climbingPostureIndex = climbingPostureIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean supportsTerrain(TerrainType terrainType) {
        return terrainCostSuppliers.containsKey(terrainType);
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

    public @Nullable BlockBreakabilityEvaluator getBreakabilityEvaluator() {
        return breakabilityEvaluator;
    }

    public TerrainClassifier getTerrainClassifier() {
        return terrainClassifier;
    }

    public List<Posture> getPostures() {
        return postures;
    }

    public int getPostureCount() {
        return postures.size();
    }

    public Posture getPosture(int index) {
        return postures.get(index);
    }

    public int getEntityWidth(int postureIndex) {
        return postures.get(postureIndex).width();
    }

    public int getEntityHeight(int postureIndex) {
        return postures.get(postureIndex).height();
    }

    public float getPostureTransitionCost(int postureIndex) {
        return postureTransitionCosts.getOrDefault(postureIndex, 0.0f);
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

    /**
     * Returns the posture index required for CLIMBABLE terrain, or -1 if no specific posture is required.
     */
    public int getClimbingPostureIndex() {
        return climbingPostureIndex;
    }

    public static final class Builder {

        private final Map<TerrainType, Supplier<Float>> terrainCostSuppliers;

        private final List<Posture> postures;

        private final Map<Integer, Float> postureTransitionCosts;

        private TerrainClassifier terrainClassifier;

        private int maxFallDistance;

        private int maxStepHeight;

        private @Nullable BlockBreakabilityEvaluator breakabilityEvaluator;

        private boolean canOpenDoors;

        private boolean canWalkOverFences;

        private int climbingPostureIndex = -1;

        private Builder() {
            this.terrainCostSuppliers = new EnumMap<>(TerrainType.class);
            this.postures = new ArrayList<>();
            this.postureTransitionCosts = new HashMap<>();
            this.terrainClassifier = TerrainClassifiers.GROUND_ONLY;
            this.maxFallDistance = DEFAULT_MAX_FALL_DISTANCE;
            this.maxStepHeight = DEFAULT_MAX_STEP_HEIGHT;
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

        /**
         * Adds a posture with zero transition cost. The first posture added is the default (index 0).
         */
        public Builder addPosture(Posture posture) {
            postures.add(posture);
            return this;
        }

        /**
         * Adds a posture with a transition cost. The cost is applied when switching TO this posture.
         */
        public Builder addPosture(Posture posture, float transitionCost) {
            var index = postures.size();

            postures.add(posture);
            postureTransitionCosts.put(index, transitionCost);

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

        public Builder withBreakabilityEvaluator(BlockBreakabilityEvaluator evaluator) {
            this.breakabilityEvaluator = evaluator;
            return this;
        }

        /**
         * Sets the posture index that CLIMBABLE nodes must use. All climbable path nodes will be generated with this
         * posture, ensuring the entity adopts the correct dimensions and animation while climbing. Defaults to -1 (no
         * forced posture).
         */
        public Builder withClimbingPostureIndex(int postureIndex) {
            this.climbingPostureIndex = postureIndex;
            return this;
        }

        public TerrainEvaluatorConfig build() {
            if (terrainCostSuppliers.isEmpty()) {
                terrainCostSuppliers.put(TerrainType.GROUND, () -> DEFAULT_COST);
            }

            if (postures.isEmpty()) {
                postures.add(Posture.DEFAULT);
            }

            return new TerrainEvaluatorConfig(
                terrainCostSuppliers,
                terrainClassifier,
                breakabilityEvaluator,
                postures,
                postureTransitionCosts,
                maxFallDistance,
                maxStepHeight,
                canOpenDoors,
                canWalkOverFences,
                climbingPostureIndex
            );
        }
    }
}
