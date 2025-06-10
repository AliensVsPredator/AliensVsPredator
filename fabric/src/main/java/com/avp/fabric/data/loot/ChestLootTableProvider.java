package com.avp.fabric.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.avp.common.data.loot.AmmoChestBulletsLootTable;
import com.avp.common.data.loot.BarrelBulletMaterialLootTable;
import com.avp.common.data.loot.BarrelCasingsLootTable;
import com.avp.common.data.loot.ChestRawMaterialLootTable;
import com.avp.common.data.loot.CommanderChestPersonalLootTable;
import com.avp.common.data.loot.MarineChestPersonalLootTable;
import com.avp.common.registry.key.AVPLootTableKeys;

public class ChestLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public ChestLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.CHEST);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(AVPLootTableKeys.BARREL_CASINGS, BarrelCasingsLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTableKeys.BARREL_BULLET_MATERIAL, BarrelBulletMaterialLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTableKeys.AMMO_CHEST_BULLETS, AmmoChestBulletsLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTableKeys.CHEST_RAW_MATERIAL, ChestRawMaterialLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTableKeys.MARINE_CHEST_PERSONAL, MarineChestPersonalLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPLootTableKeys.COMMANDER_CHEST_PERSONAL, CommanderChestPersonalLootTable.LOOT_TABLE.apply(provider));
    }
}
