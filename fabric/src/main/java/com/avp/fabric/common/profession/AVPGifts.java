package com.avp.fabric.common.profession;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import com.avp.AVPResources;

public class AVPGifts {

    public static final ResourceKey<LootTable> COMMISSARY_GIFT_LOOT_TABLE = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("gameplay/hero_of_the_village/commissary_gift")
    );
}
