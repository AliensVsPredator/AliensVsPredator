package org.avp.fabric.common.data;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.registry.item.AVPWeaponBlueprintItemRegistry;

public class AVPFabricLootTableModifier {

    public static void registerVanillaLootTableModifications() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                addSimpleLootPool(
                    BuiltInLootTables.VILLAGE_WEAPONSMITH,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM88Mod4CombatPistol,
                    50
                );
                addSimpleLootPool(
                    BuiltInLootTables.SIMPLE_DUNGEON,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprint3712Shotgun,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.DESERT_PYRAMID,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintF90Rifle,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.JUNGLE_TEMPLE,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM4Carbine,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.PILLAGER_OUTPOST,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintSniperRifle,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.STRONGHOLD_CORRIDOR,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM41APulseRifle,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.BASTION_TREASURE,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintFlamethrowerSevastopol,
                    33
                );
                addSimpleLootPool(
                    BuiltInLootTables.WOODLAND_MANSION,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintOldPainless,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.ANCIENT_CITY,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM83A2Sadar,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.END_CITY_TREASURE,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM56Smartgun,
                    20
                );

                // AK-47
                if (BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.equals(id)) {
                    var poolBuilder = LootPool.lootPool()
                        .add(
                            LootItem.lootTableItem(AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintAk47.get())
                        )
                        .build();
                    tableBuilder.pool(poolBuilder);
                }
            }
        });
    }

    private static void addSimpleLootPool(
        ResourceLocation target,
        ResourceLocation id,
        LootTable.Builder tableBuilder,
        BLHolder<Item> itemHolder,
        int weight
    ) {
        if (!target.equals(id)) {
            return;
        }

        var poolBuilder = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(100 - weight))
            .add(
                LootItem.lootTableItem(itemHolder.get())
                    .setWeight(weight)
            )
            .build();
        tableBuilder.pool(poolBuilder);
    }

    private AVPFabricLootTableModifier() {
        throw new UnsupportedOperationException();
    }
}
