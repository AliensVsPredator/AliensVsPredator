package com.avp.common.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

public class MarineChestPersonalLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.COOKED_BEEF)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                        .setWeight(45)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.COOKED_MUTTON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.BOWL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .setWeight(35)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.BOOK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                        .setWeight(30)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.TORCH)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 12)))
                        .setWeight(20)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.MAP)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(2)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.EMERALD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                        .setWeight(10)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    EmptyLootItem.emptyItem().setWeight(1)
                )
            // FIXME:
            // .add(
            // LootItem.lootTableItem(AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT.get())
            // .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
            // .setWeight(1)
            // )
        );
}
