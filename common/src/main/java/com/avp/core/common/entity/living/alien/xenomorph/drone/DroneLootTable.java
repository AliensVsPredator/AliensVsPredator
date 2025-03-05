package com.avp.core.common.entity.living.alien.xenomorph.drone;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DroneLootTable {

    public static LootTable.Builder create(HolderLookup.Provider provider, Item item) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(
                        LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                    )
            );
    }

    private DroneLootTable() {
        throw new UnsupportedOperationException();
    }
}
