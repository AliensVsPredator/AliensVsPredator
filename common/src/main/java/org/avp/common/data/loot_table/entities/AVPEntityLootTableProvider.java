package org.avp.common.data.loot_table.entities;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.avp.common.data.loot_table.AbstractAVPEntityLootTableProvider;
import org.avp.common.entity.AVPBaseAlienEntityTypes;
import org.avp.common.item.AVPItems;

public class AVPEntityLootTableProvider extends AbstractAVPEntityLootTableProvider {

    @Override
    public void generate() {
        add(
            AVPBaseAlienEntityTypes.QUEEN.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.ROYAL_JELLY.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                )
        );
    }
}
