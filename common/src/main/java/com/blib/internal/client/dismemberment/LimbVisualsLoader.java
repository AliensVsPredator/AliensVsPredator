package com.blib.internal.client.dismemberment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.dismemberment.v1.LimbVisualsRegistry;

/**
 * Client-side resource-pack reload listener for {@code assets/<ns>/blib_limb_visuals/<path>.json}. Files whose path
 * resolves to a registered entity type carry visual data for that entity's limbs. Files whose path is not an entity
 * type are reusable templates; entity files can inherit from them with {@code parent}.
 * <p>
 * Mismatched halves (logic declared in {@code /data} but no entry here) leave the renderer with no rootBone / offsets /
 * scale to apply, which manifests as a blank limb fragment — the same graceful failure as a missing texture.
 */
public final class LimbVisualsLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbVisualsLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limb_visuals";

    /** Per-file shape: {@code { "parent": "<template_id>", "visuals": { "<limb_id>": {LimbVisuals...}, ... } }}. */
    private record File(Optional<ResourceLocation> parent, Map<ResourceLocation, LimbVisuals> visuals) {

        static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(File::parent),
                Codec
                    .unboundedMap(ResourceLocation.CODEC, LimbVisuals.CODEC)
                    .optionalFieldOf("visuals", Map.of())
                    .forGetter(File::visuals)
            ).apply(instance, File::new)
        );
    }

    public LimbVisualsLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonByPath, ResourceManager resourceManager, ProfilerFiller profiler) {
        var parsedFiles = new LinkedHashMap<ResourceLocation, File>();
        var templates = new LinkedHashMap<ResourceLocation, File>();
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbVisuals>>();
        for (var entry : jsonByPath.entrySet()) {
            var fileId = entry.getKey();
            var parsed = File.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb visuals for {}: {}",
                    fileId,
                    parsed.error().map(err -> err.message()).orElse("unknown error")
                );
                continue;
            }
            parsedFiles.put(fileId, result.get());
            if (!isEntityType(fileId)) {
                templates.put(fileId, result.get());
            }
        }

        var resolvedTemplates = new HashMap<ResourceLocation, Map<ResourceLocation, LimbVisuals>>();
        for (var entry : parsedFiles.entrySet()) {
            var entityTypeId = entry.getKey();
            if (!isEntityType(entityTypeId)) {
                continue;
            }
            var file = entry.getValue();
            var perEntity = new LinkedHashMap<ResourceLocation, LimbVisuals>();
            file.parent().ifPresent(parent -> perEntity.putAll(resolveTemplate(parent, templates, resolvedTemplates, new HashSet<>())));
            perEntity.putAll(file.visuals());
            if (!perEntity.isEmpty()) {
                next.put(entityTypeId, perEntity);
            }
        }
        LimbVisualsRegistry.replaceTier2(next);
    }

    private static Map<ResourceLocation, LimbVisuals> resolveTemplate(
        ResourceLocation templateId,
        Map<ResourceLocation, File> templates,
        Map<ResourceLocation, Map<ResourceLocation, LimbVisuals>> resolvedTemplates,
        Set<ResourceLocation> resolving
    ) {
        var cached = resolvedTemplates.get(templateId);
        if (cached != null) {
            return cached;
        }

        var template = templates.get(templateId);
        if (template == null) {
            LOGGER.warn("Limb visual template '{}' does not exist.", templateId);
            return Map.of();
        }
        if (!resolving.add(templateId)) {
            LOGGER.warn("Limb visual template '{}' has a cyclic parent chain.", templateId);
            return Map.of();
        }

        var out = new LinkedHashMap<ResourceLocation, LimbVisuals>();
        template.parent().ifPresent(parent -> out.putAll(resolveTemplate(parent, templates, resolvedTemplates, resolving)));
        out.putAll(template.visuals());
        resolving.remove(templateId);

        var resolved = Collections.unmodifiableMap(new LinkedHashMap<>(out));
        resolvedTemplates.put(templateId, resolved);
        return resolved;
    }

    private static boolean isEntityType(ResourceLocation id) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(id).isPresent();
    }
}
