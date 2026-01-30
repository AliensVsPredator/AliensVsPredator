package com.blib.internal.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.blib.internal.common.io.FileLoader;
import com.blib.mod.BLib;

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
                var defaultModelLocation = BLib.MOD.resources().createLocation("geo/default_model.geo.json");
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
