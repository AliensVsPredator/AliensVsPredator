package com.avp.common.block.chest_loot_tables;

import com.avp.common.item.AVPItems;
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

public class CommanderChestPersonalLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(Items.DIAMOND)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(25)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(Items.GOLD_INGOT)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(20)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(Items.EMERALD)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(15)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL)
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(3)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE)
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(3)
                            )
            );
}
