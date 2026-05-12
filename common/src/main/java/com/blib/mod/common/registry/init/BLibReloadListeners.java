package com.blib.mod.common.registry.init;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.api.BLibAPI;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.registry.v1.impl.BLibReloadListenerRegistry;
import com.blib.internal.client.animation.cache.AzBakedAnimationCache;
import com.blib.internal.client.dismemberment.LimbVisualsLoader;
import com.blib.internal.client.model.AzBakedModelCache;
import com.blib.internal.common.dismemberment.LimbDefinitionDataLoader;
import com.blib.mod.BLib;

public class BLibReloadListeners {

    private static final BLibReloadListenerRegistry REGISTRY = BLib.MOD.registries().createReloadListenerRegistry();

    public static void initialize() {
        REGISTRY.register("azurelib_cache", BLibReloadListeners::reload, PackType.CLIENT_RESOURCES);
        REGISTRY.register("blib_limbs", new LimbDefinitionDataLoader(), PackType.SERVER_DATA);
        // Client-side visuals reload listener is only relevant in client environments — the registry it populates has
        // no server-side counterpart. Guarding the registration avoids referencing client-only classes on a dedicated
        // server when the listener constructor is invoked at mod init.
        if (BLibAPI.getDistributionType() == DistributionEnvironmentType.CLIENT) {
            REGISTRY.register("blib_limb_visuals", new LimbVisualsLoader(), PackType.CLIENT_RESOURCES);
        }
    }

    private static CompletableFuture<Void> reload(
        PreparableReloadListener.PreparationBarrier stage,
        ResourceManager resourceManager,
        ProfilerFiller preparationsProfiler,
        ProfilerFiller reloadProfiler,
        Executor backgroundExecutor,
        Executor gameExecutor
    ) {
        return CompletableFuture
            .allOf(
                AzBakedAnimationCache.getInstance().loadAnimations(backgroundExecutor, resourceManager),
                AzBakedModelCache.getInstance().loadModels(backgroundExecutor, resourceManager)
            )
            .thenCompose(stage::wait)
            .thenAcceptAsync(empty -> {}, gameExecutor);
    }
}
