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
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.item.TempAVPItems;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        // Normal
        biConsumer.accept(TempAVPEntityTypes.DRONE.get().getDefaultLootTable(), DroneLootTable.create(provider, TempAVPItems.CHITIN.get()));
        biConsumer.accept(
            TempAVPEntityTypes.WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.CHITIN.get(), TempAVPItems.PLATED_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.CHITIN.get(), TempAVPItems.PLATED_CHITIN.get())
        );

        // Nether
        biConsumer.accept(
            TempAVPEntityTypes.NETHER_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.NETHER_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.NETHER_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.NETHER_CHITIN.get(), TempAVPItems.PLATED_NETHER_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.NETHER_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.NETHER_CHITIN.get(), TempAVPItems.PLATED_NETHER_CHITIN.get())
        );

        // Aberrant
        biConsumer.accept(
            TempAVPEntityTypes.ABERRANT_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.ABERRANT_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.ABERRANT_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.ABERRANT_CHITIN.get(), TempAVPItems.PLATED_ABERRANT_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.ABERRANT_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, TempAVPItems.ABERRANT_CHITIN.get(), TempAVPItems.PLATED_ABERRANT_CHITIN.get())
        );

        // Irradiated
        biConsumer.accept(
            TempAVPEntityTypes.IRRADIATED_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.IRRADIATED_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.IRRADIATED_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, TempAVPItems.IRRADIATED_CHITIN.get(), TempAVPItems.PLATED_IRRADIATED_CHITIN.get())
        );
        biConsumer.accept(
            TempAVPEntityTypes.IRRADIATED_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(
                provider,
                TempAVPItems.IRRADIATED_CHITIN.get(),
                TempAVPItems.PLATED_IRRADIATED_CHITIN.get()
            )
        );

        biConsumer.accept(TempAVPEntityTypes.YAUTJA.get().getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
