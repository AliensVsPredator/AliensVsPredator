package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

import com.avp.common.item.AVPEnchantmentTags;

public class AVPEnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {

    public AVPEnchantmentTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(AVPEnchantmentTags.GUN_ENCHANTMENTS)
            .add(
                Enchantments.FLAME,
                Enchantments.INFINITY,
                Enchantments.MENDING,
                Enchantments.MULTISHOT,
                Enchantments.PIERCING,
                Enchantments.POWER,
                Enchantments.PUNCH,
                Enchantments.QUICK_CHARGE,
                Enchantments.UNBREAKING,
                Enchantments.VANISHING_CURSE
            );
    }
}
