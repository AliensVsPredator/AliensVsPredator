package com.avp.common.entity.living.alien.xenomorph.queen;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import com.avp.common.entity.living.alien.AlienVariantType;
import com.avp.common.item.AVPItems;

public class QueenLootTable {

    public static LootTable.Builder createLootTableBuilder(HolderLookup.Provider provider, AlienVariantType alienVariantType) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(
                        LootItem.lootTableItem(AVPItems.RAW_ROYAL_JELLY.get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(
                        LootItem.lootTableItem(alienVariantType.chitin().get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(
                        LootItem.lootTableItem(alienVariantType.platedChitin().get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                    )
            );
    }

    private QueenLootTable() {
        throw new UnsupportedOperationException();
    }
}
