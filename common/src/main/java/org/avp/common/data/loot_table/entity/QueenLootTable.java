package org.avp.common.data.loot_table.entity;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.avp.common.registry.item.AVPItemRegistry;

public class QueenLootTable {

    public static final LootTable.Builder LOOT_TABLE = LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItemRegistry.INSTANCE.royalJelly.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
                )
        )
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItemRegistry.INSTANCE.xenomorphChitin.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(2, 4)))
                )
        );

    private QueenLootTable() {
        throw new UnsupportedOperationException();
    }
}
