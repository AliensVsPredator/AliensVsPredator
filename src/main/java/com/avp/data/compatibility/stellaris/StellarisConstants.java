package com.avp.data.compatibility.stellaris;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class StellarisConstants {

    public static final String MOD_ID = "stellaris";

    public static final TagKey<EntityType<?>> NO_OXYGEN_NEEDED = create("no_oxygen_needed");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, location(name));
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
