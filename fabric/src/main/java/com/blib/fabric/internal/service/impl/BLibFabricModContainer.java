package com.blib.fabric.internal.service.impl;

import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.common.BLibDecoratedPotPatternCache;
import com.blib.internal.common.registry.util.BLibRegistrationUtil;

@ApiStatus.Internal
public class BLibFabricModContainer {

    private final BLibMod mod;

    private final List<NetworkHandler<?>> clientBoundPacketHandlers;

    private final List<Runnable> deferredAzureLibIdentityRegistrations;

    private final List<Runnable> deferredCompostableRegistrations;

    private final List<Runnable> deferredDecoratedPotPatternRegistrations;

    private final List<Runnable> deferredEntityAttributeRegistrations;

    private final List<Runnable> deferredEntitySpawnDataRegistrations;

    private final List<Runnable> deferredFurnaceFuelRegistrations;

    private final Map<Registry<?>, List<Runnable>> deferredRegistrations;

    private final List<Runnable> deferredVillagerTradeRegistrations;

    public BLibFabricModContainer(BLibMod mod) {
        this.mod = mod;
        this.clientBoundPacketHandlers = new ArrayList<>();
        this.deferredAzureLibIdentityRegistrations = new ArrayList<>();
        this.deferredCompostableRegistrations = new ArrayList<>();
        this.deferredDecoratedPotPatternRegistrations = new ArrayList<>();
        this.deferredEntityAttributeRegistrations = new ArrayList<>();
        this.deferredEntitySpawnDataRegistrations = new ArrayList<>();
        this.deferredFurnaceFuelRegistrations = new ArrayList<>();
        this.deferredRegistrations = new HashMap<>();
        this.deferredVillagerTradeRegistrations = new ArrayList<>();
    }

    public List<NetworkHandler<?>> getClientBoundPacketHandlers() {
        return clientBoundPacketHandlers;
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

    /* package-private */ void finalizeRegistrations() {
        // Run primary registries.
        BLibRegistrationUtil.REGISTRATION_ORDER.forEach(this::runRegistrationsFor);
        // Run AzureLib identity registrations after primary registries are ran.
        deferredAzureLibIdentityRegistrations.forEach(Runnable::run);
        // Run compostable registrations after primary registries are ran.
        deferredCompostableRegistrations.forEach(Runnable::run);
        // Run decorated pot pattern registrations after primary registries are ran.
        deferredDecoratedPotPatternRegistrations.forEach(Runnable::run);
        // Run furnace fuel registrations after primary registries are ran.
        deferredFurnaceFuelRegistrations.forEach(Runnable::run);

        // Run entity attribute registrations after primary registries are ran.
        deferredEntityAttributeRegistrations.forEach(Runnable::run);
        // Run entity spawn data registrations after primary registries registrations.
        deferredEntitySpawnDataRegistrations.forEach(Runnable::run);
        // Run villager trade registrations after primary registries are ran.
        deferredVillagerTradeRegistrations.forEach(Runnable::run);
    }

    /* package-private */ void deferAzureLibIdentityRegistration(BLibHolder<? extends Item> holder) {
        deferredAzureLibIdentityRegistrations.add(() -> AzIdentityRegistry.register(holder.get()));
    }

    /* package-private */ void deferCompostableRegistration(BLibHolder<? extends ItemLike> holder, float chance) {
        deferredCompostableRegistrations.add(() -> CompostingChanceRegistry.INSTANCE.add(holder.get(), chance));
    }

    /* package-private */ void deferDecoratedPotPatternRegistration(String path, BLibHolder<? extends Item> holder) {
        deferredDecoratedPotPatternRegistrations.add(
            () -> BLibDecoratedPotPatternCache.put(holder.get(), mod.resources().createKey(Registries.DECORATED_POT_PATTERN, path))
        );
    }

    /* package-private */ void deferFurnaceFuelRegistration(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        deferredFurnaceFuelRegistrations.add(() -> FuelRegistry.INSTANCE.add(holder.get(), burnTimeInTicks));
    }

    /* package-private */ <T extends CustomPacketPayload> void registerNetworkHandler(NetworkHandler<T> networkHandler) {
        clientBoundPacketHandlers.add(networkHandler);
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

    private void runRegistrationsFor(Registry<?> registry) {
        deferredRegistrations.getOrDefault(registry, List.of()).forEach(Runnable::run);
    }

    private <T> @NotNull Holder<T> registerPoiType(ResourceLocation resourceLocation, PoiType poiType) {
        PointOfInterestHelper.register(resourceLocation, poiType.maxTickets(), poiType.validRange(), poiType.matchingStates());
        // Immediately get the holder or throw. This should be safe to do since we registered the PoiType in the last
        // line. This is necessary because PointOfInterestHelper doesn't return back a holder after registration.
        @SuppressWarnings("unchecked")
        var registeredHolder = (Holder<T>) BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(resourceLocation).orElseThrow();
        return registeredHolder;
    }
}
