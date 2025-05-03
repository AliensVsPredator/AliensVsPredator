package com.avp.data.worldgen.builder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.avp.AVPResources;

public record AVPOreData(
    String name,
    ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
    ResourceKey<PlacedFeature> placedFeatureKey,
    OreConfiguration oreConfiguration,
    List<PlacementModifier> modifiers
) {

    // WARNING: THIS ORDER MATTERS. DO NOT CHANGE IT OR I WILL FIND YOU.
    private static final Comparator<PlacementModifier> MODIFIER_COMPARATOR = Comparator.comparingInt(mod -> switch (mod) {
        case RepeatingPlacement $ -> 1;
        case InSquarePlacement $ -> 2;
        case HeightRangePlacement $ -> 3;
        case PlacementFilter $ -> 4;
        default -> Integer.MAX_VALUE;
    });

    public ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>> createConfiguredFeature() {
        return new ConfiguredFeature<>(Feature.ORE, oreConfiguration);
    }

    public PlacedFeature createPlacedFeature(HolderGetter<ConfiguredFeature<?, ?>> holder) {
        return new PlacedFeature(holder.getOrThrow(configuredFeatureKey), modifiers);
    }

    @Override
    public String name() {
        return name;
    }

    public static Builder builder(String name, OreConfiguration.TargetBlockState targetBlockState) {
        return builder(name, List.of(targetBlockState));
    }

    public static Builder builder(String name, List<OreConfiguration.TargetBlockState> targetBlockStates) {
        return new Builder(name, targetBlockStates);
    }

    public static class Builder {

        private final String name;

        private final List<OreConfiguration.TargetBlockState> targetBlockStates;

        private final List<PlacementModifier> modifiers;

        private int veinSize;

        private float normalizedAirDiscardChance;

        private Builder(String name, List<OreConfiguration.TargetBlockState> targetBlockStates) {
            this.name = name;
            this.targetBlockStates = new ArrayList<>(targetBlockStates);
            this.modifiers = new ArrayList<>();
            this.normalizedAirDiscardChance = 0F;
            this.veinSize = 9;
        }

        /*
         * CONFIGURED FEATURE DATA
         */

        public Builder normalizedAirDiscardChance(float normalizedAirDiscardChance) {
            this.normalizedAirDiscardChance = normalizedAirDiscardChance;
            return this;
        }

        public Builder target(OreConfiguration.TargetBlockState targetBlockState) {
            targetBlockStates.add(targetBlockState);
            return this;
        }

        public Builder veinSize(int veinSize) {
            this.veinSize = veinSize;
            return this;
        }

        /*
         * PLACED FEATURE DATA
         */

        public Builder biomeFilter(BiomeFilter biomeFilter) {
            modifiers.add(biomeFilter);
            return this;
        }

        public Builder count(int count) {
            modifiers.add(CountPlacement.of(count));
            return this;
        }

        public Builder heightRange(HeightRangePlacement heightRangePlacement) {
            modifiers.add(heightRangePlacement);
            return this;
        }

        public Builder spread() {
            modifiers.add(InSquarePlacement.spread());
            return this;
        }

        public AVPOreData build() {
            var resourceLocation = AVPResources.location(name);
            var configuredFeatureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, resourceLocation);
            var placedFeatureKey = ResourceKey.create(Registries.PLACED_FEATURE, resourceLocation);

            var oreConfiguration = new OreConfiguration(targetBlockStates, veinSize, normalizedAirDiscardChance);

            // Sorting the modifiers
            modifiers.sort(MODIFIER_COMPARATOR);

            return new AVPOreData(name, configuredFeatureKey, placedFeatureKey, oreConfiguration, modifiers);
        }
    }
}
