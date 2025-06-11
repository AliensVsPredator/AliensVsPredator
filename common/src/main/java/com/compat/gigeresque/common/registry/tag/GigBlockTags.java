package com.compat.gigeresque.common.registry.tag;

import com.compat.gigeresque.GigResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GigBlockTags {

    public static final TagKey<Block> ACID_RESISTANT = create("acid_resistant");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, GigResources.location(name));
    }
}
