package com.avp.neoforge;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;

import com.avp.AVP;
import com.avp.common.entity.living.alien.chestburster.ChestbursterSpawning;
import com.avp.common.entity.living.alien.ovamorph.OvamorphSpawning;
import com.avp.common.entity.living.alien.xenomorph.drone.DroneSpawning;
import com.avp.common.entity.living.alien.xenomorph.praetorian.PraetorianSpawning;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.avp.common.entity.living.alien.xenomorph.warrior.WarriorSpawning;
import com.avp.common.entity.living.human.marine.MarineSpawning;
import com.avp.common.entity.living.villager.gift.AVPVillagerGiftKeys;
import com.avp.common.entity.living.villager.profession.AVPVillagerProfessions;
import com.avp.common.entity.living.yautja.YautjaSpawning;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.registry.AlienInfectionRegistry;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.network.NetworkHandler;
import com.avp.data.worldgen.AVPVillageInjection;
import com.avp.mixin.GiveGiftToHeroAccessor;
import com.avp.mixin.ParrotSoundMapAccessor;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;

@Mod(AVP.MOD_ID)
public class AVPNeoForge {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    public AVPNeoForge(IEventBus modBus) {
        AVP.initialize();

        REGISTRY.initialize(modBus);

        // Mod bus events.
        modBus.addListener(AVPNeoForge::registerPayloadHandlers);
        modBus.addListener(AVPNeoForge::registerEntityAttributes);
        modBus.addListener(AVPNeoForge::registerSpawnPlacements);
        modBus.addListener(AVPNeoForge::registerMiscellaneous);

        // Game bus events.
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerCommands);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addNewVillageBuilding);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addCustomTrades);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, AVPNeoForge::onWorldEndTick);
    }

    public static void registerMiscellaneous(FMLCommonSetupEvent event) {
        // Register alien infections.
        REGISTRY.getAlienInfectionSuppliers()
            .forEach(alienInfectionSupplier -> AlienInfectionRegistry.register(alienInfectionSupplier.get()));
        // Register alien lifecycles.
        REGISTRY.getAlienLifecycleSuppliers()
            .forEach(alienLifecycleSupplier -> AlienLifecycleRegistry.register(alienLifecycleSupplier.get()));
        // Register AzureLib item identities.
        REGISTRY.getAzureLibItemIdentitySuppliers()
            .forEach(itemSupplier -> AzIdentityRegistry.register(itemSupplier.get()));
    }

    // Game event
    public static void registerCommands(RegisterCommandsEvent event) {
        REGISTRY.getLiteralArgumentBuilders()
            .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder));
    }

    // Mod event
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        REGISTRY.getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.first().get(), pair.second().get().build()));
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
        event.register(
            AVPEntityTypes.YAUTJA.get(),
            placement,
            heightMap,
            YautjaSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );

        event.register(
            AVPEntityTypes.MARINE.get(),
            placement,
            heightMap,
            MarineSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.DRONE.get(),
            placement,
            heightMap,
            DroneSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.PRAETORIAN.get(),
            placement,
            heightMap,
            PraetorianSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.QUEEN.get(),
            placement,
            heightMap,
            QueenSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.WARRIOR.get(),
            placement,
            heightMap,
            WarriorSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_DRONE.get(),
            placement,
            heightMap,
            DroneSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_PRAETORIAN.get(),
            placement,
            heightMap,
            PraetorianSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_WARRIOR.get(),
            placement,
            heightMap,
            WarriorSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_QUEEN.get(),
            placement,
            heightMap,
            QueenSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.CHESTBURSTER.get(),
            placement,
            heightMap,
            ChestbursterSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.OVAMORPH.get(),
            placement,
            heightMap,
            OvamorphSpawning.PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_CHESTBURSTER.get(),
            placement,
            heightMap,
            ChestbursterSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
            AVPEntityTypes.NETHER_OVAMORPH.get(),
            placement,
            heightMap,
            OvamorphSpawning.NETHER_PREDICATE,
            RegisterSpawnPlacementsEvent.Operation.AND
        );
    }

    // Inject Village houses
    public static void addNewVillageBuilding(ServerAboutToStartEvent event) {
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
    public static void onWorldEndTick(LevelTickEvent.Post event) {
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
        gifts.put(AVPVillagerProfessions.COMMISSARY.get(), AVPVillagerGiftKeys.COMMISSARY_GIFT_LOOT_TABLE);
    }

    public static void addCustomTrades(VillagerTradesEvent event) {
        var trades = event.getTrades();

        REGISTRY.getVillagerTradeData()
            .forEach(villagerTradeData -> {
                if (event.getType() == villagerTradeData.first().get()) {
                    trades.get(villagerTradeData.second()).addAll(villagerTradeData.third());
                }
            });
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1")
            .executesOn(HandlerThread.NETWORK);

        REGISTRY.getNetworkHandlers()
            .forEach(networkHandler -> {
                @SuppressWarnings("unchecked")
                var typedNetworkHandler = (NetworkHandler<CustomPacketPayload>) networkHandler;

                switch (typedNetworkHandler) {
                    case NetworkHandler.FromClient<CustomPacketPayload> handler -> registrar.playToServer(
                        handler.type(),
                        handler.codec(),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                    case NetworkHandler.FromEither<CustomPacketPayload> handler -> registrar.playBidirectional(
                        handler.type(),
                        handler.codec(),
                        new DirectionalPayloadHandler<>(
                            (payload, context) -> context.enqueueWork(
                                () -> handler.fromServerPayloadConsumer().accept(payload, context.player())
                            ),
                            (payload, context) -> context.enqueueWork(
                                () -> handler.fromClientPayloadConsumer().accept(payload, context.player())
                            )
                        )
                    );
                    case NetworkHandler.FromServer<CustomPacketPayload> handler -> registrar.playToClient(
                        handler.type(),
                        handler.codec(),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                }
            });
    }
}
