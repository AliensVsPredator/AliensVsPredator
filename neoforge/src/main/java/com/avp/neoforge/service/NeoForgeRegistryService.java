package com.avp.neoforge.service;

import com.avp.service.RegistryService;
import com.just.core.functional.tuple.Tuple3;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;

public class NeoForgeRegistryService implements RegistryService {

    private final List<NetworkHandler<?>> networkHandlers;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public NeoForgeRegistryService() {
        this.networkHandlers = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
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

    public List<NetworkHandler<?>> getNetworkHandlers() {
        return networkHandlers;
    }

    public List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }
}
