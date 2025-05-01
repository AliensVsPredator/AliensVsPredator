package com.avp.fabric;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;

import com.avp.AVP;
import com.avp.common.worldgen.biome.AVPBiomes;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.block.CompostingChanceRegistry;
import com.avp.fabric.common.block.DecoratedPotPatterns;
import com.avp.fabric.common.block.DispenserBlockBehaviors;
import com.avp.fabric.common.block.FlammableBlockRegistry;
import com.avp.fabric.common.block.entity.AVPBlockEntityTypes;
import com.avp.fabric.common.block_item.AVPBlockItems;
import com.avp.fabric.common.command.Commands;
import com.avp.fabric.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.fabric.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.fabric.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.fabric.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.fabric.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.fabric.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.fabric.common.effect.AVPEffects;
import com.avp.fabric.common.entity.spawn.SpawnPlacements;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.fuel.AVPFuelRegistry;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.ArmorItems;
import com.avp.fabric.common.item.SpawnEggItems;
import com.avp.fabric.common.level.gameevent.AVPGameEvents;
import com.avp.fabric.common.lifecycle.Infections;
import com.avp.fabric.common.lifecycle.Lifecycles;
import com.avp.fabric.common.menu.MenuTypes;
import com.avp.fabric.common.network.CommonPacketRegistry;
import com.avp.fabric.common.network.ServerPacketHandlerRegistry;
import com.avp.fabric.common.particle.AVPParticleTypes;
import com.avp.fabric.common.patrols.MarinePatrolSpawner;
import com.avp.fabric.common.profession.AVPGifts;
import com.avp.fabric.common.profession.AVPProfessions;
import com.avp.fabric.common.profession.AVPTrades;
import com.avp.fabric.common.recipe.AVPRecipes;
import com.avp.fabric.common.sound.AVPJukeboxSongs;
import com.avp.fabric.common.sound.AVPSoundEvents;
import com.avp.fabric.common.worldgen.NukedAshPlacement;
import com.avp.fabric.common.worldgen.WorldGen;
import com.avp.fabric.data.loot.LootTableModifier;
import com.avp.fabric.mixin.GiveGiftToHeroAccessor;
import com.avp.fabric.mixin.ParrotSoundMapAccessor;
import com.avp.fabric.mixin.StructurePoolAccessor;

public class AVPFabric implements ModInitializer {

    private final MarinePatrolSpawner customSpawner = new MarinePatrolSpawner();

    private final NukedAshPlacement nukedAshPlacement = new NukedAshPlacement();

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
        Registries.PROCESSOR_LIST,
        ResourceLocation.withDefaultNamespace("empty")
    );

    @Override
    public void onInitialize() {
        AVP.initialize();

        // Core
        AVPBlockEntityTypes.initialize();
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
        modifyGifts();
        modifyParrotSounds();
    }

    public static void modifyParrotSounds() {
        var sounds = ParrotSoundMapAccessor.getSoundMap();

        /*
         * TODO: Use Yautja sound when added
         */
        sounds.put(AVPEntityTypes.YAUTJA, SoundEvents.ALLAY_AMBIENT_WITH_ITEM);
    }

    public static void modifyGifts() {
        var gifts = GiveGiftToHeroAccessor.getGifts();

        gifts.put(AVPProfessions.COMMISSARY, AVPGifts.COMMISSARY_GIFT_LOOT_TABLE);
    }

    private static void addBuildingToPool(
        Registry<StructureTemplatePool> templatePoolRegistry,
        Registry<StructureProcessorList> processorListRegistry,
        ResourceLocation poolRL,
        String nbtPieceRL,
        int weight
    ) {
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
            ((StructurePoolAccessor) pool).getElements().add(piece);
        }

        var listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setElementCounts(listOfPieceEntries);
    }

    public void addNewVillageBuilding(final MinecraftServer event) {
        var templatePoolRegistry = event.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var processorListRegistry = event.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);

        addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/plains/houses"),
            "avp:village/plains/houses/plains_commissary",
            5
        );

        addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/snowy/houses"),
            "avp:village/snowy/houses/snowy_commissary",
            5
        );

        addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/savanna/houses"),
            "avp:village/savanna/houses/savanna_commissary",
            5
        );

        addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/taiga/houses"),
            "avp:village/taiga/houses/taiga_commissary",
            5
        );

        addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/desert/houses"),
            "avp:village/desert/houses/desert_commissary",
            5
        );
    }
}
