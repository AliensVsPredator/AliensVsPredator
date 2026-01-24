package com.blib.api.common.tag.v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CommonBlockTags {

    public static final TagKey<Block> CHESTS = create("chests");

    public static final TagKey<Block> ORES = create("ores");

    public static final TagKey<Block> STORAGE_BLOCKS = create("storage_blocks");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, CommonConstants.location(name));
    }
}
