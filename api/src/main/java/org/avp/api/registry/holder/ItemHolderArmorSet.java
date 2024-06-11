package org.avp.api.registry.holder;

import net.minecraft.world.item.Item;

public record ItemHolderArmorSet(
    BLHolder<Item> helmet,
    BLHolder<Item> body,
    BLHolder<Item> leggings,
    BLHolder<Item> boots
) {}
