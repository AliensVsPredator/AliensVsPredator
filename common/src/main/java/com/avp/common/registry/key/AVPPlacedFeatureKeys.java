package com.avp.common.registry.key;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.avp.AVPResources;

public class AVPPlacedFeatureKeys {

    public static final ResourceKey<PlacedFeature> AUTUNITE_GEODE = ResourceKey.create(
        Registries.PLACED_FEATURE,
        AVPResources.location("autunite_geode")
    );
}
