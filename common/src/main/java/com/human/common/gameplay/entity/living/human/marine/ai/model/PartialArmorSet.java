package com.human.common.gameplay.entity.living.human.marine.ai.model;

import com.just.core.functional.option.Option;
import net.minecraft.world.item.ItemStack;

public record PartialArmorSet(
    Option<ItemStack> head,
    Option<ItemStack> chest,
    Option<ItemStack> legs,
    Option<ItemStack> feet
) {

    public boolean isEmpty() {
        return head.isNone()
            && chest.isNone()
            && legs.isNone()
            && feet.isNone();
    }
}
