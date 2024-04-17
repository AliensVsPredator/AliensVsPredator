package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.avp.common.AVPResources;

public class AVPBlockTags {

    public static final TagKey<Block> ACID_IMMUNE = create("acid_immune");

    private static TagKey<Block> create(String registryName) {
        return TagKey.create(Registries.BLOCK, AVPResources.location(registryName));
    }

    private AVPBlockTags() {
        throw new UnsupportedOperationException();
    }
}
