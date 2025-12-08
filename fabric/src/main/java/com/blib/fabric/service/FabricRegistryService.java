package com.blib.fabric.service;

import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    private final List<NetworkHandler<?>> clientBoundPacketHandlers;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    public FabricRegistryService() {
        this.clientBoundPacketHandlers = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier) {
        AzIdentityRegistry.register(itemSupplier.get());
    }

    @Override
    public void registerCompostableItem(
        Supplier<? extends ItemLike> itemLikeSupplier,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        CompostingChanceRegistry.INSTANCE.add(itemLikeSupplier.get(), chance);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        var spawnSettings = spawnData.getConfigData().spawnSettings();
        var entityType = spawnData.getEntityType();

        if (!spawnData.isPlacementDisabled()) {
            var placement = spawnData.getPlacementData().type();
            var heightMap = spawnData.getPlacementData().heightmapType();
            var spawnPredicate = spawnData.getPlacementData().spawnPredicate();

            SpawnPlacements.register(entityType, placement, heightMap, spawnPredicate);
        }

        if (!spawnData.isConfigDisabled()) {
            Predicate<BiomeSelectionContext> biomeSelector = biomeSelectionContext -> biomeSelectionContext.hasTag(
                spawnData.getConfigData().biomeTagKey()
            );
            var spawnGroup = entityType.getCategory();
            var weight = spawnSettings.weight();
            var minGroupSize = spawnSettings.minGroupSize();
            var maxGroupSize = spawnSettings.maxGroupSize();

            BiomeModifications.addSpawn(biomeSelector, spawnGroup, entityType, weight, minGroupSize, maxGroupSize);
        }
    }

    @Override
    public void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        FuelRegistry.INSTANCE.add(itemLikeSupplier.get(), burnTimeInTicks);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler) {
        switch (networkHandler) {
            case NetworkHandler.FromClient<T> handler -> ServerPlayNetworking.registerGlobalReceiver(
                networkHandler.type(),
                (payload, context) -> context.server().execute(() -> handler.payloadConsumer().accept(payload, context.player()))
            );
            case NetworkHandler.FromEither<T> handler -> {
                ServerPlayNetworking.registerGlobalReceiver(
                    networkHandler.type(),
                    (payload, context) -> context.server()
                        .execute(() -> handler.fromClientPayloadConsumer().accept(payload, context.player()))
                );

                clientBoundPacketHandlers.add(networkHandler);
            }
            case NetworkHandler.FromServer<T> handler -> clientBoundPacketHandlers.add(networkHandler);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection) {
        var handleClient = false;
        var handleServer = false;
        var codec = new JustStreamCodecToMojangStreamCodecAdapter<>(packetDirection.codec());
        var type = packetDirection.type();

        switch (packetDirection) {
            case PacketDirection.BI<T> ignored -> {
                handleClient = true;
                handleServer = true;
            }
            case PacketDirection.C2S<T> ignored -> handleServer = true;
            case PacketDirection.S2C<T> ignored -> handleClient = true;
        }

        if (handleClient) {
            PayloadTypeRegistry.playS2C().register(type, codec);
        }

        if (handleServer) {
            PayloadTypeRegistry.playC2S().register(type, codec);
        }
    }

    @Override
    public PreparableReloadListener registerReloadListener(String id, PreparableReloadListener listener) {
        var adaptedListener = new IdentifiableResourceReloadListener() {

            @Override
            public ResourceLocation getFabricId() {
                return AVPResources.location(id);
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(
                PreparationBarrier preparationBarrier,
                ResourceManager resourceManager,
                ProfilerFiller preparationsProfiler,
                ProfilerFiller reloadProfiler,
                Executor backgroundExecutor,
                Executor gameExecutor
            ) {
                return listener.reload(
                    preparationBarrier,
                    resourceManager,
                    preparationsProfiler,
                    reloadProfiler,
                    backgroundExecutor,
                    gameExecutor
                );
            }
        };

        ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(adaptedListener);

        return listener;
    }

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        TradeOfferHelper.registerVillagerOffers(
            villagerProfessionSupplier.get(),
            level,
            factories -> factories.addAll(villagerTradeItemListings)
        );
    }

    public List<NetworkHandler<?>> getClientBoundPacketHandlers() {
        return clientBoundPacketHandlers;
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return literalArgumentBuilders;
    }
}
