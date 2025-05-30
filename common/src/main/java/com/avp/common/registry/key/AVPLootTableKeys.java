package com.avp.common.registry.key;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import com.avp.AVPResources;

public class AVPLootTableKeys {

    public static final ResourceKey<LootTable> BARREL_CASINGS = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/barrel_casings")
    );

    public static final ResourceKey<LootTable> BARREL_BULLET_MATERIAL = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/barrel_bullet_material")
    );

    public static final ResourceKey<LootTable> AMMO_CHEST_BULLETS = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/ammo_chest_bullets")
    );

    public static final ResourceKey<LootTable> CHEST_RAW_MATERIAL = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/chest_raw_material")
    );

    public static final ResourceKey<LootTable> MARINE_CHEST_PERSONAL = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/marine_chest_personal")
    );

    public static final ResourceKey<LootTable> COMMANDER_CHEST_PERSONAL = ResourceKey.create(
        Registries.LOOT_TABLE,
        AVPResources.location("chests/commander_chest_personal")
    );
}
