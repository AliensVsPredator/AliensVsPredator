package com.blib.internal.common.dismemberment;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

/**
 * Server-side data-pack reload listener for {@code data/<ns>/blib_limbs/<path>.json} files. Files whose path resolves
 * to a registered entity type describe that entity's logical limbs (id, category, fatal, random pose options). Files
 * whose path is not an entity type are reusable templates; entity files can inherit from them with {@code parent}.
 * Visual fields live in a separate client-side registry; spawn-offset functions live in {@code SpawnFunctionRegistry}
 * and are resolved per-id from {@link LimbDefinition#CODEC}.
 * <p>
 * Atomically swaps tier 2 of {@link LimbDefinitionRegistry} on each reload. Java-registered (tier-1) entries remain.
 */
public final class LimbDefinitionDataLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbDefinitionDataLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limbs";

    /** Per-file shape: {@code { "parent": "<template_id>", "limbs": [ {LimbDefinition...}, ... ] }}. */
    private record File(Optional<ResourceLocation> parent, List<LimbDefinition> limbs) {

        static final Codec<File> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(File::parent),
                LimbDefinition.CODEC.listOf().optionalFieldOf("limbs", List.of()).forGetter(File::limbs)
            ).apply(instance, File::new)
        );
    }

    public LimbDefinitionDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonByPath, ResourceManager resourceManager, ProfilerFiller profiler) {
        var parsedFiles = new LinkedHashMap<ResourceLocation, File>();
        var templates = new LinkedHashMap<ResourceLocation, File>();
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var entry : jsonByPath.entrySet()) {
            var fileId = entry.getKey();
            var parsed = File.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb definitions for {}: {}",
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

        var resolvedTemplates = new HashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var entry : parsedFiles.entrySet()) {
            var entityTypeId = entry.getKey();
            if (!isEntityType(entityTypeId)) {
                continue;
            }
            var file = entry.getValue();
            var perEntity = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            file.parent().ifPresent(parent -> perEntity.putAll(resolveTemplate(parent, templates, resolvedTemplates, new HashSet<>())));
            addDefinitions(perEntity, file.limbs());
            if (!perEntity.isEmpty()) {
                next.put(entityTypeId, perEntity);
            }
        }
        LimbDefinitionRegistry.replaceTier2(next);
    }

    private static Map<ResourceLocation, LimbDefinition> resolveTemplate(
        ResourceLocation templateId,
        Map<ResourceLocation, File> templates,
        Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> resolvedTemplates,
        Set<ResourceLocation> resolving
    ) {
        var cached = resolvedTemplates.get(templateId);
        if (cached != null) {
            return cached;
        }

        var template = templates.get(templateId);
        if (template == null) {
            LOGGER.warn("Limb definition template '{}' does not exist.", templateId);
            return Map.of();
        }
        if (!resolving.add(templateId)) {
            LOGGER.warn("Limb definition template '{}' has a cyclic parent chain.", templateId);
            return Map.of();
        }

        var out = new LinkedHashMap<ResourceLocation, LimbDefinition>();
        template.parent().ifPresent(parent -> out.putAll(resolveTemplate(parent, templates, resolvedTemplates, resolving)));
        addDefinitions(out, template.limbs());
        resolving.remove(templateId);

        var resolved = Collections.unmodifiableMap(new LinkedHashMap<>(out));
        resolvedTemplates.put(templateId, resolved);
        return resolved;
    }

    private static void addDefinitions(Map<ResourceLocation, LimbDefinition> out, List<LimbDefinition> definitions) {
        for (var definition : definitions) {
            out.put(definition.id(), definition);
        }
    }

    private static boolean isEntityType(ResourceLocation id) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(id).isPresent();
    }
}
