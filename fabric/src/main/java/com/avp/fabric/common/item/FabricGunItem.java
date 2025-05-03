package com.avp.fabric.common.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import com.avp.common.item.AVPEnchantmentTags;
import com.avp.common.item.GunItem;
import com.avp.common.item.gun.GunConfig;

public class FabricGunItem extends GunItem {

    public FabricGunItem(GunConfig gunConfig) {
        super(gunConfig);
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS);
    }
}
