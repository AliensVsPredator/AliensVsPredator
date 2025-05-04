package com.avp.common.entity.living.villager.gift;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class AVPVillagerGiftKeys {

    public static final ResourceKey<LootTable> COMMISSARY_GIFT_LOOT_TABLE = create("gameplay/hero_of_the_village/commissary_gift");

    private static @NotNull ResourceKey<LootTable> create(String path) {
        return ResourceKey.create(
            Registries.LOOT_TABLE,
            AVPResources.location(path)
        );
    }
}
