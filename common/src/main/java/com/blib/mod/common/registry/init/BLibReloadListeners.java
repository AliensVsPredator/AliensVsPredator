package com.blib.mod.common.registry.init;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.api.common.registry.v1.impl.BLibReloadListenerRegistry;
import com.blib.azurelib.common.animation.cache.AzBakedAnimationCache;
import com.blib.azurelib.common.model.cache.AzBakedModelCache;
import com.blib.mod.BLib;

public class BLibReloadListeners {

    private static final BLibReloadListenerRegistry REGISTRY = BLib.MOD.registries().createReloadListenerRegistry();

    public static void initialize() {
        REGISTRY.register("azurelib_cache", BLibReloadListeners::reload, PackType.CLIENT_RESOURCES);
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
