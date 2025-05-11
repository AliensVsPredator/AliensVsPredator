package com.avp.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

import com.avp.common.entity.spawning.AVPEntitySpawnData;
import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.network.NetworkHandler;
import com.avp.common.network.PacketDirection;
import com.avp.common.registry.AVPDeferredHolder;

public interface RegistryService {

    <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier);

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    <S extends LivingEntity, P extends LivingEntity> Supplier<AlienInfection<S, P>> registerAlienInfection(
        Supplier<AlienInfection<S, P>> alienInfectionSupplier
    );

    Supplier<AlienLifecycle> registerAlienLifecycle(Supplier<AlienLifecycle> alienLifecycleSupplier);

    void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier);

    void registerCompostableItem(
        Supplier<? extends ItemLike> itemLikeSupplier,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    );

    void registerEntityAttributes(
        Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    );

    <T extends Mob> void registerEntitySpawnData(AVPEntitySpawnData<T> spawnData);

    void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks);

    <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler);

    <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection);

    void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    );

}
