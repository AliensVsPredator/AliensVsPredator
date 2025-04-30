package com.avp.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.ArrayList;

import com.avp.AVPResources;

public class AVPCavePlacements {

    public static final ResourceKey<PlacedFeature> AUTUNITE_GEODE = ResourceKey.create(
        Registries.PLACED_FEATURE,
        AVPResources.location("autunite_geode")
    );

    public static void bootstrap(BootstrapContext<PlacedFeature> registry) {
        var configuredFeatureLookup = registry.lookup(Registries.CONFIGURED_FEATURE);

        registry.register(AUTUNITE_GEODE, createAutuniteGeodePlacement(configuredFeatureLookup));
    }

    private static PlacedFeature createAutuniteGeodePlacement(HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureLookup) {
        var modifiers = new ArrayList<PlacementModifier>();

        modifiers.add(RarityFilter.onAverageOnceEvery(24));
        modifiers.add(HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)));
        modifiers.add(InSquarePlacement.spread());
        modifiers.add(BiomeFilter.biome());

        return new PlacedFeature(configuredFeatureLookup.getOrThrow(AVPCaveConfigurations.AUTUNITE_GEODE), modifiers);
    }
}
