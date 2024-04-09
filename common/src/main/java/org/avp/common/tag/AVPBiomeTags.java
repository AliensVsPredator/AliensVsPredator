package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.avp.common.AVPResources;

public class AVPBiomeTags {

    public static final TagKey<Biome> IS_WET;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static TagKey<Biome> create(String registryName) {
        return TagKey.create(Registries.BIOME, AVPResources.location(registryName));
    }

    static {
        IS_WET = create("is_wet");
    }
}
