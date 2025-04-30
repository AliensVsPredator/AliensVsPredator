package com.avp.fabric.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

import com.avp.fabric.AVPResources;

public class AVPEnchantmentTags {

    public static final TagKey<Enchantment> GUN_ENCHANTMENTS = create("gun_enchantments");

    private static TagKey<Enchantment> create(String name) {
        return TagKey.create(Registries.ENCHANTMENT, AVPResources.location(name));
    }
}
