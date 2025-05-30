package com.avp.neoforge.common.item;

import com.human.common.gameplay.item.GunItem;
import com.human.common.gameplay.item.gun.GunConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.tag.AVPEnchantmentTags;

public class NeoForgeGunItem extends GunItem implements IItemExtension {

    public NeoForgeGunItem(GunConfig gunConfig) {
        super(gunConfig);
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS);
    }
}
