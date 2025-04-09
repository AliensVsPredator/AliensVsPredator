package com.avp.common.block.chest_loot_tables;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

import com.avp.common.item.AVPItems;

public class ChestRawMaterialLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_ZINC)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(50)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.COPPER_ORE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(50)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.RAW_COPPER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_ZINC)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_BRASS)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(25)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_MONAZITE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(5)
                )
        );
}
