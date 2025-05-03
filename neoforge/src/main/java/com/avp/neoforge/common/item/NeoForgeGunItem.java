package com.avp.neoforge.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;

import com.avp.common.item.AVPEnchantmentTags;
import com.avp.common.item.GunItem;
import com.avp.common.item.gun.GunConfig;

public class NeoForgeGunItem extends GunItem implements IItemExtension {

    public NeoForgeGunItem(GunConfig gunConfig) {
        super(gunConfig);
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS);
    }
}
