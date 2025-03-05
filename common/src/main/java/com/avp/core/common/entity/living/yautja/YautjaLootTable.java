package com.avp.core.common.entity.living.yautja;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;

public class YautjaLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(ArmorItems.JUNGLE_PREDATOR_HELMET.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(ArmorItems.JUNGLE_PREDATOR_LEGGINGS.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(ArmorItems.JUNGLE_PREDATOR_BOOTS.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_AXE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_HOE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_PICKAXE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_SHOVEL.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(
                    LootItem.lootTableItem(AVPItems.VERITANIUM_SWORD.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
        );

    private YautjaLootTable() {
        throw new UnsupportedOperationException();
    }
}
