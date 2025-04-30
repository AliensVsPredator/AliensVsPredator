package com.avp.common.util;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface AVPInventory {

    boolean canAddItem(ItemStack itemStack);

    void forEach(Consumer<ItemStack> consumer);

    void pickUpItem(ItemEntity itemEntity);

    void removeItemType(Item item, int amount);

    Stream<ItemStack> stream();
}
