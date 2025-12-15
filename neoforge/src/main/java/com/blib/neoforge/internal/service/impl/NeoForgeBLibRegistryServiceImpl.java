package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple4;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibRegistryService;
import com.blib.neoforge.data.BLibNeoForgeCompostableDataMapProvider;
import com.blib.neoforge.data.BLibNeoForgeEntitySpawnDataProvider;
import com.blib.neoforge.data.BLibNeoForgeFurnaceFuelDataMapProvider;

@ApiStatus.Internal
public class NeoForgeBLibRegistryServiceImpl implements BLibRegistryService {

    private final Map<BLibMod, BLibNeoForgeModContainer> modToContainerMap;

    public NeoForgeBLibRegistryServiceImpl() {
        this.modToContainerMap = new ConcurrentHashMap<>();
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
    public void registerAzureLibIdentity(BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .registerAzureLibIdentity(holder);
    }

    @Override
    public void registerCompostable(BLibHolder<? extends ItemLike> holder, float chance, boolean villagersCanCompost, boolean replace) {
        getModContainer(holder)
            .registerCompostable(new Tuple4<>(holder, chance, villagersCanCompost, replace));
    }

    @Override
    public void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .registerDecoratedPotPattern(path, holder);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .registerEntityAttributes(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .registerEntitySpawnData(spawnData);
    }

    @Override
    public void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        getModContainer(holder)
            .registerFurnaceFuel(new Tuple2<>(holder, burnTimeInTicks));
    }

    @Override
    public void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener) {
        getModContainer(mod)
            .registerReloadListener(listener);
    }

    /* package-private */ void finalize(BLibMod mod, IEventBus eventBus) {
        getModContainer(mod)
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(eventBus));

        eventBus.<EntityAttributeCreationEvent>addListener(event -> onRegisterEntityAttributes(mod, event));
        eventBus.<RegisterSpawnPlacementsEvent>addListener(event -> onRegisterEntitySpawnPlacements(mod, event));

        eventBus.<FMLCommonSetupEvent>addListener(
            event -> {
                getModContainer(mod)
                    .getAzureLibIdentityEntries()
                    .forEach(holder -> AzIdentityRegistry.register(holder.get()));

                getModContainer(mod)
                    .getDeferredDecoratedPotPatternRegistrations()
                    .forEach(Runnable::run);
            }
        );

        eventBus.<GatherDataEvent>addListener(event -> {
            var generator = event.getGenerator();
            var packOutput = generator.getPackOutput();
            var lookupProvider = event.getLookupProvider();
            var run = event.includeServer();

            generator.addProvider(run, new BLibNeoForgeCompostableDataMapProvider(mod, packOutput, lookupProvider));
            generator.addProvider(run, new BLibNeoForgeEntitySpawnDataProvider(mod, lookupProvider));
            generator.addProvider(run, new BLibNeoForgeFurnaceFuelDataMapProvider(mod, packOutput, lookupProvider));
        });

        NeoForge.EVENT_BUS.<AddReloadListenerEvent>addListener(
            event -> getModContainer(mod).getReloadListeners().forEach(event::addListener)
        );
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

    private BLibNeoForgeModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    public BLibNeoForgeModContainer getModContainer(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibNeoForgeModContainer(mod));
    }
}
