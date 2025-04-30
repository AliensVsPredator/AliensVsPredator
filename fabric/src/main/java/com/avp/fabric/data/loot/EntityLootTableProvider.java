package com.avp.fabric.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.avp.fabric.common.entity.living.alien.xenomorph.drone.DroneLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.PraetorianLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.queen.QueenLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.warrior.WarriorLootTable;
import com.avp.fabric.common.entity.living.yautja.YautjaLootTable;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        // Normal
        biConsumer.accept(AVPEntityTypes.DRONE.getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.CHITIN));
        biConsumer.accept(AVPEntityTypes.WARRIOR.getDefaultLootTable(), WarriorLootTable.create(provider, AVPItems.CHITIN));
        biConsumer.accept(
            AVPEntityTypes.PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.CHITIN, AVPItems.PLATED_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.CHITIN, AVPItems.PLATED_CHITIN)
        );

        // Nether
        biConsumer.accept(AVPEntityTypes.NETHER_DRONE.getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.NETHER_CHITIN));
        biConsumer.accept(AVPEntityTypes.NETHER_WARRIOR.getDefaultLootTable(), WarriorLootTable.create(provider, AVPItems.NETHER_CHITIN));
        biConsumer.accept(
            AVPEntityTypes.NETHER_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.NETHER_CHITIN, AVPItems.PLATED_NETHER_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.NETHER_CHITIN, AVPItems.PLATED_NETHER_CHITIN)
        );

        // Aberrant
        biConsumer.accept(AVPEntityTypes.ABERRANT_DRONE.getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.ABERRANT_CHITIN));
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_WARRIOR.getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.ABERRANT_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.ABERRANT_CHITIN, AVPItems.PLATED_ABERRANT_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.ABERRANT_CHITIN, AVPItems.PLATED_ABERRANT_CHITIN)
        );

        // Irradiated
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_DRONE.getDefaultLootTable(),
            DroneLootTable.create(provider, AVPItems.IRRADIATED_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_WARRIOR.getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.IRRADIATED_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.IRRADIATED_CHITIN, AVPItems.PLATED_IRRADIATED_CHITIN)
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.IRRADIATED_CHITIN, AVPItems.PLATED_IRRADIATED_CHITIN)
        );

        biConsumer.accept(AVPEntityTypes.YAUTJA.getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
