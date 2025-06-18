package com.avp.fabric.data.loot;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.data.loot.DroneLootTable;
import com.alien.common.data.loot.PraetorianLootTable;
import com.alien.common.data.loot.QueenLootTable;
import com.alien.common.data.loot.WarriorLootTable;
import com.alien.common.registry.init.AlienEntityTypes;
import com.predator.common.gameplay.entity.living.yautja.YautjaLootTable;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider provider;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.provider = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        // Normal
        biConsumer.accept(AlienEntityTypes.BOILER.get().getDefaultLootTable(), WarriorLootTable.create(provider, AlienVariantTypes.NORMAL));
        biConsumer.accept(AlienEntityTypes.DRONE.get().getDefaultLootTable(), DroneLootTable.create(provider, AlienVariantTypes.NORMAL));
        biConsumer.accept(
            AlienEntityTypes.WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.NORMAL)
        );
        biConsumer.accept(
            AlienEntityTypes.PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AlienVariantTypes.NORMAL)
        );
        biConsumer.accept(
            AlienEntityTypes.PROWLER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.NORMAL)
        );
        biConsumer.accept(
            AlienEntityTypes.QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AlienVariantTypes.NORMAL)
        );
        biConsumer.accept(AlienEntityTypes.RUNNER.get().getDefaultLootTable(), DroneLootTable.create(provider, AlienVariantTypes.NORMAL));

        // Nether
        biConsumer.accept(
            AlienEntityTypes.NETHER_BOILER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_PROWLER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AlienVariantTypes.NETHER)
        );
        biConsumer.accept(
            AlienEntityTypes.NETHER_RUNNER.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.NETHER)
        );

        // Aberrant
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_BOILER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_PROWLER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AlienVariantTypes.ABERRANT)
        );
        biConsumer.accept(
            AlienEntityTypes.ABERRANT_RUNNER.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.ABERRANT)
        );

        // Irradiated
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_DRONE.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.IRRADIATED)
        );
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_WARRIOR.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.IRRADIATED)
        );
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_PRAETORIAN.get().getDefaultLootTable(),
            PraetorianLootTable.create(provider, AlienVariantTypes.IRRADIATED)
        );
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_PROWLER.get().getDefaultLootTable(),
            WarriorLootTable.create(provider, AlienVariantTypes.IRRADIATED)
        );
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_QUEEN.get().getDefaultLootTable(),
            QueenLootTable.createLootTableBuilder(provider, AlienVariantTypes.IRRADIATED)
        );
        biConsumer.accept(
            AlienEntityTypes.IRRADIATED_RUNNER.get().getDefaultLootTable(),
            DroneLootTable.create(provider, AlienVariantTypes.IRRADIATED)
        );

        biConsumer.accept(PredatorEntityTypes.YAUTJA.get().getDefaultLootTable(), YautjaLootTable.LOOT_TABLE.apply(provider));
    }
}
