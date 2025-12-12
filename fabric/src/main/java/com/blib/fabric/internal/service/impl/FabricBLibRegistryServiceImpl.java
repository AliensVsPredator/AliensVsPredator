package com.blib.fabric.internal.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.internal.common.registry.BLibRegistries;
import com.blib.internal.service.BLibRegistryService;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricBLibRegistryServiceImpl implements BLibRegistryService {

    // TODO: These fields need to be per-mod.

    private final List<Runnable> deferredEntityAttributeRegistrations;

    private final List<Runnable> deferredEntitySpawnDataRegistrations;

    private final Map<Registry<?>, List<Runnable>> deferredRegistrations;

    public FabricBLibRegistryServiceImpl() {
        this.deferredEntityAttributeRegistrations = new ArrayList<>();
        this.deferredEntitySpawnDataRegistrations = new ArrayList<>();
        this.deferredRegistrations = new HashMap<>();
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
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

        return holder;
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        deferredEntityAttributeRegistrations.add(
            () -> FabricDefaultAttributeRegistry.register(holder.get(), attributeSupplierBuilderSupplier.get())
        );
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
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

    public void finalize(BLibMod mod) {
        BLibRegistries.REGISTRATION_ORDER.forEach(this::runRegistrationsFor);
        // Run entity attribute registrations after primary registries are ran.
        deferredEntityAttributeRegistrations.forEach(Runnable::run);
        // Run entity spawn data registrations after entity attribute registrations.
        deferredEntitySpawnDataRegistrations.forEach(Runnable::run);
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
