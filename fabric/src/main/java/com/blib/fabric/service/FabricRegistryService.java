package com.blib.fabric.service;

import com.avp.service.RegistryService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;

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
