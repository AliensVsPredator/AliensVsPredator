package org.avp.common.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.avp.common.AVPResources;

public class AVPOreFeatures {

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static final ResourceKey<PlacedFeature> BAUXITE_ORE_PLACED_KEY;

    public static final ResourceKey<PlacedFeature> COBALT_ORE_PLACED_KEY;

    public static final ResourceKey<PlacedFeature> LITHIUM_ORE_PLACED_KEY;

    public static final ResourceKey<PlacedFeature> MONAZITE_ORE_PLACED_KEY;

    public static final ResourceKey<PlacedFeature> SILICA_ORE_PLACED_KEY;

    static {
        BAUXITE_ORE_PLACED_KEY = register("ore_bauxite");
        COBALT_ORE_PLACED_KEY = register("ore_cobalt");
        LITHIUM_ORE_PLACED_KEY = register("ore_lithium");
        MONAZITE_ORE_PLACED_KEY = register("ore_monazite");
        SILICA_ORE_PLACED_KEY = register("ore_silica");
    }

    private static ResourceKey<PlacedFeature> register(String registryName) {
        return ResourceKey.create(Registries.PLACED_FEATURE, AVPResources.location(registryName));
    }

    private AVPOreFeatures() {
        throw new UnsupportedOperationException();
    }
}
