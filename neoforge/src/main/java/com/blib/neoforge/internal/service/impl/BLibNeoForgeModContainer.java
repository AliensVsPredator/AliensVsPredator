package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple3;
import com.just.core.functional.tuple.Tuple4;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.common.event.impl.BLibCommonSetupEvents;
import com.blib.common.event.impl.BLibEventListenerContainer;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.registry.BLibHolder;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeLevelTickEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerTrackingEntityEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeTagsUpdatedEvents;

@ApiStatus.Internal
public class BLibNeoForgeModContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> compostableData;

    private final List<Registry<?>> customRegistryEntries;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    private final List<Tuple2<BLibHolder<? extends ItemLike>, Integer>> furnaceFuelData;

    private final List<BLibEntitySpawnData<?>> entitySpawnDataEntries;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final List<NetworkHandler<?>> networkHandlers;

    private final BLibEventListenerContainer<BLibCommonSetupEvent> onCommonSetup;

    private final BLibNeoForgeEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity;

    private final BLibNeoForgeEventHandle<BLibTagsUpdatedEvent> onTagsUpdated;

    private final BLibNeoForgeEventHandle<BLibLevelTickEvent> postLevelTick;

    private final BLibNeoForgeEventHandle<BLibBlockBreakEvent> preBlockBreak;

    private final BLibNeoForgeEventHandle<BLibLevelTickEvent> preLevelTick;

    private final List<PreparableReloadListener> reloadListeners;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public BLibNeoForgeModContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = new HashMap<>();

        this.compostableData = new ArrayList<>();
        this.customRegistryEntries = new ArrayList<>();
        this.entityAttributeSupplierPairs = new ArrayList<>();
        this.entitySpawnDataEntries = new ArrayList<>();
        this.furnaceFuelData = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.onCommonSetup = BLibCommonSetupEvents.FACTORY.apply(mod);
        this.onPlayerStartTrackingEntity = BLibNeoForgePlayerTrackingEntityEvents.FACTORY.apply(mod);
        this.onTagsUpdated = BLibNeoForgeTagsUpdatedEvents.FACTORY.apply(mod);
        this.postLevelTick = BLibNeoForgeLevelTickEvents.POST_FACTORY.apply(mod);
        this.preBlockBreak = BLibNeoForgePlayerBlockBreakEvents.FACTORY.apply(mod);
        this.preLevelTick = BLibNeoForgeLevelTickEvents.PRE_FACTORY.apply(mod);
        this.reloadListeners = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
    }

    public List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> getCompostableData() {
        return Collections.unmodifiableList(compostableData);
    }

    public List<BLibEntitySpawnData<?>> getEntitySpawnDataEntries() {
        return Collections.unmodifiableList(entitySpawnDataEntries);
    }

    public List<Tuple2<BLibHolder<? extends ItemLike>, Integer>> getFurnaceFuelData() {
        return Collections.unmodifiableList(furnaceFuelData);
    }

    public BLibEventListenerContainer<BLibCommonSetupEvent> onCommonSetup() {
        return onCommonSetup;
    }

    public BLibNeoForgeEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return onPlayerStartTrackingEntity;
    }

    public BLibNeoForgeEventHandle<BLibTagsUpdatedEvent> onTagsUpdated() {
        return onTagsUpdated;
    }

    public BLibNeoForgeEventHandle<BLibLevelTickEvent> postLevelTick() {
        return postLevelTick;
    }

    public BLibNeoForgeEventHandle<BLibBlockBreakEvent> preBlockBreak() {
        return preBlockBreak;
    }

    public BLibNeoForgeEventHandle<BLibLevelTickEvent> preLevelTick() {
        return preLevelTick;
    }

    /* package-private */ List<Registry<?>> getCustomRegistryEntries() {
        return Collections.unmodifiableList(customRegistryEntries);
    }

    @SuppressWarnings("unchecked")
    /* package-private */ <T> DeferredRegister<T> getDeferredRegister(Registry<T> registry) {
        return (DeferredRegister<T>) registryToDeferredRegisterMap.computeIfAbsent(
            registry,
            $ -> createDeferredRegistry(mod.id(), registry)
        );
    }

    /* package-private */ Collection<DeferredRegister<?>> getDeferredRegisters() {
        return Collections.unmodifiableCollection(registryToDeferredRegisterMap.values());
    }

    /* package-private */ List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityAttributeSupplierPairs() {
        return Collections.unmodifiableList(entityAttributeSupplierPairs);
    }

    /* package-private */ List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return Collections.unmodifiableList(literalArgumentBuilders);
    }

    /* package-private */ List<NetworkHandler<?>> getNetworkHandlers() {
        return Collections.unmodifiableList(networkHandlers);
    }

    /* package-private */ List<PreparableReloadListener> getReloadListeners() {
        return Collections.unmodifiableList(reloadListeners);
    }

    /* package-private */ List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return Collections.unmodifiableList(villagerTradeData);
    }

    /* package-private */ void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    /* package-private */ void registerCompostable(Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean> tuple) {
        compostableData.add(tuple);
    }

    /* package-private */ void registerCustomRegistry(Registry<?> registry) {
        customRegistryEntries.add(registry);
    }

    /* package-private */ void registerFurnaceFuel(Tuple2<BLibHolder<? extends ItemLike>, Integer> tuple) {
        furnaceFuelData.add(tuple);
    }

    /* package-private */ void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        entityAttributeSupplierPairs.add(new Tuple2<>(holder, attributeSupplierBuilderSupplier));
    }

    /* package-private */ <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        entitySpawnDataEntries.add(spawnData);
    }

    /* package-private */ <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler) {
        networkHandlers.add(networkHandler);
    }

    /* package-private */ void registerReloadListener(PreparableReloadListener listener) {
        reloadListeners.add(listener);
    }

    /* package-private */ void registerVillagerTrade(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(holder, level, villagerTradeItemListings));
    }
}
