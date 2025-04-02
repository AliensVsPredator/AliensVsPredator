package com.avp.common.entity.living.human.marine;

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

import com.avp.common.item.AVPItems;

public class MarineLootTable {

    // TODO: Update with changes when Pheonix has them
    public static final Function<HolderLookup.Provider, LootTable.Builder> LOOT_TABLE = provider -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(AVPItems.STEEL_NUGGET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(68)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.BRASS_NUGGET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(58)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.LEAD_NUGGET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(48)
                )
                .add(
                    LootItem.lootTableItem(Items.RABBIT_STEW)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(27)
                )
                .add(
                    LootItem.lootTableItem(Items.COOKED_BEEF)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(23)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.STEEL_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(20)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.BRASS_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(19)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.LEAD_INGOT)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0, 1)))
                        .setWeight(18)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.BARREL)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.GRIP)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.RECEIVER)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.STOCK)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
                .add(
                    LootItem.lootTableItem(AVPItems.AMMO_CHEST)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1)
                )
        );

    private MarineLootTable() {
        throw new UnsupportedOperationException();
    }
}
