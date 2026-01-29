package com.blib.azurelib.common.model.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.cache.AzResourceCache;
import com.blib.azurelib.common.loading.FileLoader;
import com.blib.azurelib.common.loading.json.raw.Model;
import com.blib.azurelib.common.loading.object.GeometryTree;
import com.blib.azurelib.common.model.AzBakedModel;
import com.blib.azurelib.common.model.factory.registry.AzBakedModelFactoryRegistry;

public class AzBakedModelCache extends AzResourceCache {

    private static final AzBakedModelCache INSTANCE = new AzBakedModelCache();

    public static AzBakedModelCache getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, AzBakedModel> bakedModels;

    private AzBakedModelCache() {
        this.bakedModels = new Object2ObjectOpenHashMap<>();
    }

    public CompletableFuture<Void> loadModels(Executor backgroundExecutor, ResourceManager resourceManager) {
        return loadResources(backgroundExecutor, resourceManager, "geo", resource -> {
            Model model = FileLoader.loadModelFile(resource, resourceManager);

            if (model == null) {
                var defaultModelLocation = AzureLib.modResource("geo/default_model.geo.json");
                model = FileLoader.loadModelFile(defaultModelLocation, resourceManager);
                var defaultBaked = AzBakedModelFactoryRegistry
                    .getForNamespace(resource.getNamespace())
                    .constructGeoModel(GeometryTree.fromModel(model));

                AzBakedModel.setDefault(defaultBaked);
            }

            return AzBakedModelFactoryRegistry.getForNamespace(resource.getNamespace())
                .constructGeoModel(GeometryTree.fromModel(model));
        }, bakedModels::put);
    }

    public @Nullable AzBakedModel getNullable(ResourceLocation resourceLocation) {
        return bakedModels.get(resourceLocation);
    }
}
