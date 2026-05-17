package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple3;
import com.just.core.functional.tuple.Tuple4;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
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

import com.blib.api.client.event.v1.BLibScreenInitEvent;
import com.blib.api.common.entity.v1.spawning.BLibEntitySpawnData;
import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibChunkClaimAddedEvent;
import com.blib.api.common.event.v1.BLibChunkClaimRemovedEvent;
import com.blib.api.common.event.v1.BLibChunkLoadEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibEntityLoadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibFactionCreatedEvent;
import com.blib.api.common.event.v1.BLibFactionDataChangedEvent;
import com.blib.api.common.event.v1.BLibFactionMemberChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRelationshipChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibFactionsLoadedEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerAdvancementAwardEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.event.v1.handle.BLibGlobalOnlyEventHandle;
import com.blib.api.common.event.v1.handle.impl.BLibEventListenerContainer;
import com.blib.api.common.event.v1.impl.BLibCommonSetupEvents;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeLevelTickEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerTrackingEntityEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeScreenInitEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeServerLifecycleEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeTagsUpdatedEvents;

@ApiStatus.Internal
public class BLibNeoForgeModContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Tuple3<Holder<Potion>, Supplier<? extends Item>, Holder<Potion>>> brewingRecipeData;

    private final List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> compostableData;

    private final List<Registry<?>> customRegistryEntries;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    private final List<Tuple2<BLibHolder<? extends ItemLike>, Integer>> furnaceFuelData;

    private final List<BLibEntitySpawnData<?>> entitySpawnDataEntries;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final List<NetworkHandler<?>> networkHandlers;

    private final BLibEventListenerHandle<BLibChunkClaimAddedEvent> onChunkClaimAdded;

    private final BLibEventListenerHandle<BLibChunkClaimRemovedEvent> onChunkClaimRemoved;

    private final BLibEventListenerHandle<BLibChunkLoadEvent> onChunkLoad;

    private final BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave;

    private final BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload;

    private final BLibEventListenerContainer<BLibCommonSetupEvent> onCommonSetup;

    private final BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad;

    private final BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove;

    private final BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick;

    private final BLibEventListenerHandle<BLibFactionCreatedEvent> onFactionCreated;

    private final BLibEventListenerHandle<BLibFactionDataChangedEvent> onFactionDataChanged;

    private final BLibEventListenerHandle<BLibFactionMemberChangedEvent> onFactionMemberChanged;

    private final BLibEventListenerHandle<BLibFactionRelationshipChangedEvent> onFactionRelationshipChanged;

    private final BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove;

    private final BLibEventListenerHandle<BLibFactionsLoadedEvent> onFactionsLoaded;

    private final BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave;

    private final BLibEventListenerHandle<BLibPlayerAdvancementAwardEvent> onPlayerAdvancementAward;

    private final BLibNeoForgeEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity;

    private final BLibEventListenerHandle<BLibServerSaveEvent> onServerSave;

    private final BLibNeoForgeEventHandle<BLibTagsUpdatedEvent> onTagsUpdated;

    private final BLibNeoForgeEventHandle<BLibLevelTickEvent> postLevelTick;

    private final BLibNeoForgeEventHandle<BLibScreenInitEvent> postScreenInit;

    private final BLibNeoForgeEventHandle<BLibBlockBreakEvent> preBlockBreak;

    private final BLibNeoForgeEventHandle<BLibLevelTickEvent> preLevelTick;

    private final BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Started> serverStarted;

    private final BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Starting> serverStarting;

    private final BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped;

    private final BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping;

    private final List<Tuple2<PreparableReloadListener, PackType>> reloadListeners;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public BLibNeoForgeModContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = new HashMap<>();

        this.brewingRecipeData = new ArrayList<>();
        this.compostableData = new ArrayList<>();
        this.customRegistryEntries = new ArrayList<>();
        this.entityAttributeSupplierPairs = new ArrayList<>();
        this.entitySpawnDataEntries = new ArrayList<>();
        this.furnaceFuelData = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.onChunkClaimAdded = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_CLAIM_ADDED);
        this.onChunkClaimRemoved = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_CLAIM_REMOVED);
        this.onChunkLoad = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_LOAD);
        this.onChunkSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_SAVE);
        this.onChunkUnload = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_UNLOAD);
        this.onCommonSetup = BLibCommonSetupEvents.FACTORY.apply(mod);
        this.onEntityLoad = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_LOAD);
        this.onEntityRemove = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_REMOVE);
        this.onEntityTick = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_TICK);
        this.onFactionCreated = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_CREATED);
        this.onFactionDataChanged = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_DATA_CHANGED);
        this.onFactionMemberChanged = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_MEMBER_CHANGED);
        this.onFactionRelationshipChanged = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_RELATIONSHIP_CHANGED);
        this.onFactionRemove = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_REMOVE);
        this.onFactionsLoaded = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTIONS_LOADED);
        this.onLevelSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.LEVEL_SAVE);
        this.onPlayerAdvancementAward = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.PLAYER_ADVANCEMENT_AWARD);
        this.onPlayerStartTrackingEntity = BLibNeoForgePlayerTrackingEntityEvents.FACTORY.apply(mod);
        this.onServerSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.SERVER_SAVE);
        this.onTagsUpdated = BLibNeoForgeTagsUpdatedEvents.FACTORY.apply(mod);
        this.postLevelTick = BLibNeoForgeLevelTickEvents.POST_FACTORY.apply(mod);
        this.postScreenInit = BLibNeoForgeScreenInitEvents.POST_FACTORY.apply(mod);
        this.preBlockBreak = BLibNeoForgePlayerBlockBreakEvents.FACTORY.apply(mod);
        this.preLevelTick = BLibNeoForgeLevelTickEvents.PRE_FACTORY.apply(mod);
        this.serverStarted = BLibNeoForgeServerLifecycleEvents.STARTED_FACTORY.apply(mod);
        this.serverStarting = BLibNeoForgeServerLifecycleEvents.STARTING_FACTORY.apply(mod);
        this.serverStopped = BLibNeoForgeServerLifecycleEvents.STOPPED_FACTORY.apply(mod);
        this.serverStopping = BLibNeoForgeServerLifecycleEvents.STOPPING_FACTORY.apply(mod);
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

    public BLibEventListenerHandle<BLibChunkClaimAddedEvent> onChunkClaimAdded() {
        return onChunkClaimAdded;
    }

    public BLibEventListenerHandle<BLibChunkClaimRemovedEvent> onChunkClaimRemoved() {
        return onChunkClaimRemoved;
    }

    public BLibEventListenerHandle<BLibChunkLoadEvent> onChunkLoad() {
        return onChunkLoad;
    }

    public BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave() {
        return onChunkSave;
    }

    public BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload() {
        return onChunkUnload;
    }

    public BLibEventListenerContainer<BLibCommonSetupEvent> onCommonSetup() {
        return onCommonSetup;
    }

    public BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad() {
        return onEntityLoad;
    }

    public BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove() {
        return onEntityRemove;
    }

    public BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick() {
        return onEntityTick;
    }

    public BLibEventListenerHandle<BLibFactionCreatedEvent> onFactionCreated() {
        return onFactionCreated;
    }

    public BLibEventListenerHandle<BLibFactionDataChangedEvent> onFactionDataChanged() {
        return onFactionDataChanged;
    }

    public BLibEventListenerHandle<BLibFactionMemberChangedEvent> onFactionMemberChanged() {
        return onFactionMemberChanged;
    }

    public BLibEventListenerHandle<BLibFactionRelationshipChangedEvent> onFactionRelationshipChanged() {
        return onFactionRelationshipChanged;
    }

    public BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove() {
        return onFactionRemove;
    }

    public BLibEventListenerHandle<BLibFactionsLoadedEvent> onFactionsLoaded() {
        return onFactionsLoaded;
    }

    public BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave() {
        return onLevelSave;
    }

    public BLibEventListenerHandle<BLibPlayerAdvancementAwardEvent> onPlayerAdvancementAward() {
        return onPlayerAdvancementAward;
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

    public BLibNeoForgeEventHandle<BLibScreenInitEvent> postScreenInit() {
        return postScreenInit;
    }

    public BLibNeoForgeEventHandle<BLibBlockBreakEvent> preBlockBreak() {
        return preBlockBreak;
    }

    public BLibNeoForgeEventHandle<BLibLevelTickEvent> preLevelTick() {
        return preLevelTick;
    }

    public BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Started> onServerStarted() {
        return serverStarted;
    }

    public BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting() {
        return serverStarting;
    }

    public BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped() {
        return serverStopped;
    }

    public BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping() {
        return serverStopping;
    }

    public BLibEventListenerHandle<BLibServerSaveEvent> onServerSave() {
        return onServerSave;
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

    /* package-private */ List<Tuple2<PreparableReloadListener, PackType>> getReloadListeners() {
        return Collections.unmodifiableList(reloadListeners);
    }

    /* package-private */ List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return Collections.unmodifiableList(villagerTradeData);
    }

    /* package-private */ void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    /* package-private */ void registerBrewingRecipe(Holder<Potion> input, Supplier<? extends Item> ingredient, Holder<Potion> output) {
        brewingRecipeData.add(new Tuple3<>(input, ingredient, output));
    }

    /* package-private */ List<Tuple3<Holder<Potion>, Supplier<? extends Item>, Holder<Potion>>> getBrewingRecipeData() {
        return Collections.unmodifiableList(brewingRecipeData);
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

    /* package-private */ void registerReloadListener(PreparableReloadListener listener, PackType packType) {
        reloadListeners.add(new Tuple2<>(listener, packType));
    }

    /* package-private */ void registerVillagerTrade(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(holder, level, villagerTradeItemListings));
    }
}
