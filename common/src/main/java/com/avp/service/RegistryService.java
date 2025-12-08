package com.avp.service;

import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public interface RegistryService {

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier);

    void registerCompostableItem(
        Supplier<? extends ItemLike> itemLikeSupplier,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    );

    <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData);

    void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks);

    <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler);

    <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection);

    PreparableReloadListener registerReloadListener(String id, PreparableReloadListener listener);

    void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    );

}
