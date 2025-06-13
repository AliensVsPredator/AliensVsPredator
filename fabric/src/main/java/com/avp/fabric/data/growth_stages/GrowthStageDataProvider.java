package com.avp.fabric.data.growth_stages;

import com.alien.common.data.GrowthStageReloadListener;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.avp.AVPResources;

public abstract class GrowthStageDataProvider implements DataProvider {

    private final FabricDataOutput output;

    private final Map<String, GrowthStage> growthStagesByName;

    protected GrowthStageDataProvider(FabricDataOutput output) {
        this.output = output;
        this.growthStagesByName = new HashMap<>();
    }

    protected abstract void generate();

    // TODO: Name shouldn't be provided by data generator.
    public void add(String name, GrowthStage growthStage) {
        growthStagesByName.put(name, growthStage);
    }

    @Override
    public final @NotNull CompletableFuture<?> run(CachedOutput cached) {
        generate();

        var pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, GrowthStageReloadListener.DIRECTORY_NAME);

        var futures = growthStagesByName.entrySet()
            .stream()
            .map(entry -> {
                var name = entry.getKey();
                var growthStage = entry.getValue();
                var id = AVPResources.location(name);

                var filePath = pathProvider.json(id);
                var jsonElement = GrowthStage.CODEC.encodeStart(JsonOps.INSTANCE, growthStage)
                    .getOrThrow();

                return DataProvider.saveStable(cached, jsonElement, filePath);
            });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public final @NotNull String getName() {
        return "Growth Stages";
    }
}
