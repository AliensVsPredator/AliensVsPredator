package org.avp.api.common.registry.holder;

import net.minecraft.world.item.Item;

@Deprecated(forRemoval = true)
public record ItemHolderArmorSet(
    BLHolder<Item> helmet,
    BLHolder<Item> body,
    BLHolder<Item> leggings,
    BLHolder<Item> boots
) {}
