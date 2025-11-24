package com.avp.common.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.avp.AVPResources;

public class AVPBlockTags {

    public static final TagKey<Block> CONCRETE = create("concrete");

    public static final TagKey<Block> FERROALUMINUM = create("ferroaluminum");

    public static final TagKey<Block> INDUSTRIAL_CONCRETE = create("industrial_concrete");

    public static final TagKey<Block> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Block> INDUSTRIAL_GLASS_BLOCK = create("industrial_glass_block");

    public static final TagKey<Block> INDUSTRIAL_GLASS_PANE = create("industrial_glass_pane");

    public static final TagKey<Block> MARINE_SPAWN_BLOCKS = create("marine_spawn_blocks");

    public static final TagKey<Block> PADDING = create("padding");

    public static final TagKey<Block> PLASTIC = create("plastic");

    public static final TagKey<Block> RAZOR_WIRE = create("razor_wire");

    public static final TagKey<Block> SHOULD_NOT_BE_DESTROYED = create("should_not_be_destroyed");

    public static final TagKey<Block> STEEL = create("steel");

    public static final TagKey<Block> TITANIUM = create("titanium");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, AVPResources.location(name));
    }
}
