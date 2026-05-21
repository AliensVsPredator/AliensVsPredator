package com.blib.internal.common.dismemberment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
public final class LimbDefinitionDataLoader extends SimplePreparableReloadListener<Map<ResourceLocation, LimbDefinitionDataLoader.File>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbDefinitionDataLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limbs";

    /** Per-file shape: {@code { "replace": false, "parent": "<template_id>", "limbs": [ {LimbDefinition...}, ... ] }}. */
    private record DecodedFile(Optional<ResourceLocation> parent, List<LimbDefinition> limbs, boolean replace) {

        static final Codec<DecodedFile> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(DecodedFile::parent),
                LimbDefinition.CODEC.listOf().optionalFieldOf("limbs", List.of()).forGetter(DecodedFile::limbs),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(DecodedFile::replace)
            ).apply(instance, DecodedFile::new)
        );
    }

    record File(Optional<ResourceLocation> parent, boolean parentSpecified, List<LimbDefinition> limbs, boolean replace) {

        File {
            limbs = List.copyOf(limbs);
        }
    }

    @Override
    protected Map<ResourceLocation, File> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        var converter = FileToIdConverter.json(DIRECTORY);
        var files = new LinkedHashMap<ResourceLocation, File>();
        for (var entry : converter.listMatchingResourceStacks(resourceManager).entrySet()) {
            var fileLocation = entry.getKey();
            var fileId = converter.fileToId(fileLocation);
            File merged = null;
            for (var resource : entry.getValue()) {
                var parsed = parseFile(fileId, fileLocation, resource);
                if (parsed != null) {
                    merged = mergeFiles(merged, parsed);
                }
            }
            if (merged != null) {
                files.put(fileId, merged);
            }
        }
        return files;
    }

    @Override
    protected void apply(Map<ResourceLocation, File> parsedFiles, ResourceManager resourceManager, ProfilerFiller profiler) {
        var templates = new LinkedHashMap<ResourceLocation, File>();
        var parents = new LinkedHashMap<ResourceLocation, ResourceLocation>();
        var templateParents = new LinkedHashMap<ResourceLocation, ResourceLocation>();
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var entry : parsedFiles.entrySet()) {
            var fileId = entry.getKey();
            var file = entry.getValue();
            if (!isEntityType(fileId)) {
                templates.put(fileId, file);
                file.parent().ifPresent(parent -> templateParents.put(fileId, parent));
            } else {
                file.parent().ifPresent(parent -> parents.put(fileId, parent));
            }
        }

        var resolvedTemplates = new HashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var templateId : templates.keySet()) {
            resolveTemplate(templateId, templates, resolvedTemplates, new HashSet<>());
        }
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
        LimbDefinitionRegistry.replaceTier2(next, parents, resolvedTemplates, templateParents);
    }

    private static File parseFile(ResourceLocation fileId, ResourceLocation fileLocation, Resource resource) {
        try (var reader = resource.openAsReader()) {
            JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
            var parsed = DecodedFile.CODEC.parse(JsonOps.INSTANCE, json);
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb definitions for {} from {} in pack {}: {}",
                    fileId,
                    fileLocation,
                    resource.sourcePackId(),
                    parsed.error().map(err -> err.message()).orElse("unknown error")
                );
                return null;
            }
            var decoded = result.get();
            var parentSpecified = json != null && json.isJsonObject() && json.getAsJsonObject().has("parent");
            return new File(decoded.parent(), parentSpecified, decoded.limbs(), decoded.replace());
        } catch (IllegalArgumentException | IOException | JsonParseException e) {
            LOGGER.warn("Failed to parse limb definitions for {} from {} in pack {}", fileId, fileLocation, resource.sourcePackId(), e);
            return null;
        }
    }

    private static File mergeFiles(File current, File patch) {
        if (current == null || patch.replace()) {
            return patch;
        }

        var byLimb = new LinkedHashMap<ResourceLocation, LimbDefinition>();
        addDefinitions(byLimb, current.limbs());
        addDefinitions(byLimb, patch.limbs());
        return new File(
            patch.parentSpecified() ? patch.parent() : current.parent(),
            current.parentSpecified() || patch.parentSpecified(),
            List.copyOf(byLimb.values()),
            true
        );
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
