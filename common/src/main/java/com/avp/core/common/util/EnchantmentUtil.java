package com.avp.core.common.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class EnchantmentUtil {

    public static int getLevel(Level level, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        var reference = level.registryAccess()
            .registry(Registries.ENCHANTMENT)
            .orElseThrow()
            .getHolderOrThrow(enchantment);

        return itemStack.getEnchantments().getLevel(reference);
    }
}
