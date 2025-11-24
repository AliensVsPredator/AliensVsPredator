package com.avp.common.registry.tag;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AVPEntityTypeTags {

    public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");

    public static final TagKey<EntityType<?>> NETHER_CREATURES = create("nether_creatures");

    public static final TagKey<EntityType<?>> RADIATION_RESISTANT = create("radiation_resistant");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(name));
    }
}
