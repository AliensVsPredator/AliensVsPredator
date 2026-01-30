package com.blib.internal.client.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.internal.client.animation.primitive.AzBakedAnimations;
import com.blib.internal.client.model.AzResourceCache;
import com.blib.internal.common.io.ResourceFileLoader;
import com.blib.internal.common.io.util.JsonUtil;

public class AzBakedAnimationCache extends AzResourceCache {

    private static final AzBakedAnimationCache INSTANCE = new AzBakedAnimationCache();

    public static AzBakedAnimationCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedAnimations> bakedAnimations;

    private AzBakedAnimationCache() {
        this.bakedAnimations = new Object2ObjectOpenHashMap<>();
    }

    // TODO: Why is there no default animation file here?
    public CompletableFuture<Void> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager) {
        return loadResources(
            backgroundExecutor,
            resourceManager,
            "animations",
            // TODO: Process result here, use default animation fallback as necessary.
            resource -> ResourceFileLoader.loadObjectFromFile(JsonUtil.GEO_GSON, AzBakedAnimations.class, resource, resourceManager),
            // TODO: What if result is an error here?
            (resourceLocation, result) -> result.inspect(animations -> bakedAnimations.put(resourceLocation, animations))
        );
    }

    public @Nullable AzBakedAnimations getOrNull(ResourceLocation resourceLocation) {
        return bakedAnimations.get(resourceLocation);
    }
}
