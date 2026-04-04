package com.blib.fabric.internal.service.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.event.v1.handle.BLibGlobalOnlyEventHandle;
import com.blib.api.common.event.v1.handle.impl.BLibEventListenerContainer;
import com.blib.api.common.event.v1.impl.BLibCommonSetupEvents;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.fabric.internal.event.impl.BLibFabricLevelTickEvents;
import com.blib.fabric.internal.event.impl.BLibFabricPlayerBlockBreakEvents;
import com.blib.fabric.internal.event.impl.BLibFabricPlayerTrackingEntityEvents;
import com.blib.fabric.internal.event.impl.BLibFabricServerLifecycleEvents;
import com.blib.fabric.internal.event.impl.BLibFabricTagsUpdatedEvents;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.registry.util.BLibRegistrationUtil;

@ApiStatus.Internal
public class BLibFabricModContainer {

    private final BLibMod mod;

    private final List<NetworkHandler<?>> clientBoundPacketHandlers;

    private final List<Runnable> deferredBrewingRecipeRegistrations;

    private final List<Runnable> deferredCompostableRegistrations;

    private final List<Runnable> deferredEntityAttributeRegistrations;

    private final List<Runnable> deferredEntitySpawnDataRegistrations;

    private final List<Runnable> deferredFurnaceFuelRegistrations;

    private final Map<Registry<?>, List<Runnable>> deferredRegistrations;

    private final List<Runnable> deferredVillagerTradeRegistrations;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final BLibEventListenerHandle<BLibChunkClaimAddedEvent> onChunkClaimAdded;

    private final BLibEventListenerHandle<BLibChunkClaimRemovedEvent> onChunkClaimRemoved;

    private final BLibEventListenerHandle<BLibChunkLoadEvent> onChunkLoad;

    private final BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave;

    private final BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload;

    private final BLibEventListenerContainer<BLibCommonSetupEvent> onCommonSetup;

    private final BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad;

    private final BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove;

    private final BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick;

    private final BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove;

    private final BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave;

    private final BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity;

    private final BLibEventListenerHandle<BLibServerSaveEvent> onServerSave;

    private final BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated;

    private final BLibEventHandle<BLibLevelTickEvent> postLevelTick;

    private final BLibEventHandle<BLibBlockBreakEvent> preBlockBreak;

    private final BLibEventHandle<BLibLevelTickEvent> preLevelTick;

    private final BLibEventHandle<BLibServerLifecycleEvent.Started> serverStarted;

    private final BLibEventHandle<BLibServerLifecycleEvent.Starting> serverStarting;

    private final BLibEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped;

    private final BLibEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping;

    public BLibFabricModContainer(BLibMod mod) {
        this.mod = mod;
        this.clientBoundPacketHandlers = new ArrayList<>();
        this.deferredBrewingRecipeRegistrations = new ArrayList<>();
        this.deferredCompostableRegistrations = new ArrayList<>();
        this.deferredEntityAttributeRegistrations = new ArrayList<>();
        this.deferredEntitySpawnDataRegistrations = new ArrayList<>();
        this.deferredFurnaceFuelRegistrations = new ArrayList<>();
        this.deferredRegistrations = new HashMap<>();
        this.deferredVillagerTradeRegistrations = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.onChunkClaimAdded = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_CLAIM_ADDED);
        this.onChunkClaimRemoved = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_CLAIM_REMOVED);
        this.onChunkLoad = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_LOAD);
        this.onChunkSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_SAVE);
        this.onChunkUnload = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.CHUNK_UNLOAD);
        this.onCommonSetup = BLibCommonSetupEvents.FACTORY.apply(mod);
        this.onEntityLoad = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_LOAD);
        this.onEntityRemove = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_REMOVE);
        this.onEntityTick = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.ENTITY_TICK);
        this.onFactionRemove = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.FACTION_REMOVE);
        this.onLevelSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.LEVEL_SAVE);
        this.onPlayerStartTrackingEntity = BLibFabricPlayerTrackingEntityEvents.FACTORY.apply(mod);
        this.onServerSave = new BLibGlobalOnlyEventHandle<>(mod, BLibGlobalEvents.SERVER_SAVE);
        this.onTagsUpdated = BLibFabricTagsUpdatedEvents.FACTORY.apply(mod);
        this.postLevelTick = BLibFabricLevelTickEvents.POST_FACTORY.apply(mod);
        this.preBlockBreak = BLibFabricPlayerBlockBreakEvents.FACTORY.apply(mod);
        this.preLevelTick = BLibFabricLevelTickEvents.PRE_FACTORY.apply(mod);
        this.serverStarted = BLibFabricServerLifecycleEvents.STARTED_FACTORY.apply(mod);
        this.serverStarting = BLibFabricServerLifecycleEvents.STARTING_FACTORY.apply(mod);
        this.serverStopped = BLibFabricServerLifecycleEvents.STOPPED_FACTORY.apply(mod);
        this.serverStopping = BLibFabricServerLifecycleEvents.STOPPING_FACTORY.apply(mod);
    }

    public List<NetworkHandler<?>> getClientBoundPacketHandlers() {
        return clientBoundPacketHandlers;
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

    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup() {
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

    public BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove() {
        return onFactionRemove;
    }

    public BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave() {
        return onLevelSave;
    }

    public BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return onPlayerStartTrackingEntity;
    }

    public BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated() {
        return onTagsUpdated;
    }

    public BLibEventHandle<BLibLevelTickEvent> postLevelTick() {
        return postLevelTick;
    }

    public BLibEventHandle<BLibBlockBreakEvent> preBlockBreak() {
        return preBlockBreak;
    }

    public BLibEventHandle<BLibLevelTickEvent> preLevelTick() {
        return preLevelTick;
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Started> onServerStarted() {
        return serverStarted;
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting() {
        return serverStarting;
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped() {
        return serverStopped;
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping() {
        return serverStopping;
    }

    public BLibEventListenerHandle<BLibServerSaveEvent> onServerSave() {
        return onServerSave;
    }

    /* package-private */ <T> void deferRegistration(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        deferredRegistrations.compute(holder.getBackingRegistry(), ($1, list) -> {
            var nonNullList = list == null ? new ArrayList<Runnable>() : list;

            nonNullList.add(() -> {
                var object = valueFactory.get();
                var resourceLocation = holder.getResourceLocation();
                var registry = holder.getBackingRegistry();

                if (object instanceof PoiType poiType) {
                    // We have to do special handling for PoiType registration on the Fabric side, since Fabric wants
                    // Poi registrations to go through their "PointOfInterestHelper" type.
                    registerPoiType(resourceLocation, poiType);
                }

                Registry.registerForHolder(registry, resourceLocation, object);
            });

            return nonNullList;
        });
    }

    /* package-private */ void deferEntityAttributesRegistration(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        deferredEntityAttributeRegistrations.add(
            () -> FabricDefaultAttributeRegistry.register(holder.get(), attributeSupplierBuilderSupplier.get())
        );
    }

    /* package-private */ <T extends Mob> void deferEntitySpawnDataRegistration(BLibEntitySpawnData<T> spawnData) {
        deferredEntitySpawnDataRegistrations.add(() -> {
            var spawnSettings = spawnData.getConfigData().spawnSettings();
            var entityType = spawnData.getEntityTypeHolder().get();

            if (!spawnData.isPlacementDisabled()) {
                var placement = spawnData.getPlacementData().type();
                var heightMap = spawnData.getPlacementData().heightmapType();
                var spawnPredicate = spawnData.getPlacementData().spawnPredicate();

                SpawnPlacements.register(entityType, placement, heightMap, spawnPredicate);
            }

            if (!spawnData.isConfigDisabled()) {
                Predicate<BiomeSelectionContext> biomeSelector = biomeSelectionContext -> biomeSelectionContext.hasTag(
                    spawnData.getConfigData().biomeTagKey()
                );
                var spawnGroup = entityType.getCategory();
                var weight = spawnSettings.weight();
                var minGroupSize = spawnSettings.minGroupSize();
                var maxGroupSize = spawnSettings.maxGroupSize();

                BiomeModifications.addSpawn(biomeSelector, spawnGroup, entityType, weight, minGroupSize, maxGroupSize);
            }
        });
    }

    /* package-private */ void deferBrewingRecipeRegistration(
        Holder<Potion> input,
        Supplier<? extends Item> ingredient,
        Holder<Potion> output
    ) {
        deferredBrewingRecipeRegistrations.add(
            () -> FabricBrewingRecipeRegistryBuilder.BUILD.register(
                builder -> builder.registerPotionRecipe(input, net.minecraft.world.item.crafting.Ingredient.of(ingredient.get()), output)
            )
        );
    }

    /* package-private */ void deferCompostableRegistration(BLibHolder<? extends ItemLike> holder, float chance) {
        deferredCompostableRegistrations.add(() -> CompostingChanceRegistry.INSTANCE.add(holder.get(), chance));
    }

    /* package-private */ void deferFurnaceFuelRegistration(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        deferredFurnaceFuelRegistrations.add(() -> FuelRegistry.INSTANCE.add(holder.get(), burnTimeInTicks));
    }

    /* package-private */ void deferVillagerTradeRegistration(
        BLibHolder<VillagerProfession> holder,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        deferredVillagerTradeRegistrations.add(
            () -> TradeOfferHelper.registerVillagerOffers(holder.get(), level, factories -> factories.addAll(villagerTradeItemListings))
        );
    }

    /* package-private */ void finalizeRegistrations() {
        // Variables are present here for source of truth concerns.
        var orderedRegistries = BLibRegistrationUtil.VANILLA_REGISTRATION_ORDER;
        var orderSensitiveRegistrySet = Set.copyOf(orderedRegistries);

        // Run order-sensitive registries, first.
        orderedRegistries.forEach(this::runRegistrationsFor);
        // Run all other registries after order-sensitive registries.
        deferredRegistrations.keySet()
            .stream()
            .filter(registry -> !orderSensitiveRegistrySet.contains(registry))
            .forEach(this::runRegistrationsFor);
        // Run brewing recipe registrations after primary registries are ran.
        deferredBrewingRecipeRegistrations.forEach(Runnable::run);
        // Run compostable registrations after primary registries are ran.
        deferredCompostableRegistrations.forEach(Runnable::run);
        // Run furnace fuel registrations after primary registries are ran.
        deferredFurnaceFuelRegistrations.forEach(Runnable::run);

        // Run entity attribute registrations after primary registries are ran.
        deferredEntityAttributeRegistrations.forEach(Runnable::run);
        // Run entity spawn data registrations after primary registries registrations.
        deferredEntitySpawnDataRegistrations.forEach(Runnable::run);
        // Run villager trade registrations after primary registries are ran.
        deferredVillagerTradeRegistrations.forEach(Runnable::run);

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> literalArgumentBuilders
                .forEach(dispatcher::register)
        );

        onCommonSetup.getListeners()
            .forEach(BLibCommonSetupEvent::invoke);
    }

    /* package-private */ void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    /* package-private */ <T extends CustomPacketPayload> void registerNetworkHandler(NetworkHandler<T> networkHandler) {
        clientBoundPacketHandlers.add(networkHandler);
    }

    private void runRegistrationsFor(Registry<?> registry) {
        deferredRegistrations.getOrDefault(registry, List.of()).forEach(Runnable::run);
    }

    private void registerPoiType(ResourceLocation resourceLocation, PoiType poiType) {
        PointOfInterestHelper.register(resourceLocation, poiType.maxTickets(), poiType.validRange(), poiType.matchingStates());
        // Immediately get the holder or throw. This should be safe to do since we registered the PoiType in the last
        // line. This is necessary because PointOfInterestHelper doesn't return back a holder after registration.
        BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(resourceLocation).orElseThrow();
    }
}
