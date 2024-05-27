package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.avp.common.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<Item> CONCRETE = create("concrete");

    public static final TagKey<Item> GUNS = create("guns");

    public static final TagKey<Item> HEAVY_GUNS = create("heavy_guns");

    public static final TagKey<Item> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Item> MEDIUM_GUNS = create("medium_guns");

    public static final TagKey<Item> SMALL_GUNS = create("small_guns");

    public static final TagKey<Item> THREATENS_PREDATORS = create("threatens_predators");

    public static final TagKey<Item> UBER_GUNS = create("uber_guns");

    private static TagKey<Item> create(String registryName) {
        return TagKey.create(Registries.ITEM, AVPResources.location(registryName));
    }

    private AVPItemTags() {
        throw new UnsupportedOperationException();
    }
}
