package com.avp.common.data.loot;

import com.avp.common.registry.init.item.AVPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

public class ChestRawMaterialLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_ZINC.get())
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
                    LootItem.lootTableItem(AVPItems.RAW_ZINC.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                        .setWeight(40)
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_BRASS.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))
                        .setWeight(25)
                )
        )
        // FIXME:
//        .withPool(
//            LootPool.lootPool()
//                .setRolls(ConstantValue.exactly(1))
//                .add(
//                    LootItem.lootTableItem(AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT.get())
//                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
//                        .setWeight(5)
//                )
//        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.RAW_MONAZITE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                        .setWeight(5)
                )
        );
}
