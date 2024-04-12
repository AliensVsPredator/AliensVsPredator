package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.avp.common.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> GUNS;
    public static final TagKey<Item> THREATENS_PREDATORS;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static TagKey<Item> create(String registryName) {
        return TagKey.create(Registries.ITEM, AVPResources.location(registryName));
    }

    static {
        GUNS = create("guns");
        THREATENS_PREDATORS = create("threatens_predators");
    }

    private AVPItemTags() {
        throw new UnsupportedOperationException();
    }
}
