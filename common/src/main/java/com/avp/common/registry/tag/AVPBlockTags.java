package com.avp.common.registry.tag;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AVPBlockTags {

    public static final TagKey<Block> SHOULD_NOT_BE_DESTROYED = create("should_not_be_destroyed");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, AVPResources.location(name));
    }
}
