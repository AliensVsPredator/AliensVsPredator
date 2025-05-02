package com.avp.fabric.common.block.chest_loot_tables;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

import com.avp.common.item.TempAVPItems;

public class ChestRawMaterialLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.RAW_ZINC.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .setWeight(50)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.COPPER_ORE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .setWeight(50)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(Items.RAW_COPPER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.RAW_ZINC.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.RAW_BRASS.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))
                        .setWeight(25)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .setWeight(5)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .setWeight(5)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(TempAVPItems.RAW_MONAZITE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                        .setWeight(5)
                )
        );
}
