package com.blib.internal.client.dismemberment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.dismemberment.v1.LimbVisualsRegistry;

/**
 * Client-side resource-pack reload listener for {@code assets/<ns>/blib_limb_visuals/<entity_path>.json}. Each file
 * carries visual data for the limbs of one entity type — a map keyed by full limb id ({@code "blib:wolf_head": {...}})
 * so a single file can hold every limb belonging to that entity. The file's {@link ResourceLocation} (computed by the
 * vanilla loader from {@code <ns>/blib_limb_visuals/<entity_path>.json}) is interpreted as the entity-type id.
 * <p>
 * Mismatched halves (logic declared in {@code /data} but no entry here) leave the renderer with no rootBone / offsets /
 * scale to apply, which manifests as a blank limb fragment — the same graceful failure as a missing texture.
 */
public final class LimbVisualsLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbVisualsLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limb_visuals";

    /** Per-file shape: {@code { "visuals": { "<limb_id>": {LimbVisuals...}, ... } }}. */
    private record File(Map<ResourceLocation, LimbVisuals> visuals) {

        static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.unboundedMap(ResourceLocation.CODEC, LimbVisuals.CODEC).fieldOf("visuals").forGetter(File::visuals)
            ).apply(instance, File::new)
        );
    }

    public LimbVisualsLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonByPath, ResourceManager resourceManager, ProfilerFiller profiler) {
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbVisuals>>();
        for (var entry : jsonByPath.entrySet()) {
            var entityTypeId = entry.getKey();
            var parsed = File.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb visuals for {}: {}",
                    entityTypeId,
                    parsed.error().map(err -> err.message()).orElse("unknown error")
                );
                continue;
            }
            var perEntity = new LinkedHashMap<>(result.get().visuals());
            next.put(entityTypeId, perEntity);
        }
        LimbVisualsRegistry.replaceTier2(next);
    }
}
