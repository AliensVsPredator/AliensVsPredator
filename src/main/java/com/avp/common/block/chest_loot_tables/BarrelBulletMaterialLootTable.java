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

public class BarrelBulletMaterialLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(AVPItems.BULLET_TIP)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(50)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(Items.GUNPOWDER)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(40)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(AVPItems.POLYMER)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(35)
                            )
            )
            .withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(AVPItems.HEAVY_CASING)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 10)))
                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                                            .setWeight(30)
                            )
            );
}
