package com.avp;

import com.avp.common.sound.AVPJukeboxSongs;
import com.avp.mixin.StructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import mod.azure.azurelib.common.api.common.config.Config;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.config.ConfigHolder;
import mod.azure.azurelib.common.internal.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.intellij.lang.annotations.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.CompostingChanceRegistry;
import com.avp.common.block.DecoratedPotPatterns;
import com.avp.common.block.DispenserBlockBehaviors;
import com.avp.common.block.FlammableBlockRegistry;
import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.command.Commands;
import com.avp.common.component.DataComponents;
import com.avp.common.config.*;
import com.avp.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.common.effect.AVPEffects;
import com.avp.common.entity.spawn.SpawnPlacements;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.fuel.AVPFuelRegistry;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;
import com.avp.common.level.gameevent.AVPGameEvents;
import com.avp.common.lifecycle.Infections;
import com.avp.common.lifecycle.Lifecycles;
import com.avp.common.menu.MenuTypes;
import com.avp.common.network.CommonPacketRegistry;
import com.avp.common.network.ServerPacketHandlerRegistry;
import com.avp.common.particle.AVPParticleTypes;
import com.avp.common.patrols.MarinePatrolSpawner;
import com.avp.common.profession.AVPProfessions;
import com.avp.common.profession.AVPTrades;
import com.avp.common.recipe.AVPRecipes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.worldgen.NukedAshPlacement;
import com.avp.common.worldgen.WorldGen;
import com.avp.common.worldgen.biome.AVPBiomes;
import com.avp.data.loot.LootTableModifier;

import java.util.ArrayList;
import java.util.List;

public class AVP implements ModInitializer {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AVPConfig config;

    private final MarinePatrolSpawner customSpawner = new MarinePatrolSpawner();

    private final NukedAshPlacement nukedAshPlacement = new NukedAshPlacement();

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
            Registries.PROCESSOR_LIST, ResourceLocation.withDefaultNamespace("empty"));

    @Override
    public void onInitialize() {
        AzureLib.initialize();
        config = registerConfig(AVPConfig.class, ConfigFormats.json()).getConfigInstance();

        // Core
        BlockEntityTypes.initialize();
        AVPBlocks.initialize();
        AVPItems.initialize();
        AVPBlockItems.initialize();
        ArmorItems.initialize();
        SpawnEggItems.initialize();
        AVPEntityTypes.initialize();
        Infections.initialize();
        Lifecycles.initialize();
        AVPParticleTypes.initialize();
        MenuTypes.initialize();
        DataComponents.initialize();
        DecoratedPotPatterns.initialize();
        WorldGen.initialize();
        AVPSoundEvents.initialize();
        AVPJukeboxSongs.initialize();
        AVPGameEvents.initialize();
        CommonPacketRegistry.initialize();
        ServerPacketHandlerRegistry.initialize();
        AVPRecipes.initialize();
        AVPEffects.initialize();
        AVPBiomes.initialize();
        AVPProfessions.initialize();

        // Creative Tabs
        BlocksCreativeModeTabInitializer.initialize();
        ColoredBlocksCreativeModeTabInitializer.initialize();
        CombatCreativeModeTabInitializer.initialize();
        IngredientsCreativeModeTabInitializer.initialize();
        SpawnEggsCreativeModeTabInitializer.initialize();
        ToolsAndUtilitiesCreativeModeTabInitializer.initialize();

        // Functionality
        CompostingChanceRegistry.initialize();
        DispenserBlockBehaviors.initialize();
        LootTableModifier.initialize();
        SpawnPlacements.initialize();
        FlammableBlockRegistry.initialize();
        AVPFuelRegistry.initialize();
        Commands.initialize();
        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
        ServerLifecycleEvents.SERVER_STARTING.register(this::addNewVillageBuilding);
        AVPTrades.initialize();
    }

    private void onWorldTick(ServerLevel serverLevel) {
        customSpawner.tick(serverLevel, serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING), true);
        nukedAshPlacement.tick(serverLevel);
    }

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param configClass   Your config class
     * @param formatFactory File format to be used by this config class. You can use values from {@link ConfigFormats}
     *                      for example.
     * @param <C>           Config type
     * @return Config holder containing your config instance. You obtain it by calling
     *         {@link ConfigHolder#getConfigInstance()} method.
     */
    public static <C> ConfigHolder<C> registerConfig(Class<C> configClass, IConfigFormatHandler formatFactory) {
        var config = configClass.getAnnotation(Config.class);

        if (config == null) {
            throw new IllegalArgumentException("Config class must be annotated with '@Config' annotation");
        }

        var id = config.id();
        var filename = config.filename();

        if (filename.isEmpty()) {
            filename = id;
        }

        var group = config.group();

        if (group.isEmpty()) {
            group = id;
        }

        var holder = new ConfigHolder<>(configClass, id, filename, group, formatFactory);
        ConfigHolderRegistry.registerConfig(holder);

        if (configClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }

        return holder;
    }

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                          Registry<StructureProcessorList> processorListRegistry,
                                          ResourceLocation poolRL,
                                          String nbtPieceRL,
                                          int weight) {
        if (processorListRegistry.getHolder(EMPTY_PROCESSOR_LIST_KEY).isEmpty()) {
            return;
        }

        var emptyProcessorList = processorListRegistry.getHolder(EMPTY_PROCESSOR_LIST_KEY).get();
        var pool = templatePoolRegistry.get(poolRL);

        if (pool == null) {
            return;
        }

        var piece = StructurePoolElement.legacy(nbtPieceRL, emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        for (var i = 0; i < weight; i++) {
            ((StructurePoolAccessor)pool).getElements().add(piece);
        }

        var listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor)pool).getElementCounts());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor)pool).setElementCounts(listOfPieceEntries);
    }

    public void addNewVillageBuilding(final MinecraftServer event) {
        var templatePoolRegistry = event.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var processorListRegistry = event.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/plains/houses"),
                "avp:village/plains/houses/plains_commissary", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/snowy/houses"),
                "avp:village/snowy/houses/snowy_commissary", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/savanna/houses"),
                "avp:village/savanna/houses/savanna_commissary", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/taiga/houses"),
                "avp:village/taiga/houses/taiga_commissary", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/desert/houses"),
                "avp:village/desert/houses/desert_commissary", 5);
    }
}
