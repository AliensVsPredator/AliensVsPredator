package com.avp.neoforge;

import com.lib.common.network.DataContainer;
import com.lib.common.network.DataUser;
import com.lib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;

import com.avp.AVP;
import com.avp.common.AVPEvents;
import com.avp.common.data.worldgen.AVPVillageInjection;
import com.avp.common.network.NetworkHandler;
import com.avp.common.registry.init.AVPVillagerProfessions;
import com.avp.common.registry.key.AVPVillagerGiftKeys;
import com.avp.mixin.GiveGiftToHeroAccessor;
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
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerDataReloadListeners);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerPlayerTrackingEntityHandler);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerTagUpdateHandler);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addNewVillageBuilding);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addCustomTrades);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, AVPNeoForge::onWorldEndTick);
    }

    public static void registerMiscellaneous(FMLCommonSetupEvent event) {
        // Register AzureLib item identities.
        REGISTRY.getAzureLibItemIdentitySuppliers()
            .forEach(itemSupplier -> AzIdentityRegistry.register(itemSupplier.get()));
    }

    // Game event
    public static void registerCommands(RegisterCommandsEvent event) {
        REGISTRY.getLiteralArgumentBuilders()
            .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder));
    }

    // Game event
    public static void registerDataReloadListeners(AddReloadListenerEvent event) {
        REGISTRY.getReloadListeners()
            .forEach(event::addListener);
    }

    public static void registerPlayerTrackingEntityHandler(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }

    // Game event
    public static void registerTagUpdateHandler(TagsUpdatedEvent event) {
        AVPEvents.onTagsUpdated();
    }

    // Mod event
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        REGISTRY.getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.v1().get(), pair.v2().get().build()));
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        REGISTRY.getEntitySpawnDataEntries().forEach(spawnData -> {
            if (spawnData.isPlacementDisabled()) {
                return;
            }

            @SuppressWarnings("unchecked")
            var entityType = (EntityType<Mob>) spawnData.getEntityType();
            var placementData = spawnData.getPlacementData();
            var placement = placementData.type();
            var heightMap = placementData.heightmapType();
            @SuppressWarnings("unchecked")
            var spawnPredicate = (SpawnPlacements.SpawnPredicate<Mob>) placementData.spawnPredicate();

            event.register(
                entityType,
                placement,
                heightMap,
                spawnPredicate,
                RegisterSpawnPlacementsEvent.Operation.AND
            );
        });
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
        var gifts = GiveGiftToHeroAccessor.getGifts();

        AVP.CUSTOM_SPAWNER.tick(serverLevel, serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING), true);
        AVP.NUKED_ASH_PLACEMENT.tick(serverLevel);
        gifts.put(AVPVillagerProfessions.COMMISSARY.get(), AVPVillagerGiftKeys.COMMISSARY_GIFT_LOOT_TABLE);
    }

    public static void addCustomTrades(VillagerTradesEvent event) {
        var trades = event.getTrades();

        REGISTRY.getVillagerTradeData()
            .forEach(villagerTradeData -> {
                if (event.getType() == villagerTradeData.v1().get()) {
                    trades.get(villagerTradeData.v2()).addAll(villagerTradeData.v3());
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
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                    case NetworkHandler.FromEither<CustomPacketPayload> handler -> registrar.playBidirectional(
                        handler.type(),
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
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
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                }
            });
    }
}
