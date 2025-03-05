package com.avp.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.avp.common.entity.living.alien.xenomorph.drone.DroneLootTable;
import com.avp.common.entity.living.alien.xenomorph.praetorian.PraetorianLootTable;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenLootTable;
import com.avp.common.entity.living.alien.xenomorph.warrior.WarriorLootTable;
import com.avp.common.entity.living.yautja.YautjaLootTable;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(AVPEntityTypes.DRONE.getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.CHITIN));
        biConsumer.accept(AVPEntityTypes.NETHER_DRONE.getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.NETHER_CHITIN));
        biConsumer.accept(AVPEntityTypes.WARRIOR.getDefaultLootTable(), WarriorLootTable.create(provider, AVPItems.CHITIN));
        biConsumer.accept(AVPEntityTypes.NETHER_WARRIOR.getDefaultLootTable(), WarriorLootTable.create(provider, AVPItems.NETHER_CHITIN));
        biConsumer.accept(
            AVPEntityTypes.PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.CHITIN, AVPItems.PLATED_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.NETHER_CHITIN, AVPItems.PLATED_NETHER_CHITIN)
        );
        biConsumer.accept(AVPEntityTypes.QUEEN.getDefaultLootTable(), QueenLootTable.LOOT_TABLE.apply(provider));
        biConsumer.accept(AVPEntityTypes.YAUTJA.getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
