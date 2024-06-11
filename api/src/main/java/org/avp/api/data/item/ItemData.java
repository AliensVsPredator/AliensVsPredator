package org.avp.api.data.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Set;

public record ItemData(
    String registryName,
    ItemModelData itemModelData,
    Set<TagKey<Item>> itemTagData
) {

    public ItemData withRegistryName(String registryName) {
        return new ItemData(registryName, itemModelData, itemTagData);
    }

    public ItemData withPrefixRegistryName(String prefix) {
        return new ItemData(prefix + registryName, itemModelData, itemTagData);
    }

    public ItemData withSuffixRegistryName(String suffix) {
        return new ItemData(registryName + suffix, itemModelData, itemTagData);
    }
}
