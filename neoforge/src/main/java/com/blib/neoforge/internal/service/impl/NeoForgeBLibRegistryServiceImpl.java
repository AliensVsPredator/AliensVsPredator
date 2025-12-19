package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple4;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
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

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.registry.BLibHolder;
import com.blib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;
import com.blib.internal.service.BLibRegistryService;
import com.blib.neoforge.internal.data.BLibNeoForgeCompostableDataMapProvider;
import com.blib.neoforge.internal.data.BLibNeoForgeEntitySpawnDataProvider;
import com.blib.neoforge.internal.data.BLibNeoForgeFurnaceFuelDataMapProvider;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibLevelTickEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibPlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibTagsUpdatedEvents;

@ApiStatus.Internal
public class NeoForgeBLibRegistryServiceImpl implements BLibRegistryService {

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
    public void registerAzureLibIdentity(BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .registerAzureLibIdentity(holder);
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
    public void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .registerDecoratedPotPattern(path, holder);
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
    public void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener) {
        getModContainer(mod)
            .registerReloadListener(listener);
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

    /* package-private */ void finalize(BLibMod mod, IEventBus eventBus) {
        var modContainer = getModContainer(mod);

        modContainer
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(eventBus));

        eventBus.<EntityAttributeCreationEvent>addListener(event -> onRegisterEntityAttributes(mod, event));
        eventBus.<RegisterSpawnPlacementsEvent>addListener(event -> onRegisterEntitySpawnPlacements(mod, event));

        eventBus.<FMLCommonSetupEvent>addListener(
            event -> {
                modContainer
                    .getAzureLibIdentityEntries()
                    .forEach(holder -> AzIdentityRegistry.register(holder.get()));

                modContainer
                    .getDeferredDecoratedPotPatternRegistrations()
                    .forEach(Runnable::run);
            }
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
        });

        NeoForge.EVENT_BUS.<RegisterCommandsEvent>addListener(
            event -> modContainer.getLiteralArgumentBuilders()
                .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder))
        );

        NeoForge.EVENT_BUS.<AddReloadListenerEvent>addListener(
            event -> modContainer.getReloadListeners().forEach(event::addListener)
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

        NeoForgeBLibLevelTickEvents.AFTER.initialize();
        NeoForgeBLibLevelTickEvents.BEFORE.initialize();
        NeoForgeBLibPlayerBlockBreakEvents.BEFORE.initialize();
        NeoForgeBLibTagsUpdatedEvents.ROUTER.initialize();
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
}
