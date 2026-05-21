package com.blib.internal.client.dismemberment;

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
public final class LimbVisualsLoader extends SimplePreparableReloadListener<Map<ResourceLocation, LimbVisualsLoader.File>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbVisualsLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib_limb_visuals";

    /** Per-file shape: {@code { "replace": false, "parent": "<template_id>", "visuals": { "<limb_id>": {LimbVisuals...}, ... } }}. */
    private record DecodedFile(Optional<ResourceLocation> parent, Map<ResourceLocation, LimbVisuals> visuals, boolean replace) {

        static final Codec<DecodedFile> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(DecodedFile::parent),
                Codec
                    .unboundedMap(ResourceLocation.CODEC, LimbVisuals.CODEC)
                    .optionalFieldOf("visuals", Map.of())
                    .forGetter(DecodedFile::visuals),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(DecodedFile::replace)
            ).apply(instance, DecodedFile::new)
        );
    }

    record File(Optional<ResourceLocation> parent, boolean parentSpecified, Map<ResourceLocation, LimbVisuals> visuals, boolean replace) {

        File {
            visuals = Collections.unmodifiableMap(new LinkedHashMap<>(visuals));
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
        var next = new HashMap<ResourceLocation, Map<ResourceLocation, LimbVisuals>>();
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

        var resolvedTemplates = new HashMap<ResourceLocation, Map<ResourceLocation, LimbVisuals>>();
        for (var templateId : templates.keySet()) {
            resolveTemplate(templateId, templates, resolvedTemplates, new HashSet<>());
        }
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
        LimbVisualsRegistry.replaceTier2(next, parents, resolvedTemplates, templateParents);
    }

    private static File parseFile(ResourceLocation fileId, ResourceLocation fileLocation, Resource resource) {
        try (var reader = resource.openAsReader()) {
            JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
            var parsed = DecodedFile.CODEC.parse(JsonOps.INSTANCE, json);
            var result = parsed.result();
            if (result.isEmpty()) {
                LOGGER.warn(
                    "Failed to parse limb visuals for {} from {} in pack {}: {}",
                    fileId,
                    fileLocation,
                    resource.sourcePackId(),
                    parsed.error().map(err -> err.message()).orElse("unknown error")
                );
                return null;
            }
            var decoded = result.get();
            var parentSpecified = json != null && json.isJsonObject() && json.getAsJsonObject().has("parent");
            return new File(decoded.parent(), parentSpecified, decoded.visuals(), decoded.replace());
        } catch (IllegalArgumentException | IOException | JsonParseException e) {
            LOGGER.warn("Failed to parse limb visuals for {} from {} in pack {}", fileId, fileLocation, resource.sourcePackId(), e);
            return null;
        }
    }

    private static File mergeFiles(File current, File patch) {
        if (current == null || patch.replace()) {
            return patch;
        }

        var visuals = new LinkedHashMap<ResourceLocation, LimbVisuals>();
        visuals.putAll(current.visuals());
        visuals.putAll(patch.visuals());
        return new File(
            patch.parentSpecified() ? patch.parent() : current.parent(),
            current.parentSpecified() || patch.parentSpecified(),
            visuals,
            true
        );
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
