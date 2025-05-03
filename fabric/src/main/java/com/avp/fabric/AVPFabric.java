package com.avp.fabric;

import com.avp.data.worldgen.AVPVillageInjection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameRules;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.patrols.MarinePatrolSpawner;
import com.avp.fabric.common.block.CompostingChanceRegistry;
import com.avp.fabric.common.block.DispenserBlockBehaviors;
import com.avp.fabric.common.block.FlammableBlockRegistry;
import com.avp.fabric.common.entity.spawn.SpawnPlacements;
import com.avp.fabric.common.fuel.AVPFuelRegistry;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.network.CommonPacketRegistry;
import com.avp.fabric.common.network.ServerPacketHandlerRegistry;
import com.avp.fabric.common.profession.AVPGifts;
import com.avp.fabric.common.profession.AVPProfessions;
import com.avp.fabric.common.profession.AVPTrades;
import com.avp.fabric.common.worldgen.NukedAshPlacement;
import com.avp.fabric.common.worldgen.WorldGen;
import com.avp.fabric.data.loot.LootTableModifier;
import com.avp.fabric.service.FabricRegistryService;
import com.avp.mixin.GiveGiftToHeroAccessor;
import com.avp.mixin.ParrotSoundMapAccessor;
import com.avp.service.Services;

public class AVPFabric implements ModInitializer {

    private static final FabricRegistryService REGISTRY = (FabricRegistryService) Services.REGISTRY;

    private final MarinePatrolSpawner customSpawner = new MarinePatrolSpawner();

    private final NukedAshPlacement nukedAshPlacement = new NukedAshPlacement();

    @Override
    public void onInitialize() {
        AVP.initialize();

        // Core
        AVPItems.initialize();
        WorldGen.initialize();
        CommonPacketRegistry.initialize();
        ServerPacketHandlerRegistry.initialize();
        AVPProfessions.initialize();

        // Functionality
        CompostingChanceRegistry.initialize();
        DispenserBlockBehaviors.initialize();
        LootTableModifier.initialize();
        SpawnPlacements.initialize();
        FlammableBlockRegistry.initialize();
        AVPFuelRegistry.initialize();
        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
        ServerLifecycleEvents.SERVER_STARTING.register(this::addNewVillageBuilding);
        AVPTrades.initialize();

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> REGISTRY.getLiteralArgumentBuilders()
                .forEach(dispatcher::register)
        );
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
        // FIXME: This will break on NeoForge.
        sounds.put(AVPEntityTypes.YAUTJA.get(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM);
    }

    public static void modifyGifts() {
        var gifts = GiveGiftToHeroAccessor.getGifts();

        gifts.put(AVPProfessions.COMMISSARY, AVPGifts.COMMISSARY_GIFT_LOOT_TABLE);
    }

    public void addNewVillageBuilding(final MinecraftServer event) {
        var templatePoolRegistry = event.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var processorListRegistry = event.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);

        AVPVillageInjection.addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/plains/houses"),
            "avp:village/plains/houses/plains_commissary",
            5
        );

        AVPVillageInjection.addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/snowy/houses"),
            "avp:village/snowy/houses/snowy_commissary",
            5
        );

        AVPVillageInjection.addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/savanna/houses"),
            "avp:village/savanna/houses/savanna_commissary",
            5
        );

        AVPVillageInjection.addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/taiga/houses"),
            "avp:village/taiga/houses/taiga_commissary",
            5
        );

        AVPVillageInjection.addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.withDefaultNamespace("village/desert/houses"),
            "avp:village/desert/houses/desert_commissary",
            5
        );
    }
}
