package com.avp.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AVPOreConfigurations {

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> registry) {
        var placedFeatureLookup = registry.lookup(Registries.PLACED_FEATURE);

        AVPOres.getAll().forEach(data -> registry.register(data.configuredFeatureKey(), data.createConfiguredFeature()));
    }
}
