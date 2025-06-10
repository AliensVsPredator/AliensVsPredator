package com.avp.common.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

import com.avp.AVPResources;

public class AVPEnchantmentTags {

    public static final TagKey<Enchantment> GUN_ENCHANTMENTS = create("gun_enchantments");

    private static TagKey<Enchantment> create(String id) {
        return TagKey.create(Registries.ENCHANTMENT, AVPResources.location(id));
    }
}
