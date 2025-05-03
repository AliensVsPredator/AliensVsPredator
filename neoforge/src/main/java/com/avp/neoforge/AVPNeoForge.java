package com.avp.neoforge;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.registry.AlienInfectionRegistry;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.profession.AVPCommonTrades;
import com.avp.common.profession.AVPGifts;
import com.avp.data.worldgen.AVPVillageInjection;
import com.avp.mixin.GiveGiftToHeroAccessor;
import com.avp.mixin.ParrotSoundMapAccessor;
import com.avp.neoforge.common.profession.AVPProfessions;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;

@Mod(AVP.MOD_ID)
public class AVPNeoForge {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    public AVPNeoForge(IEventBus modBus) {
        AVP.initialize();

        REGISTRY.initialize(modBus);
        AVPProfessions.register(modBus);

        // Mod bus events
        modBus.addListener(AVPNeoForge::registerEntityAttributes);
        modBus.addListener(AVPNeoForge::registerMiscellaneous);

        // Game bus events
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerCommands);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addNewVillageBuilding);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addCustomTrades);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, AVPNeoForge::onWorldEndTick);
    }

    public static void registerMiscellaneous(final FMLCommonSetupEvent event) {
        // Register alien infections.
        REGISTRY.getAlienInfectionSuppliers()
            .forEach(alienInfectionSupplier -> AlienInfectionRegistry.register(alienInfectionSupplier.get()));
        // Register alien lifecycles.
        REGISTRY.getAlienLifecycleSuppliers()
            .forEach(alienLifecycleSupplier -> AlienLifecycleRegistry.register(alienLifecycleSupplier.get()));
    }

    // Game event
    public static void registerCommands(final RegisterCommandsEvent event) {
        REGISTRY.getLiteralArgumentBuilders()
            .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder));
    }

    // Mod event
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        REGISTRY.getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.first().get(), pair.second().get().build()));
    }

    // Inject Village houses
    public static void addNewVillageBuilding(final ServerAboutToStartEvent event) {
        var templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        var processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

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

    // Marine Spawns and Ash placement in nuked zones
    public static void onWorldEndTick(final LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide)
            return;

        var serverLevel = (ServerLevel) event.getLevel();
        var sounds = ParrotSoundMapAccessor.getSoundMap();
        var gifts = GiveGiftToHeroAccessor.getGifts();

        AVP.CUSTOM_SPAWNER.tick(serverLevel, serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING), true);
        AVP.NUKED_ASH_PLACEMENT.tick(serverLevel);
        /*
         * TODO: Use Yautja sound when added
         */
        sounds.put(AVPEntityTypes.YAUTJA.get(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM);
        gifts.put(AVPProfessions.COMMISSARY.value(), AVPGifts.COMMISSARY_GIFT_LOOT_TABLE);
    }

    public static void addCustomTrades(final VillagerTradesEvent event) {
        if (event.getType() == AVPProfessions.COMMISSARY.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).addAll(AVPCommonTrades.level1Trades);
            trades.get(2).addAll(AVPCommonTrades.level2Trades);
            trades.get(3).addAll(AVPCommonTrades.level3Trades);
            trades.get(4).addAll(AVPCommonTrades.level4Trades);
            trades.get(5).addAll(AVPCommonTrades.level5Trades);
        }
    }
}
