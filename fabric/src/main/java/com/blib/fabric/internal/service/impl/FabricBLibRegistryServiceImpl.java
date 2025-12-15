package com.blib.fabric.internal.service.impl;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibRegistryService;

@ApiStatus.Internal
public class FabricBLibRegistryServiceImpl implements BLibRegistryService {

    private final Map<BLibMod, BLibFabricModContainer> modToContainerMap;

    public FabricBLibRegistryServiceImpl() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        getModContainer(holder)
            .deferRegistration(holder, valueFactory);

        return holder;
    }

    @Override
    public void registerAzureLibIdentity(BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .deferAzureLibIdentityRegistration(holder);
    }

    @Override
    public void registerCompostable(
        BLibHolder<? extends ItemLike> holder,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        getModContainer(holder)
            .deferCompostableRegistration(holder, chance);
    }

    public void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .deferDecoratedPotPatternRegistration(path, holder);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .deferEntityAttributesRegistration(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .deferEntitySpawnDataRegistration(spawnData);
    }

    @Override
    public void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        getModContainer(holder)
            .deferFurnaceFuelRegistration(holder, burnTimeInTicks);
    }

    @Override
    public void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener) {
        var resourceLocation = mod.resources().createLocation(path);
        var adaptedListener = new IdentifiableResourceReloadListener() {

            @Override
            public ResourceLocation getFabricId() {
                return resourceLocation;
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(
                PreparationBarrier preparationBarrier,
                ResourceManager resourceManager,
                ProfilerFiller preparationsProfiler,
                ProfilerFiller reloadProfiler,
                Executor backgroundExecutor,
                Executor gameExecutor
            ) {
                return listener.reload(
                    preparationBarrier,
                    resourceManager,
                    preparationsProfiler,
                    reloadProfiler,
                    backgroundExecutor,
                    gameExecutor
                );
            }
        };

        ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(adaptedListener);
    }

    /* package-private */ void finalize(BLibMod mod) {
        getModContainer(mod)
            .runDeferredRegistrations();
    }

    private BLibFabricModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    private BLibFabricModContainer getModContainer(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibFabricModContainer(mod));
    }
}
