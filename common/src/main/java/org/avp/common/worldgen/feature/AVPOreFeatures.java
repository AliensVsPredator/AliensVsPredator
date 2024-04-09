package org.avp.common.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.avp.common.AVPResources;
import org.avp.common.registry.AVPRegistry;

public class AVPOreFeatures implements AVPRegistry {

    private static final AVPOreFeatures INSTANCE = new AVPOreFeatures();

    public static AVPOreFeatures getInstance() {
        return INSTANCE;
    }

    public ResourceKey<PlacedFeature> BAUXITE_ORE_PLACED_KEY;

    public ResourceKey<PlacedFeature> COBALT_ORE_PLACED_KEY;

    public ResourceKey<PlacedFeature> LITHIUM_ORE_PLACED_KEY;

    public ResourceKey<PlacedFeature> MONAZITE_ORE_PLACED_KEY;

    public ResourceKey<PlacedFeature> SILICA_ORE_PLACED_KEY;

    @Override
    public void register() {
        BAUXITE_ORE_PLACED_KEY = register("ore_bauxite");
        COBALT_ORE_PLACED_KEY = register("ore_cobalt");
        LITHIUM_ORE_PLACED_KEY = register("ore_lithium");
        MONAZITE_ORE_PLACED_KEY = register("ore_monazite");
        SILICA_ORE_PLACED_KEY = register("ore_silica");
    }

    private ResourceKey<PlacedFeature> register(String registryName) {
        return ResourceKey.create(Registries.PLACED_FEATURE, AVPResources.location(registryName));
    }
}
