package com.avp.common.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.avp.AVPResources;

public class AVPBlockTags {

    public static final TagKey<Block> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<Block> CONCRETE = create("concrete");

    public static final TagKey<Block> CRAFTED_RESIN = create("crafted_resin");

    public static final TagKey<Block> XENOMORPH_IMMUNE = create("xenomorph_immune");

    public static final TagKey<Block> FERROALUMINUM = create("ferroaluminum");

    public static final TagKey<Block> INDUSTRIAL_CONCRETE = create("industrial_concrete");

    public static final TagKey<Block> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Block> INDUSTRIAL_GLASS_BLOCK = create("industrial_glass_pane");

    public static final TagKey<Block> INDUSTRIAL_GLASS_PANE = create("industrial_glass_pane");

    public static final TagKey<Block> NETHER_ACID_IMMUNE = create("nether_acid_immune");

    public static final TagKey<Block> IRRADIATED_ACID_IMMUNE = create("irradiated_acid_immune");

    public static final TagKey<Block> IRRADIATED_RESIN = create("irradiated_resin");

    public static final TagKey<Block> ABERRANT_RESIN = create("aberrant_resin");

    public static final TagKey<Block> NETHER_RESIN = create("nether_resin");

    public static final TagKey<Block> NORMAL_RESIN = create("normal_resin");

    public static final TagKey<Block> MARINE_SPAWN_BLOCKS = create("marine_spawn_blocks");

    public static final TagKey<Block> PADDING = create("padding");

    public static final TagKey<Block> PLASTIC = create("plastic");

    public static final TagKey<Block> RAZOR_WIRE = create("razor_wire");

    public static final TagKey<Block> RESIN = create("resin");

    public static final TagKey<Block> RESIN_BLOCKS = create("resin_blocks");

    public static final TagKey<Block> RESIN_NODES = create("resin_nodes");

    public static final TagKey<Block> RESIN_REPLACEABLE = create("resin_replaceable");

    public static final TagKey<Block> RESIN_VEINS = create("resin_veins");

    public static final TagKey<Block> RESIN_WEBS = create("resin_webs");

    public static final TagKey<Block> SHOULD_NOT_BE_DESTROYED = create("should_not_be_destroyed");

    public static final TagKey<Block> STEEL = create("steel");

    public static final TagKey<Block> TITANIUM = create("titanium");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, AVPResources.location(name));
    }
}
