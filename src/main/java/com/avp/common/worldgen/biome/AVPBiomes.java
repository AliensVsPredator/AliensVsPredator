package com.avp.common.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import com.avp.AVPResources;

public class AVPBiomes {

    public static final ResourceKey<Biome> NUKED_BIOME = register("nuked_biome");

    private static ResourceKey<Biome> register(String id) {
        return ResourceKey.create(Registries.BIOME, AVPResources.location(id));
    }

    public static void initialize() {}
}
