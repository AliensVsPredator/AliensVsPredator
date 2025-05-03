package com.avp.neoforge.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;

import com.avp.common.item.AVPEnchantmentTags;
import com.avp.common.item.old_painless.OldPainlessItem;

public class NeoForgeOldPainlessItem extends OldPainlessItem implements IItemExtension {

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS);
    }
}
