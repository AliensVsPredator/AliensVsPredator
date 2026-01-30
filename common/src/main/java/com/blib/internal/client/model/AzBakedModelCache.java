package com.blib.internal.client.model;

import com.just.core.functional.result.Err;
import com.just.core.functional.result.Ok;
import com.just.core.functional.result.Result;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.internal.common.io.ResourceFileLoader;
import com.blib.internal.common.io.util.JsonUtil;
import com.blib.mod.BLib;

public class AzBakedModelCache extends AzResourceCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzBakedModelCache.class);

    private static final AzBakedModelCache INSTANCE = new AzBakedModelCache();

    private static final String MODELS_PATH = "geo";

    private static final String DEFAULT_MODEL_PATH = MODELS_PATH + "/default_model.geo.json";

    private static final ResourceLocation DEFAULT_MODEL_RESOURCE_LOCATION = BLib.MOD.resources().createLocation(DEFAULT_MODEL_PATH);

    public static AzBakedModelCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedModel> bakedModels;

    private AzBakedModelCache() {
        this.bakedModels = new Object2ObjectOpenHashMap<>();
    }

    public CompletableFuture<Void> loadModels(Executor backgroundExecutor, ResourceManager resourceManager) {
        var defaultBakedModelOption = loadDefaultBakedModel(resourceManager);

        return loadResources(
            backgroundExecutor,
            resourceManager,
            MODELS_PATH,
            resourceLocation -> {
                if (Objects.equals(DEFAULT_MODEL_RESOURCE_LOCATION, resourceLocation)) {
                    return Result.err(null);
                }

                return loadAndBakeModel(resourceManager, resourceLocation, defaultBakedModelOption);
            },
            this::tryInsertBakedModelResult
        );
    }

    public @Nullable AzBakedModel getOrNull(ResourceLocation resourceLocation) {
        return bakedModels.get(resourceLocation);
    }

    private void tryInsertBakedModelResult(
        ResourceLocation resourceLocation,
        Result<AzBakedModel, ResourceFileLoader.ObjectLoadError> result
    ) {
        result.inspect(bakedModel -> bakedModels.put(resourceLocation, bakedModel));
    }

    private Result<AzBakedModel, ResourceFileLoader.ObjectLoadError> loadDefaultBakedModel(ResourceManager resourceManager) {
        var defaultModelResult = ResourceFileLoader.loadObjectFromFile(
            JsonUtil.GEO_GSON,
            Model.class,
            DEFAULT_MODEL_RESOURCE_LOCATION,
            resourceManager
        );

        // TODO: Clean this up when Result.toOption becomes available.
        var defaultBakedModelResult = defaultModelResult.map(
            model -> bakeModel(DEFAULT_MODEL_RESOURCE_LOCATION, defaultModelResult.unwrap())
        );

        switch (defaultBakedModelResult) {
            case Err<AzBakedModel, ResourceFileLoader.ObjectLoadError> err -> err.unwrapErr()
                .log(LOGGER, Level.ERROR, "Failed to load default model '{}'.", "", DEFAULT_MODEL_RESOURCE_LOCATION);
            // TODO: This is semantically misleading at best and brittle at worst. "Defaults" should never change.
            case Ok<AzBakedModel, ResourceFileLoader.ObjectLoadError> ignored -> AzBakedModel.setDefault(defaultBakedModelResult.unwrap());
        }

        return defaultBakedModelResult;
    }

    private Result<AzBakedModel, ResourceFileLoader.ObjectLoadError> loadAndBakeModel(
        ResourceManager resourceManager,
        ResourceLocation resourceLocation,
        Result<AzBakedModel, ResourceFileLoader.ObjectLoadError> defaultBakedModelResult
    ) {
        var result = ResourceFileLoader.loadObjectFromFile(JsonUtil.GEO_GSON, Model.class, resourceLocation, resourceManager);

        if (result.isErr()) {
            var error = result.unwrapErr();
            var postMessage = defaultBakedModelResult.isOk() ? "Using default baked model as a fallback." : "";

            error.log(LOGGER, Level.WARN, "Failed to load model '{}'.", postMessage, resourceLocation);

            return defaultBakedModelResult;
        }

        var model = result.unwrap();

        return Result.ok(bakeModel(resourceLocation, model));
    }

    private AzBakedModel bakeModel(ResourceLocation resource, Model defaultModel) {
        return AzBakedModelFactoryRegistry
            .getForNamespace(resource.getNamespace())
            .constructGeoModel(GeometryTree.fromModel(defaultModel));
    }
}
