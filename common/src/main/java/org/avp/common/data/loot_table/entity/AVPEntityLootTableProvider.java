package org.avp.common.data.loot_table.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.avp.api.GameObject;
import org.avp.common.data.loot_table.AbstractAVPEntityLootTableProvider;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.type.AVPYautjaEntityTypes;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPItems;

public class AVPEntityLootTableProvider extends AbstractAVPEntityLootTableProvider {

    @Override
    public void generate() {
        // Base line
        addQueenLootTable();
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.BOILER);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.DRONE);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.PRAETORIAN);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.SPITTER);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.WARRIOR);

        // Runner line
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.CRUSHER);
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.DRONE_RUNNER);
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.WARRIOR_RUNNER);

        // Yautja
        addYautjaLootTable();
    }

    private void addStandardXenomorphLootTable(GameObject<? extends EntityType<?>> entityTypeGameObject) {
        // Queen
        add(
            entityTypeGameObject.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.XENOMORPH_CHITIN.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 3.0F)))
                        )
                )
        );
    }

    private void addQueenLootTable() {
        add(
            AVPBaseAlienEntityTypes.QUEEN.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.ROYAL_JELLY.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.XENOMORPH_CHITIN.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(2.0F, 4.0F)))
                        )
                )
        );
    }

    private void addYautjaLootTable() {
        add(
            AVPYautjaEntityTypes.YAUTJA.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.VERITANIUM_SHARD.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 3.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.VERITANIUM_HELMET.get()))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.VERITANIUM_BODY.get()))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.VERITANIUM_LEGGINGS.get()))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.VERITANIUM_BOOTS.get()))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.LASER_MINE.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.SHURIKEN.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 4.0F)))
                        )
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.SMART_DISC.get()))
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.YAUTJA_ARTIFACT.get()))
                )
        );
    }
}
