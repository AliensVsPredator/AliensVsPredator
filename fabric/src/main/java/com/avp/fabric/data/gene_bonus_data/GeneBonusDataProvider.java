package com.avp.fabric.data.gene_bonus_data;

import com.avp.common.data.GeneBonusDataReloadListener;
import com.lib.common.gameplay.gene.GeneBonusData;
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

public abstract class GeneBonusDataProvider implements DataProvider {

    private final FabricDataOutput output;

    private final Map<String, GeneBonusData> geneBonusDataByName;

    protected GeneBonusDataProvider(FabricDataOutput output) {
        this.output = output;
        this.geneBonusDataByName = new HashMap<>();
    }

    protected abstract void generate();

    // TODO: Name shouldn't be provided by data generator.
    public void add(String name, GeneBonusData geneBonusData) {
        geneBonusDataByName.put(name, geneBonusData);
    }

    @Override
    public final @NotNull CompletableFuture<?> run(CachedOutput cached) {
        generate();

        var pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, GeneBonusDataReloadListener.DIRECTORY_NAME);

        var futures = geneBonusDataByName.entrySet()
            .stream()
            .map(entry -> {
                var name = entry.getKey();
                var geneBonusData = entry.getValue();
                var id = AVPResources.location(name);

                var filePath = pathProvider.json(id);
                var jsonElement = GeneBonusData.CODEC.encodeStart(JsonOps.INSTANCE, geneBonusData)
                    .getOrThrow();

                return DataProvider.saveStable(cached, jsonElement, filePath);
            });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public final @NotNull String getName() {
        return "Gene Bonus Data";
    }
}
