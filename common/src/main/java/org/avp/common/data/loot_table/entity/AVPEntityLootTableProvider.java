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
import org.avp.common.entity.data.BoilerData;
import org.avp.common.entity.data.CrusherData;
import org.avp.common.entity.data.DroneData;
import org.avp.common.entity.data.DroneRunnerData;
import org.avp.common.entity.data.NauticomorphData;
import org.avp.common.entity.data.PraetorianData;
import org.avp.common.entity.data.QueenData;
import org.avp.common.entity.data.SpitterData;
import org.avp.common.entity.data.WarriorData;
import org.avp.common.entity.data.WarriorRunnerData;
import org.avp.common.entity.data.YautjaData;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPItems;

public class AVPEntityLootTableProvider extends AbstractAVPEntityLootTableProvider {

    @Override
    public void generate() {
        // Base line
        addQueenLootTable();
        addStandardXenomorphLootTable(BoilerData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(DroneData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(NauticomorphData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(PraetorianData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(SpitterData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(WarriorData.INSTANCE.getHolder());

        // Runner line
        addStandardXenomorphLootTable(CrusherData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(DroneRunnerData.INSTANCE.getHolder());
        addStandardXenomorphLootTable(WarriorRunnerData.INSTANCE.getHolder());

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
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.xenomorphChitin.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 3)))
                        )
                )
        );
    }

    private void addQueenLootTable() {
        add(
            QueenData.INSTANCE.getHolder().get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.royalJelly.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.xenomorphChitin.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(2, 4)))
                        )
                )
        );
    }

    private void addYautjaLootTable() {
        add(
            YautjaData.INSTANCE.getHolder().get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.veritaniumShard.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 3)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(EmptyLootItem.emptyItem().setWeight(36))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumHelmet.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumBody.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumLeggings.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritaniumBoots.get()).setWeight(1))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.laserMine.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.shuriken.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.smartDisc.get()))
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.yautjaArtifact.get()))
                )
        );
    }
}
