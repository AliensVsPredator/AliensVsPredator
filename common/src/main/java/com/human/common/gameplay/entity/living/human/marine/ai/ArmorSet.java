package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.core.functional.option.Option;
import net.minecraft.world.item.ItemStack;

public record ArmorSet(
    Option<ItemStack> head,
    Option<ItemStack> chest,
    Option<ItemStack> legs,
    Option<ItemStack> feet
) {}
