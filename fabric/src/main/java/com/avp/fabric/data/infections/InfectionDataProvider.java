package com.avp.fabric.data.infections;

import com.alien.common.data.InfectionReloadListener;
import com.alien.common.model.lifecycle.infection.Infection;
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

public abstract class InfectionDataProvider implements DataProvider {

    private final FabricDataOutput output;

    private final Map<String, Infection> infectionsByName;

    protected InfectionDataProvider(FabricDataOutput output) {
        this.output = output;
        this.infectionsByName = new HashMap<>();
    }

    protected abstract void generate();

    // TODO: Name shouldn't be provided by data generator.
    public void add(String name, Infection infection) {
        infectionsByName.put(name, infection);
    }

    @Override
    public final @NotNull CompletableFuture<?> run(CachedOutput cached) {
        generate();

        var pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, InfectionReloadListener.DIRECTORY_NAME);

        var futures = infectionsByName.entrySet()
            .stream()
            .map(entry -> {
                var name = entry.getKey();
                var infection = entry.getValue();
                var id = AVPResources.location(name);

                var filePath = pathProvider.json(id);
                var jsonElement = Infection.CODEC.encodeStart(JsonOps.INSTANCE, infection)
                    .getOrThrow();

                return DataProvider.saveStable(cached, jsonElement, filePath);
            });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public final @NotNull String getName() {
        return "Infections";
    }
}
