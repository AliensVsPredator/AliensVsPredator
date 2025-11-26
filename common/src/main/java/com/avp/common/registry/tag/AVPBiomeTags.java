package com.avp.common.registry.tag;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class AVPBiomeTags {

    public static final TagKey<Biome> HAS_MARINE_CAMP_GRASS = create("has_marine_camp_grass");

    public static final TagKey<Biome> HAS_MOBILE_LAB = create("has_mobile_lab");

    public static final TagKey<Biome> HAS_OUTPOST_COMMS = create("has_outpost_comms");

    public static final TagKey<Biome> HAS_OUTPOST_MUNITION = create("has_outpost_munition");

    public static final TagKey<Biome> HAS_OUTPOST_SUPPLY_BADLAND = create("has_outpost_supply_badland");

    public static final TagKey<Biome> HAS_OUTPOST_SUPPLY_DESERT = create("has_outpost_supply_desert");

    public static final TagKey<Biome> IS_IRRADIATED = create("is_irradiated");

    private static TagKey<Biome> create(String name) {
        return TagKey.create(Registries.BIOME, AVPResources.location(name));
    }
}
