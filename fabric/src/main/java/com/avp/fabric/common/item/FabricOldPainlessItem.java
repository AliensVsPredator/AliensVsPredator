package com.avp.fabric.common.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import com.avp.common.item.AVPEnchantmentTags;
import com.avp.common.item.old_painless.OldPainlessItem;

public class FabricOldPainlessItem extends OldPainlessItem {

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS);
    }
}
