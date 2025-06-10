package com.avp.common.registry.key;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;

import com.avp.AVPResources;

public class AVPBiomeKeys {

    public static final ResourceKey<Biome> NUKED_BIOME = register("nuked_biome");

    private static ResourceKey<Biome> register(String id) {
        return ResourceKey.create(Registries.BIOME, AVPResources.location(id));
    }

    public static void initialize() {}
}
