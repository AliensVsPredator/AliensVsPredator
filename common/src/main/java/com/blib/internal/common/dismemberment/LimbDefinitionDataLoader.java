package com.blib.internal.common.dismemberment;

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
import java.util.List;
import java.util.Map;

import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

/**
 * Server-side data-pack reload listener for {@code data/<ns>/blib_limbs/<entity_path>.json} files. Each file describes
 * the logical limbs of an entity type (id, category, fatal); the file's {@link ResourceLocation} (computed by the
 * vanilla loader from {@code <ns>/blib_limbs/<entity_path>.json}) is interpreted as the entity-type id. Visual fields
 * live in a separate client-side registry; spawn-offset functions live in {@code SpawnFunctionRegistry} and are
 * resolved per-id from {@link LimbDefinition#CODEC}.
 * <p>
 * Atomically swaps tier 2 of {@link LimbDefinitionRegistry} on each reload. Java-registered (tier-1) entries remain.
 */
public final class LimbDefinitionDataLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbDefinitionDataLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limbs";

    /** Per-file shape: {@code { "limbs": [ {LimbDefinition...}, ... ] }}. */
    private record File(List<LimbDefinition> limbs) {

        static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                LimbDefinition.CODEC.listOf().fieldOf("limbs").forGetter(File::limbs)
            ).apply(instance, File::new)
        );
    }

    public LimbDefinitionDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonByPath, ResourceManager resourceManager, ProfilerFiller profiler) {
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var entry : jsonByPath.entrySet()) {
            var entityTypeId = entry.getKey();
            var parsed = File.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb definitions for {}: {}",
                    entityTypeId,
                    parsed.error().map(err -> err.message()).orElse("unknown error")
                );
                continue;
            }
            var perEntity = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            for (var def : result.get().limbs()) {
                perEntity.put(def.id(), def);
            }
            next.put(entityTypeId, perEntity);
        }
        LimbDefinitionRegistry.replaceTier2(next);
    }
}
