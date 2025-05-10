package com.avp.fabric.data.compatibility.gigeresque;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class GigeresqueConstants {

    public static final String MOD_ID = "gigeresque";

    private static final String ACID_RESISTANT = "acid_resistant";

    public static final TagKey<EntityType<?>> ACID_RESISTANT_ENTITIES = createEntity(ACID_RESISTANT);

    public static final TagKey<Block> ACID_RESISTANT_BLOCKS = createBlock(ACID_RESISTANT);

    private static TagKey<EntityType<?>> createEntity(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, location(name));
    }

    private static TagKey<Block> createBlock(String name) {
        return TagKey.create(Registries.BLOCK, location(name));
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
