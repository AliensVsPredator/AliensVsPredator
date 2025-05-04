package com.avp.common.entity.living.villager.gift;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Function;

import com.avp.common.item.AVPItems;

public class CommissaryGiftLootTable {

    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.SMALL_BULLET.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(2)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.MEDIUM_BULLET.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
        );
}
