package com.avp.service;

import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public interface RegistryService {

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier);

    <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler);

    <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection);

    void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    );

}
