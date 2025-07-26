package com.alien.common.data;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.GrowthStageRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import com.avp.AVP;

public class GrowthStageReloadListener extends SimpleJsonResourceReloadListener {

    public static final String DIRECTORY_NAME = "growth_stages";

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    public GrowthStageReloadListener() {
        super(GSON, DIRECTORY_NAME);
    }

    @Override
    protected void apply(
        @NotNull Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap,
        @NotNull ResourceManager resourceManager,
        @NotNull ProfilerFiller profilerFiller
    ) {
        GrowthStageRegistry.clear();

        for (var entry : resourceLocationJsonElementMap.entrySet()) {
            var id = entry.getKey();
            var jsonElement = entry.getValue();

            GrowthStage.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                .resultOrPartial(err -> AVP.LOGGER.error("Failed to parse GrowthStage {}: {}", id, err))
                .ifPresent(GrowthStageRegistry::register);
        }
    }
}
