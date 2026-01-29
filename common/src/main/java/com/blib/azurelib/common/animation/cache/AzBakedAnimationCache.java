package com.blib.azurelib.common.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.azurelib.common.animation.primitive.AzBakedAnimations;
import com.blib.azurelib.common.cache.AzResourceCache;
import com.blib.azurelib.common.loading.FileLoader;

public class AzBakedAnimationCache extends AzResourceCache {

    private static final AzBakedAnimationCache INSTANCE = new AzBakedAnimationCache();

    public static AzBakedAnimationCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedAnimations> bakedAnimations;

    private AzBakedAnimationCache() {
        this.bakedAnimations = new Object2ObjectOpenHashMap<>();
    }

    public CompletableFuture<Void> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager) {
        return loadResources(
            backgroundExecutor,
            resourceManager,
            "animations",
            resource -> FileLoader.loadAzAnimationsFile(resource, resourceManager),
            bakedAnimations::put
        );
    }

    public @Nullable AzBakedAnimations getNullable(ResourceLocation resourceLocation) {
        return bakedAnimations.get(resourceLocation);
    }
}
