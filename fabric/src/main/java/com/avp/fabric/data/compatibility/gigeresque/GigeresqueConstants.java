package com.avp.fabric.data.compatibility.gigeresque;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GigeresqueConstants {

    public static final String MOD_ID = "gigeresque";

    private static final String ACID_RESISTANT = "acid_resistant";

    public static final TagKey<Block> ACID_RESISTANT_BLOCKS = createBlock("acid_resistant");

    private static TagKey<Block> createBlock(String name) {
        return TagKey.create(Registries.BLOCK, location(name));
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final ResourceLocation ACID_RESISTANT_BLOCK_TAG = ResourceLocation.fromNamespaceAndPath(MOD_ID, ACID_RESISTANT);

    public static final ResourceLocation ACID_RESISTANT_ENTITY_TYPE_TAG = ResourceLocation.fromNamespaceAndPath(MOD_ID, ACID_RESISTANT);
}
