package com.avp.fabric.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.avp.common.entity.living.yautja.YautjaLootTable;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.entity.living.alien.xenomorph.drone.DroneLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.PraetorianLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.queen.QueenLootTable;
import com.avp.fabric.common.entity.living.alien.xenomorph.warrior.WarriorLootTable;
import com.avp.fabric.common.entity.type.AVPEntityTypes;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        // Normal
        biConsumer.accept(AVPEntityTypes.DRONE.getDefaultLootTable(), DroneLootTable.create(provider, TempAVPItems.CHITIN.get()));
        biConsumer.accept(AVPEntityTypes.WARRIOR.getDefaultLootTable(), WarriorLootTable.create(provider, TempAVPItems.CHITIN.get()));
        biConsumer.accept(
            AVPEntityTypes.PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.CHITIN.get(), TempAVPItems.PLATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.CHITIN.get(), TempAVPItems.PLATED_CHITIN.get())
        );

        // Nether
        biConsumer.accept(
            AVPEntityTypes.NETHER_DRONE.getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_WARRIOR.getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get(), TempAVPItems.PLATED_NETHER_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.NETHER_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.NETHER_CHITIN.get(), TempAVPItems.PLATED_NETHER_CHITIN.get())
        );

        // Aberrant
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_DRONE.getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_WARRIOR.getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get(), TempAVPItems.PLATED_ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.ABERRANT_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.ABERRANT_CHITIN.get(), TempAVPItems.PLATED_ABERRANT_CHITIN.get())
        );

        // Irradiated
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_DRONE.getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_WARRIOR.getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_PRAETORIAN.getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get(), TempAVPItems.PLATED_IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            AVPEntityTypes.IRRADIATED_QUEEN.getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(
                provider,
                TempAVPItems.IRRADIATED_CHITIN.get(),
                TempAVPItems.PLATED_IRRADIATED_CHITIN.get()
            )
        );

        biConsumer.accept(TempAVPEntityTypes.YAUTJA.get().getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
