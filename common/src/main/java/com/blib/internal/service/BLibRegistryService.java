package com.blib.internal.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.registry.BLibHolder;

@ApiStatus.Internal
public interface BLibRegistryService {

    <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory);

    void registerCommand(BLibMod mod, LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    void registerCompostable(BLibHolder<? extends ItemLike> holder, float chance, boolean villagersCanCompost, boolean replace);

    void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    );

    <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData);

    void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks);

    <T extends CustomPacketPayload> void registerPacketHandler(BLibMod mod, NetworkHandler<T> networkHandler);

    <T extends CustomPacketPayload> void registerPacketDirection(BLibMod mod, PacketDirection<T> packetDirection);

    void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener);

    void registerVillagerTrade(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    );
}
