package com.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CommonBlockTags {

    public static final TagKey<Block> CHESTS = create("chests");

    public static final TagKey<Block> ORES_BLOCKS = create("ores");

    public static final TagKey<Block> STORAGE_BLOCKS = create("storage_blocks");

    public static final TagKey<Block> STORAGE_BLOCKS_ALUMINUM = create("storage_blocks/aluminum");

    public static final TagKey<Block> STORAGE_BLOCKS_BRASS = create("storage_blocks/brass");

    public static final TagKey<Block> STORAGE_BLOCKS_FERROALUMINUM = create("storage_blocks/ferroaluminum");

    public static final TagKey<Block> STORAGE_BLOCKS_LEAD = create("storage_blocks/lead");

    public static final TagKey<Block> STORAGE_BLOCKS_RAW_ALUMINUM = create("storage_blocks/raw_aluminum");

    public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = create("storage_blocks/raw_lead");

    public static final TagKey<Block> STORAGE_BLOCKS_RAW_TITANIUM = create("storage_blocks/raw_titanium");

    public static final TagKey<Block> STORAGE_BLOCKS_RAW_ZINC = create("storage_blocks/raw_zinc");

    public static final TagKey<Block> STORAGE_BLOCKS_STEEL = create("storage_blocks/steel");

    public static final TagKey<Block> STORAGE_BLOCKS_TITANIUM = create("storage_blocks/titanium");

    public static final TagKey<Block> STORAGE_BLOCKS_URANIUM = create("storage_blocks/uranium");

    public static final TagKey<Block> STORAGE_BLOCKS_ZINC = create("storage_blocks/zinc");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, CommonConstants.location(name));
    }
}
