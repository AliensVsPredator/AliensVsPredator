package com.avp.common.profession;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;

import com.avp.common.item.AVPItems;

public class CommissaryGiftLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.SMALL_BULLET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .setWeight(20)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.MEDIUM_BULLET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .setWeight(20)
                )
        );
}
