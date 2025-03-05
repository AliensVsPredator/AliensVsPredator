package com.avp.common.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import com.avp.AVPResources;

public class AVPBiomeTags {

    public static final TagKey<Biome> HAS_ALTAR = create("has_altar");

    private static TagKey<Biome> create(String name) {
        return TagKey.create(Registries.BIOME, AVPResources.location(name));
    }
}
