package com.avp.common.profession;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class AVPGifts {

    public static final ResourceKey<LootTable> COMMISAARY_GIFT_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            AVPResources.location("gameplay/hero_of_the_village/commisaary_gift")
    );
}
