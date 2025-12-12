package com.blib.neoforge.internal.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.internal.service.BLibRegistryService;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class NeoForgeBLibRegistryServiceImpl implements BLibRegistryService {

    private final Map<BLibMod, NeoForgeBLibModContainer> modToContainerMap;

    public NeoForgeBLibRegistryServiceImpl() {
        modToContainerMap = new ConcurrentHashMap<>();
    }

    public void finalize(BLibMod mod, IEventBus eventBus) {
        getModContainer(mod)
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(eventBus));

        eventBus.<EntityAttributeCreationEvent>addListener(event -> onRegisterEntityAttributes(mod, event));
        eventBus.<RegisterSpawnPlacementsEvent>addListener(event -> onRegisterEntitySpawnPlacements(mod, event));
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        var blibRegistry = holder.getRegistry();
        var modContainer = getModContainer(blibRegistry.getMod());
        var backingRegistry = blibRegistry.getBackingRegistry();
        @SuppressWarnings("unchecked")
        var deferredRegister = (DeferredRegister<T>) modContainer.getDeferredRegister(backingRegistry);

        if (deferredRegister == null) {
            throw new IllegalArgumentException("Unhandled registry: " + backingRegistry);
        }

        return deferredRegister.register(holder.getPath(), valueFactory);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .registerEntityAttribute(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .registerEntitySpawnData(spawnData);
    }

    private void onRegisterEntityAttributes(BLibMod mod, EntityAttributeCreationEvent event) {
        getModContainer(mod).getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.v1().get(), pair.v2().get().build()));
    }

    private void onRegisterEntitySpawnPlacements(BLibMod mod, RegisterSpawnPlacementsEvent event) {
        getModContainer(mod).getEntitySpawnDataEntries()
            .forEach(spawnData -> {
                if (spawnData.isPlacementDisabled()) {
                    return;
                }

                @SuppressWarnings("unchecked")
                var entityType = (EntityType<Mob>) spawnData.getEntityTypeHolder().get();
                var placementData = spawnData.getPlacementData();
                var placement = placementData.type();
                var heightMap = placementData.heightmapType();
                @SuppressWarnings("unchecked")
                var spawnPredicate = (SpawnPlacements.SpawnPredicate<Mob>) placementData.spawnPredicate();

                event.register(
                    entityType,
                    placement,
                    heightMap,
                    spawnPredicate,
                    RegisterSpawnPlacementsEvent.Operation.AND
                );
            });
    }

    private NeoForgeBLibModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    private NeoForgeBLibModContainer getModContainer(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new NeoForgeBLibModContainer(mod));
    }
}
