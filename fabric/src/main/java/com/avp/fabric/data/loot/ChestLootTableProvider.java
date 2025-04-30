package com.avp.fabric.data.loot;

import com.avp.fabric.common.block.chest_loot_tables.AVPLootTables;
import com.avp.fabric.common.block.chest_loot_tables.AmmoChestBulletsLootTable;
import com.avp.fabric.common.block.chest_loot_tables.BarrelBulletMaterialLootTable;
import com.avp.fabric.common.block.chest_loot_tables.BarrelCasingsLootTable;
import com.avp.fabric.common.block.chest_loot_tables.ChestRawMaterialLootTable;
import com.avp.fabric.common.block.chest_loot_tables.CommanderChestPersonalLootTable;
import com.avp.fabric.common.block.chest_loot_tables.MarineChestPersonalLootTable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ChestLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public ChestLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.CHEST);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(AVPLootTables.BARREL_CASINGS, BarrelCasingsLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTables.BARREL_BULLET_MATERIAL, BarrelBulletMaterialLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTables.AMMO_CHEST_BULLETS, AmmoChestBulletsLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTables.CHEST_RAW_MATERIAL, ChestRawMaterialLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTables.MARINE_CHEST_PERSONAL, MarineChestPersonalLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTables.COMMANDER_CHEST_PERSONAL, CommanderChestPersonalLootTable.LOOT_TABLE.apply(provider));
    }
}
