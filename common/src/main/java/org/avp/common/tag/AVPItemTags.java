package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.avp.common.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> ACID_RESISTANT = create("acid_resistant");

    public static final TagKey<Item> GUNS = create("guns");

    public static final TagKey<Item> THREATENS_PREDATORS = create("threatens_predators");

    private static TagKey<Item> create(String registryName) {
        return TagKey.create(Registries.ITEM, AVPResources.location(registryName));
    }

    private AVPItemTags() {
        throw new UnsupportedOperationException();
    }
}
