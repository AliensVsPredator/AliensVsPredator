package com.avp.fabric.data.loot;

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
        // Normal
        biConsumer.accept(AVPEntityTypes.DRONE.get().getDefaultLootTable(), DroneLootTable.create(provider, AVPItems.CHITIN.get()));
        biConsumer.accept(
            AVPEntityTypes.WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.CHITIN.get(), AVPItems.PLATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.CHITIN.get(), AVPItems.PLATED_CHITIN.get())
        );

        // Nether
        biConsumer.accept(
            AVPEntityTypes.NETHER_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.NETHER_CHITIN.get(), AVPItems.PLATED_NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.NETHER_CHITIN.get(), AVPItems.PLATED_NETHER_CHITIN.get())
        );

        // Aberrant
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.ABERRANT_CHITIN.get(), AVPItems.PLATED_ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AVPItems.ABERRANT_CHITIN.get(), AVPItems.PLATED_ABERRANT_CHITIN.get())
        );

        // Irradiated
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AVPItems.IRRADIATED_CHITIN.get(), AVPItems.PLATED_IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(
                provider,
                AVPItems.IRRADIATED_CHITIN.get(),
                AVPItems.PLATED_IRRADIATED_CHITIN.get()
            )
        );

        biConsumer.accept(AVPEntityTypes.YAUTJA.get().getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
