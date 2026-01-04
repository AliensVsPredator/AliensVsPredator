package com.blib.fabric.internal.service.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.registry.BLibHolder;
import com.blib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;
import com.blib.internal.service.BLibRegistryService;

@ApiStatus.Internal
public class BLibFabricRegistryServiceImpl implements BLibRegistryService {

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        getModContainer(holder)
            .deferRegistration(holder, valueFactory);

        return holder;
    }

    @Override
    public void registerCommand(BLibMod mod, LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        getModContainer(mod)
            .registerCommand(literalArgumentBuilder);
    }

    @Override
    public void registerCompostable(
        BLibHolder<? extends ItemLike> holder,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        getModContainer(holder)
            .deferCompostableRegistration(holder, chance);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .deferEntityAttributesRegistration(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .deferEntitySpawnDataRegistration(spawnData);
    }

    @Override
    public void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        getModContainer(holder)
            .deferFurnaceFuelRegistration(holder, burnTimeInTicks);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandler(BLibMod mod, NetworkHandler<T> networkHandler) {
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

                getModContainer(mod).registerNetworkHandler(networkHandler);
            }
            case NetworkHandler.FromServer<T> handler -> getModContainer(mod).registerNetworkHandler(networkHandler);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(BLibMod mod, PacketDirection<T> packetDirection) {
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
    public void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener) {
        var resourceLocation = mod.resources().createLocation(path);
        var adaptedListener = new IdentifiableResourceReloadListener() {

            @Override
            public ResourceLocation getFabricId() {
                return resourceLocation;
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
    }

    @Override
    public void registerVillagerTrade(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        getModContainer(holder)
            .deferVillagerTradeRegistration(holder, level, villagerTradeItemListings);
    }

    public BLibFabricModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    public BLibFabricModContainer getModContainer(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod);
    }

    /* package-private */ void initialize(BLibMod mod) {
        getModContainer(mod)
            .finalizeRegistrations();
    }
}
