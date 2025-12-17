package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple3;
import com.just.core.functional.tuple.Tuple4;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.common.BLibDecoratedPotPatternCache;
import com.blib.internal.common.registry.util.BLibRegistrationUtil;

@ApiStatus.Internal
public class BLibNeoForgeModContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<BLibHolder<? extends Item>> azureLibIdentityEntries;

    private final List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> compostableData;

    private final List<Registry<?>> customRegistryEntries;

    private final List<Runnable> deferredDecoratedPotPatternRegistrations;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    private final List<Tuple2<BLibHolder<? extends ItemLike>, Integer>> furnaceFuelData;

    private final List<BLibEntitySpawnData<?>> entitySpawnDataEntries;

    private final List<NetworkHandler<?>> networkHandlers;

    private final List<PreparableReloadListener> reloadListeners;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public BLibNeoForgeModContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = BLibRegistrationUtil.VANILLA_REGISTRATION_ORDER
            .stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    registry -> createDeferredRegistry(mod.id(), registry)
                )
            );

        this.azureLibIdentityEntries = new ArrayList<>();
        this.compostableData = new ArrayList<>();
        this.customRegistryEntries = new ArrayList<>();
        this.deferredDecoratedPotPatternRegistrations = new ArrayList<>();
        this.entityAttributeSupplierPairs = new ArrayList<>();
        this.entitySpawnDataEntries = new ArrayList<>();
        this.furnaceFuelData = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.reloadListeners = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
    }

    public List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> getCompostableData() {
        return Collections.unmodifiableList(compostableData);
    }

    public List<Runnable> getDeferredDecoratedPotPatternRegistrations() {
        return deferredDecoratedPotPatternRegistrations;
    }

    public List<BLibEntitySpawnData<?>> getEntitySpawnDataEntries() {
        return Collections.unmodifiableList(entitySpawnDataEntries);
    }

    public List<Tuple2<BLibHolder<? extends ItemLike>, Integer>> getFurnaceFuelData() {
        return Collections.unmodifiableList(furnaceFuelData);
    }

    /* package-private */ List<BLibHolder<? extends Item>> getAzureLibIdentityEntries() {
        return azureLibIdentityEntries;
    }

    /* package-private */ List<Registry<?>> getCustomRegistryEntries() {
        return customRegistryEntries;
    }

    @SuppressWarnings("unchecked")
    /* package-private */ <T> DeferredRegister<T> getDeferredRegister(Registry<T> registry) {
        return (DeferredRegister<T>) registryToDeferredRegisterMap.get(registry);
    }

    /* package-private */ Collection<DeferredRegister<?>> getDeferredRegisters() {
        return Collections.unmodifiableCollection(registryToDeferredRegisterMap.values());
    }

    /* package-private */ List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityAttributeSupplierPairs() {
        return Collections.unmodifiableList(entityAttributeSupplierPairs);
    }

    /* package-private */ List<NetworkHandler<?>> getNetworkHandlers() {
        return networkHandlers;
    }

    /* package-private */ List<PreparableReloadListener> getReloadListeners() {
        return reloadListeners;
    }

    /* package-private */ List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }

    /* package-private */ void registerAzureLibIdentity(BLibHolder<? extends Item> holder) {
        azureLibIdentityEntries.add(holder);
    }

    /* package-private */ void registerCompostable(Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean> tuple) {
        compostableData.add(tuple);
    }

    /* package-private */ void registerCustomRegistry(Registry<?> registry) {
        customRegistryEntries.add(registry);
        // Special case for custom registries, automatically bootstrap a deferred registry for the custom registry.
        registryToDeferredRegisterMap.put(registry, createDeferredRegistry(mod.id(), registry));
    }

    /* package-private */ void registerFurnaceFuel(Tuple2<BLibHolder<? extends ItemLike>, Integer> tuple) {
        furnaceFuelData.add(tuple);
    }

    /* package-private */ void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder) {
        deferredDecoratedPotPatternRegistrations.add(
            () -> BLibDecoratedPotPatternCache.put(holder.get(), mod.resources().createKey(Registries.DECORATED_POT_PATTERN, path))
        );
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
