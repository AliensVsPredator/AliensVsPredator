package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple4;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

import com.blib.api.BLibAPI;
import com.blib.api.common.codec.v1.stream.adapter.J2MStreamCodecAdapter;
import com.blib.api.common.entity.v1.spawning.BLibEntitySpawnData;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.network.v1.PacketDirection;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.service.BLibRegistryService;
import com.blib.neoforge.internal.data.BLibNeoForgeCompostableDataMapProvider;
import com.blib.neoforge.internal.data.BLibNeoForgeEntitySpawnDataProvider;
import com.blib.neoforge.internal.data.BLibNeoForgeFurnaceFuelDataMapProvider;

@ApiStatus.Internal
public class BLibNeoForgeRegistryServiceImpl implements BLibRegistryService {

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        var blibRegistry = holder.getRegistry();
        var modContainer = getModContainer(blibRegistry.getMod());
        var backingRegistry = blibRegistry.getBackingRegistry();
        @SuppressWarnings("unchecked")
        var deferredRegister = (DeferredRegister<T>) modContainer.getDeferredRegister(backingRegistry);

        if (deferredRegister == null) {
            throw new IllegalArgumentException("Unhandled registry: " + backingRegistry);
        }

        return deferredRegister.register(holder.getPath(), valueFactory);
    }

    @Override
    public void registerCommand(BLibMod mod, LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        getModContainer(mod)
            .registerCommand(literalArgumentBuilder);
    }

    @Override
    public void registerCompostable(BLibHolder<? extends ItemLike> holder, float chance, boolean villagersCanCompost, boolean replace) {
        getModContainer(holder)
            .registerCompostable(new Tuple4<>(holder, chance, villagersCanCompost, replace));
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .registerEntityAttributes(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .registerEntitySpawnData(spawnData);
    }

    @Override
    public void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        getModContainer(holder)
            .registerFurnaceFuel(new Tuple2<>(holder, burnTimeInTicks));
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandler(BLibMod mod, NetworkHandler<T> networkHandler) {
        getModContainer(mod)
            .registerPacketHandlers(networkHandler);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(BLibMod mod, PacketDirection<T> packetDirection) {
        /* NO-OP */
    }

    @Override
    public void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener, PackType packType) {
        switch (packType) {
            case CLIENT_RESOURCES -> registerClientReloadListener(listener);
            case SERVER_DATA -> getModContainer(mod)
                .registerReloadListener(listener, packType);
        }
    }

    @Override
    public void registerVillagerTrade(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        getModContainer(holder)
            .registerVillagerTrade(holder, level, villagerTradeItemListings);
    }

    public BLibNeoForgeModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    public BLibNeoForgeModContainer getModContainer(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod);
    }

    /* package-private */ void initialize(BLibMod mod, IEventBus eventBus) {
        var modContainer = getModContainer(mod);

        modContainer
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(eventBus));

        eventBus.<EntityAttributeCreationEvent>addListener(event -> onRegisterEntityAttributes(mod, event));
        eventBus.<RegisterSpawnPlacementsEvent>addListener(event -> onRegisterEntitySpawnPlacements(mod, event));

        eventBus.<FMLCommonSetupEvent>addListener(
            event -> modContainer.onCommonSetup()
                .getListeners()
                .forEach(BLibCommonSetupEvent::invoke)
        );

        eventBus.<NewRegistryEvent>addListener(event -> modContainer.getCustomRegistryEntries().forEach(event::register));

        eventBus.<GatherDataEvent>addListener(event -> {
            var generator = event.getGenerator();
            var packOutput = generator.getPackOutput();
            var lookupProvider = event.getLookupProvider();
            var run = event.includeServer();

            generator.addProvider(run, new BLibNeoForgeCompostableDataMapProvider(mod, packOutput, lookupProvider));
            generator.addProvider(run, new BLibNeoForgeEntitySpawnDataProvider(mod, lookupProvider));
            generator.addProvider(run, new BLibNeoForgeFurnaceFuelDataMapProvider(mod, packOutput, lookupProvider));
        });

        eventBus.<RegisterPayloadHandlersEvent>addListener(event -> {
            var registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);

            modContainer.getNetworkHandlers()
                .forEach(networkHandler -> {
                    @SuppressWarnings("unchecked")
                    var typedNetworkHandler = (NetworkHandler<CustomPacketPayload>) networkHandler;

                    switch (typedNetworkHandler) {
                        case NetworkHandler.FromClient<CustomPacketPayload> handler -> registrar.playToServer(
                            handler.type(),
                            new J2MStreamCodecAdapter<>(handler.codec()),
                            (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                        );
                        case NetworkHandler.FromEither<CustomPacketPayload> handler -> registrar.playBidirectional(
                            handler.type(),
                            new J2MStreamCodecAdapter<>(handler.codec()),
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
                            new J2MStreamCodecAdapter<>(handler.codec()),
                            (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                        );
                    }
                });
        });

        NeoForge.EVENT_BUS.<RegisterCommandsEvent>addListener(
            event -> modContainer.getLiteralArgumentBuilders()
                .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder))
        );

        NeoForge.EVENT_BUS.<AddReloadListenerEvent>addListener(
            event -> modContainer.getReloadListeners()
                .forEach(tuple2 -> event.addListener(tuple2.v1()))
        );

        NeoForge.EVENT_BUS.<VillagerTradesEvent>addListener(event -> {
            var trades = event.getTrades();

            modContainer
                .getVillagerTradeData()
                .forEach(villagerTradeData -> {
                    if (event.getType() == villagerTradeData.v1().get()) {
                        trades.get(villagerTradeData.v2()).addAll(villagerTradeData.v3());
                    }
                });
        });

        modContainer.preLevelTick().initialize();
        modContainer.preBlockBreak().initialize();

        modContainer.postLevelTick().initialize();

        modContainer.onPlayerStartTrackingEntity().initialize();
        modContainer.onTagsUpdated().initialize();
        modContainer.serverStarted().initialize();
        modContainer.serverStarting().initialize();
        modContainer.serverStopped().initialize();
        modContainer.serverStopping().initialize();
    }

    private void onRegisterEntityAttributes(BLibMod mod, EntityAttributeCreationEvent event) {
        getModContainer(mod).getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.v1().get(), pair.v2().get().build()));
    }

    private void onRegisterEntitySpawnPlacements(BLibMod mod, RegisterSpawnPlacementsEvent event) {
        getModContainer(mod).getEntitySpawnDataEntries()
            .forEach(spawnData -> {
                if (spawnData.isPlacementDisabled()) {
                    return;
                }

                @SuppressWarnings("unchecked")
                var entityType = (EntityType<Mob>) spawnData.getEntityTypeHolder().get();
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

    private static void registerClientReloadListener(PreparableReloadListener preparableReloadListener) {
        if (BLibAPI.getDistributionType() != DistributionEnvironmentType.CLIENT) {
            return;
        }

        var mc = Minecraft.getInstance();

        if (mc == null) {
            return;
        }

        if (!(mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)) {
            throw new RuntimeException("Client reload listener was initialized too early!");
        }

        resourceManager.registerReloadListener(preparableReloadListener);
    }
}
