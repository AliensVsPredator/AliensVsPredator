package com.avp.neoforge.service;

import com.avp.service.RegistryService;
import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple3;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;

public class NeoForgeRegistryService implements RegistryService {

    private final List<Supplier<? extends Item>> azureLibItemIdentitySuppliers;

    private final List<Tuple2<Supplier<? extends ItemLike>, Integer>> furnaceFuelPairs;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final List<NetworkHandler<?>> networkHandlers;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public NeoForgeRegistryService() {
        this.azureLibItemIdentitySuppliers = new ArrayList<>();
        this.furnaceFuelPairs = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier) {
        azureLibItemIdentitySuppliers.add(itemSupplier);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler) {
        networkHandlers.add(networkHandler);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection) {
        /* NO-OP */
    }

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(villagerProfessionSupplier, level, villagerTradeItemListings));
    }

    public List<Supplier<? extends Item>> getAzureLibItemIdentitySuppliers() {
        return azureLibItemIdentitySuppliers;
    }

    public List<Tuple2<Supplier<? extends ItemLike>, Integer>> getFurnaceFuelPairs() {
        return furnaceFuelPairs;
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return literalArgumentBuilders;
    }

    public List<NetworkHandler<?>> getNetworkHandlers() {
        return networkHandlers;
    }

    public List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }
}
