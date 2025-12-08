package com.blib.common.data.tag;

import com.blib.BLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class BLibEntityTypeTags {

    public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");

    public static final TagKey<EntityType<?>> NETHER_CREATURES = create("nether_creatures");

    private static TagKey<EntityType<?>> create(String path) {
        return BLib.MOD.createTagKey(Registries.ENTITY_TYPE, path);
    }
}
