package com.avp.data.worldgen.builder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

public class AVPPlacedFeatureBuilder {

    public static AVPPlacedFeatureBuilder create(Holder.Reference<ConfiguredFeature<?, ?>> holder) {
        return new AVPPlacedFeatureBuilder(holder);
    }

    private final Holder.Reference<ConfiguredFeature<?, ?>> holder;

    private final List<PlacementModifier> modifiers;

    private AVPPlacedFeatureBuilder(Holder.Reference<ConfiguredFeature<?, ?>> holder) {
        this.holder = holder;
        this.modifiers = new ArrayList<>();
    }

    public AVPPlacedFeatureBuilder biomeFilter(BiomeFilter biomeFilter) {
        modifiers.add(biomeFilter);
        return this;
    }

    public AVPPlacedFeatureBuilder count(int count) {
        return count(CountPlacement.of(count));
    }

    public AVPPlacedFeatureBuilder count(CountPlacement countPlacement) {
        modifiers.add(countPlacement);
        return this;
    }

    public AVPPlacedFeatureBuilder heightRange(HeightRangePlacement heightRangePlacement) {
        modifiers.add(heightRangePlacement);
        return this;
    }

    public AVPPlacedFeatureBuilder spread() {
        modifiers.add(InSquarePlacement.spread());
        return this;
    }

    public PlacedFeature build() {
        return new PlacedFeature(holder, modifiers);
    }
}
