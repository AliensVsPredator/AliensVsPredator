package org.avp.common.data.loot_table.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.avp.api.Holder;
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
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.INSTANCE.boiler);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.INSTANCE.drone);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.INSTANCE.praetorian);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.INSTANCE.spitter);
        addStandardXenomorphLootTable(AVPBaseAlienEntityTypes.INSTANCE.warrior);

        // Runner line
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.INSTANCE.crusher);
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner);
        addStandardXenomorphLootTable(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner);

        // Yautja
        addYautjaLootTable();
    }

    private void addStandardXenomorphLootTable(Holder<? extends EntityType<?>> entityTypeHolder) {
        // Queen
        add(
            entityTypeHolder.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.xenomorphChitin.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 3.0F)))
                        )
                )
        );
    }

    private void addQueenLootTable() {
        add(
            AVPBaseAlienEntityTypes.INSTANCE.queen.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.royalJelly.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.xenomorphChitin.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(2.0F, 4.0F)))
                        )
                )
        );
    }

    private void addYautjaLootTable() {
        add(
            AVPYautjaEntityTypes.INSTANCE.YAUTJA.get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.veritaniumShard.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 3.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(EmptyLootItem.emptyItem().setWeight(36))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumHelmet.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumBody.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumLeggings.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumBoots.get()).setWeight(1))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.laserMine.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.shuriken.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 4.0F)))
                        )
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.smartDisc.get()))
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.yautjaArtifact.get()))
                )
        );
    }
}
