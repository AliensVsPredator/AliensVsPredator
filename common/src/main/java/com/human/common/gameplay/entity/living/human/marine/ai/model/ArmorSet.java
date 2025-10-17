package com.human.common.gameplay.entity.living.human.marine.ai.model;

import net.minecraft.world.item.ArmorItem;

import java.util.function.Supplier;

public record ArmorSet(
    Supplier<? extends ArmorItem> helmet,
    Supplier<? extends ArmorItem> chestplate,
    Supplier<? extends ArmorItem> leggings,
    Supplier<? extends ArmorItem> boots
) {}
