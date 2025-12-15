package com.blib.common.data.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.blib.BLib;

public class BLibBlockTags {

    public static final TagKey<Block> SHOULD_NOT_BE_DESTROYED = create("should_not_be_destroyed");

    private static TagKey<Block> create(String path) {
        return BLib.MOD.resources().createTagKey(Registries.BLOCK, path);
    }
}
