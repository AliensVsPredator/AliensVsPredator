package com.avp.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lib.common.gameplay.gene.GeneBonusData;
import com.lib.common.registry.GeneBonusDataRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import com.avp.AVP;

public class GeneBonusDataReloadListener extends SimpleJsonResourceReloadListener {

    public static final String DIRECTORY_NAME = "gene_bonus_data";

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    public GeneBonusDataReloadListener() {
        super(GSON, DIRECTORY_NAME);
    }

    @Override
    protected void apply(
        @NotNull Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap,
        @NotNull ResourceManager resourceManager,
        @NotNull ProfilerFiller profilerFiller
    ) {
        GeneBonusDataRegistry.clear();

        for (var entry : resourceLocationJsonElementMap.entrySet()) {
            var resourceLocation = entry.getKey();
            var jsonElement = entry.getValue();

            GeneBonusData.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                .resultOrPartial(err -> AVP.LOGGER.error("Failed to parse GeneBonusData {}: {}", resourceLocation, err))
                .ifPresent(GeneBonusDataRegistry::register);
        }
    }
}
