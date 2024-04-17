package org.avp.fabric.common.data;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import org.avp.api.GameObject;
import org.avp.common.item.AVPWeaponBlueprintItems;

public class AVPFabricLootTableModifier {

    public static void registerVanillaLootTableModifications() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                addSimpleLootPool(
                    BuiltInLootTables.VILLAGE_WEAPONSMITH,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M88MOD4_COMBAT_PISTOL,
                    50
                );
                addSimpleLootPool(BuiltInLootTables.SIMPLE_DUNGEON, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_37_12_SHOTGUN, 20);
                addSimpleLootPool(BuiltInLootTables.DESERT_PYRAMID, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_F90_RIFLE, 20);
                addSimpleLootPool(BuiltInLootTables.JUNGLE_TEMPLE, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M4_CARBINE, 20);
                addSimpleLootPool(BuiltInLootTables.PILLAGER_OUTPOST, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_SNIPER_RIFLE, 20);
                addSimpleLootPool(
                    BuiltInLootTables.STRONGHOLD_CORRIDOR,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M41A_PULSE_RIFLE,
                    20
                );
                addSimpleLootPool(
                    BuiltInLootTables.BASTION_TREASURE,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_FLAMETHROWER_SEVASTOPOL,
                    33
                );
                addSimpleLootPool(BuiltInLootTables.WOODLAND_MANSION, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_OLD_PAINLESS, 20);
                addSimpleLootPool(BuiltInLootTables.ANCIENT_CITY, id, tableBuilder, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M83A2_SADAR, 20);
                addSimpleLootPool(
                    BuiltInLootTables.END_CITY_TREASURE,
                    id,
                    tableBuilder,
                    AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M56_SMARTGUN,
                    20
                );

                // AK-47
                if (BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.equals(id)) {
                    var poolBuilder = LootPool.lootPool()
                        .add(
                            LootItem.lootTableItem(AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_AK_47.get())
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
        GameObject<Item> itemGameObject,
        int weight
    ) {
        if (!target.equals(id)) {
            return;
        }

        var poolBuilder = LootPool.lootPool()
            .add(EmptyLootItem.emptyItem().setWeight(100 - weight))
            .add(
                LootItem.lootTableItem(itemGameObject.get())
                    .setWeight(weight)
            )
            .build();
        tableBuilder.pool(poolBuilder);
    }

    private AVPFabricLootTableModifier() {
        throw new UnsupportedOperationException();
    }
}
