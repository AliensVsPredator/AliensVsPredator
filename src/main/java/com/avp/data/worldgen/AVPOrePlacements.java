package com.avp.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class AVPOrePlacements {

    public static void bootstrap(BootstrapContext<PlacedFeature> registry) {
        var configuredFeatureLookup = registry.lookup(Registries.CONFIGURED_FEATURE);

        AVPOres.getAll().forEach(data -> registry.register(data.placedFeatureKey(), data.createPlacedFeature(configuredFeatureLookup)));
    }
}
