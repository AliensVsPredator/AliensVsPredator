package com.avp.fabric.common.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import com.avp.common.AVPResources;

public class AVPBiomeTags {

    public static final TagKey<Biome> HAS_ALTAR = create("has_altar");

    public static final TagKey<Biome> HAS_BADLANDS_ALTAR = create("has_badlands_altar");

    public static final TagKey<Biome> HAS_DESERT_ALTAR = create("has_desert_altar");

    public static final TagKey<Biome> HAS_DEEPSLATE_ALTAR = create("has_deepslate_altar");

    public static final TagKey<Biome> HAS_JUNGLE_ALTAR = create("has_jungle_altar");

    public static final TagKey<Biome> HAS_NETHER_ALTAR = create("has_nether_altar");

    public static final TagKey<Biome> HAS_MARINE_CAMP_GRASS = create("has_marine_camp_grass");

    public static final TagKey<Biome> HAS_MOBILE_LAB = create("has_mobile_lab");

    public static final TagKey<Biome> HAS_OUTPOST_COMMS = create("has_outpost_comms");

    public static final TagKey<Biome> HAS_OUTPOST_MUNITION = create("has_outpost_munition");

    public static final TagKey<Biome> HAS_OUTPOST_SUPPLY_BADLAND = create("has_outpost_supply_badland");

    public static final TagKey<Biome> HAS_OUTPOST_SUPPLY_DESERT = create("has_outpost_supply_desert");

    private static TagKey<Biome> create(String name) {
        return TagKey.create(Registries.BIOME, AVPResources.location(name));
    }
}
