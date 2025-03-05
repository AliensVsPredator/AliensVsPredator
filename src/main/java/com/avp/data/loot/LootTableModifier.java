package com.avp.data.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.List;

import com.avp.common.item.AVPItems;

public class LootTableModifier {

    private static final List<ResourceKey<LootTable>> VILLAGE_LOOT_TABLE_RESOURCE_KEYS = List.of(
        BuiltInLootTables.VILLAGE_WEAPONSMITH,
        BuiltInLootTables.VILLAGE_TOOLSMITH,
        BuiltInLootTables.VILLAGE_ARMORER,
        BuiltInLootTables.VILLAGE_CARTOGRAPHER,
        BuiltInLootTables.VILLAGE_MASON,
        BuiltInLootTables.VILLAGE_SHEPHERD,
        BuiltInLootTables.VILLAGE_BUTCHER,
        BuiltInLootTables.VILLAGE_FLETCHER,
        BuiltInLootTables.VILLAGE_FISHER,
        BuiltInLootTables.VILLAGE_TANNERY,
        BuiltInLootTables.VILLAGE_TEMPLE,
        BuiltInLootTables.VILLAGE_DESERT_HOUSE,
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE
    );

    private static final List<ResourceKey<LootTable>> EARLY_GAME_LOOT_TABLE_RESOURCE_KEYS = List.of(
        BuiltInLootTables.DESERT_PYRAMID,
        BuiltInLootTables.JUNGLE_TEMPLE,
        BuiltInLootTables.PILLAGER_OUTPOST,
        BuiltInLootTables.SIMPLE_DUNGEON
    );

    private static final List<ResourceKey<LootTable>> MID_GAME_LOOT_TABLE_RESOURCE_KEYS = List.of(
        BuiltInLootTables.ANCIENT_CITY,
        BuiltInLootTables.ABANDONED_MINESHAFT,
        BuiltInLootTables.STRONGHOLD_CROSSING,
        BuiltInLootTables.STRONGHOLD_CORRIDOR,
        BuiltInLootTables.STRONGHOLD_LIBRARY,
        BuiltInLootTables.WOODLAND_MANSION
    );

    public static void initialize() {
        LootTableEvents.MODIFY.register((key, builder, source, provider) -> {
            if (!source.isBuiltin()) {
                return;
            }

            // Early-game loot
            addRareVillagePistolLootPools(key, builder);
            addEarlyGameDungeonLootPools(key, builder);

            // Mid-game loot
            addMidGameDungeonLootPools(key, builder);
            addNetherBridgeFlamethrowerLootPool(key, builder);
            addBastionTreasureGuaranteedFlamethrowerLootPool(key, builder);

            // End-game loot
            addEndCityTreasureUberLootPools(key, builder);

            // Archaeology loot
            addColdOceanRuinsSherdLoot(key, builder);
            addWarmOceanRuinsSherdLoot(key, builder);
        });
    }

    private static void addColdOceanRuinsSherdLoot(ResourceKey<LootTable> key, LootTable.Builder builder) {
        if (!key.equals(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)) {
            return;
        }

        builder.modifyPools(
            (pool) -> pool
                .add(LootItem.lootTableItem(AVPItems.OVOID_POTTERY_SHERD))
                .add(LootItem.lootTableItem(AVPItems.ROYALTY_POTTERY_SHERD))
        );
    }

    private static void addWarmOceanRuinsSherdLoot(ResourceKey<LootTable> key, LootTable.Builder builder) {
        if (!key.equals(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY)) {
            return;
        }

        builder.modifyPools(
            (pool) -> pool
                .add(LootItem.lootTableItem(AVPItems.PARASITE_POTTERY_SHERD))
                .add(LootItem.lootTableItem(AVPItems.VECTOR_POTTERY_SHERD))
        );
    }

    private static void addRareVillagePistolLootPools(ResourceKey<LootTable> key, LootTable.Builder builder) {
        var pistolWeight = 20;
        var lootPool = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(100 - pistolWeight))
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL)
                    .setWeight(pistolWeight)
            )
            .build();

        VILLAGE_LOOT_TABLE_RESOURCE_KEYS.forEach(target -> {
            if (target.equals(key)) {
                builder.pool(lootPool);
            }
        });
    }

    private static void addEarlyGameDungeonLootPools(ResourceKey<LootTable> key, LootTable.Builder builder) {
        var lootPool = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(50))
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL)
                    .setWeight(10)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M37_12_SHOTGUN)
                    .setWeight(20)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_ZX_76_SHOTGUN)
                    .setWeight(20)
            )
            .build();

        EARLY_GAME_LOOT_TABLE_RESOURCE_KEYS.forEach(target -> {
            if (target.equals(key)) {
                builder.pool(lootPool);
            }
        });
    }

    private static void addMidGameDungeonLootPools(ResourceKey<LootTable> key, LootTable.Builder builder) {
        var lootPool = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(50))
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL)
                    .setWeight(4)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M37_12_SHOTGUN)
                    .setWeight(8)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_ZX_76_SHOTGUN)
                    .setWeight(8)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_F903WE_RIFLE)
                    .setWeight(15)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE)
                    .setWeight(15)
            )
            .build();

        MID_GAME_LOOT_TABLE_RESOURCE_KEYS.forEach(target -> {
            if (target.equals(key)) {
                builder.pool(lootPool);
            }
        });
    }

    private static void addNetherBridgeFlamethrowerLootPool(ResourceKey<LootTable> key, LootTable.Builder builder) {
        if (!key.equals(BuiltInLootTables.NETHER_BRIDGE)) {
            return;
        }

        var lootPool = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(66))
            .add(LootItem.lootTableItem(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL).setWeight(33))
            .build();

        builder.pool(lootPool);
    }

    private static void addBastionTreasureGuaranteedFlamethrowerLootPool(ResourceKey<LootTable> key, LootTable.Builder builder) {
        if (!key.equals(BuiltInLootTables.BASTION_TREASURE)) {
            return;
        }

        var lootPool = LootPool.lootPool()
            .add(LootItem.lootTableItem(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL))
            .build();

        builder.pool(lootPool);
    }

    private static void addEndCityTreasureUberLootPools(ResourceKey<LootTable> key, LootTable.Builder builder) {
        if (!key.equals(BuiltInLootTables.END_CITY_TREASURE)) {
            return;
        }

        var lootPool = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(50))
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE)
                    .setWeight(15)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE)
                    .setWeight(15)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER)
                    .setWeight(10)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_M56_SMARTGUN)
                    .setWeight(5)
            )
            .add(
                LootItem.lootTableItem(AVPItems.BLUEPRINT_OLD_PAINLESS)
                    .setWeight(5)
            )
            .build();

        builder.pool(lootPool);
    }

    private LootTableModifier() {
        throw new UnsupportedOperationException();
    }
}
