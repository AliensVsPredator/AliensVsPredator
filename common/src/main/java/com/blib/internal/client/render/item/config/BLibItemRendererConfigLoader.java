package com.blib.internal.client.render.item.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.blib.api.client.render.v1.item.BLibItemTransforms;

/**
 * Loads {@code assets/<ns>/blib/item_renderers/<id>.json} files into {@link BLibItemRendererConfigs}. Two phases:
 * <ol>
 * <li>Parse every JSON file into a {@link RawItemRendererConfig}, indexed by resource id.</li>
 * <li>For each raw entry, walk its parent chain (cycle-detected) and deep-merge field-by-field. Per-perspective merging
 * for the idle/blocking transform maps so a child can override a single context without losing the rest of the parent's
 * authored perspectives. Entries that end up structurally incomplete (missing
 * {@code model}/{@code texture}/{@code bone} after resolution) are silently template-only — skipped from the resolved
 * snapshot rather than treated as errors.</li>
 * </ol>
 */
public final class BLibItemRendererConfigLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibItemRendererConfigLoader.class);

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "blib/item_renderers";

    public BLibItemRendererConfigLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonByPath, ResourceManager resourceManager, ProfilerFiller profiler) {
        var raw = new HashMap<ResourceLocation, RawItemRendererConfig>();

        for (var entry : jsonByPath.entrySet()) {
            var id = entry.getKey();
            var parsed = RawItemRendererConfig.CODEC.parse(JsonOps.INSTANCE, entry.getValue());

            parsed.resultOrPartial(err -> LOGGER.warn("Failed to parse item-renderer config {}: {}", id, err))
                .ifPresent(config -> raw.put(id, config));
        }

        var resolved = new LinkedHashMap<ResourceLocation, ResolvedItemRendererConfig>();

        for (var id : raw.keySet()) {
            var merged = resolveChain(id, raw);

            if (merged == null) {
                continue;
            }

            if (merged.model() == null || merged.texture() == null || merged.bone() == null) {
                // Template-only: a config can legitimately omit these fields if it's meant to be inherited via `parent`
                // rather than bound to an item. Don't warn — that would noise up the log for every intentional
                // template.
                continue;
            }

            resolved.put(
                id,
                new ResolvedItemRendererConfig(
                    merged.model(),
                    merged.texture(),
                    merged.bone(),
                    merged.idleTransforms() != null ? merged.idleTransforms() : BLibItemTransforms.builder().build(),
                    merged.blockingTransforms()
                )
            );
        }

        BLibItemRendererConfigs.replace(resolved);
    }

    /**
     * Walk the parent chain from {@code id} root-ward, accumulating fields. Cycle detection bails the affected branch
     * with a logged warning so resolution doesn't infinite-loop; the partial merge collected before the cycle is
     * discarded (returning null) since silently dropping fields would hide the misconfiguration.
     */
    private static @Nullable RawItemRendererConfig resolveChain(
        ResourceLocation id,
        Map<ResourceLocation, RawItemRendererConfig> raw
    ) {
        var visited = new LinkedHashSet<ResourceLocation>();
        var chainTopDown = new java.util.ArrayList<RawItemRendererConfig>();
        var cursor = id;

        while (cursor != null) {
            if (!visited.add(cursor)) {
                LOGGER.warn("Cycle detected in item-renderer config parent chain at {} (chain: {})", cursor, visited);
                return null;
            }

            var node = raw.get(cursor);

            if (node == null) {
                LOGGER.warn("Item-renderer config {} references missing parent {}", id, cursor);
                return null;
            }

            chainTopDown.add(0, node);
            cursor = node.parent();
        }

        // Fold from oldest ancestor down to the child: each step lets the lower entry's non-null fields win.
        var acc = chainTopDown.get(0);

        for (var i = 1; i < chainTopDown.size(); i++) {
            acc = merge(acc, chainTopDown.get(i));
        }

        return acc;
    }

    private static RawItemRendererConfig merge(RawItemRendererConfig parent, RawItemRendererConfig child) {
        return new RawItemRendererConfig(
            child.parent(),
            child.model() != null ? child.model() : parent.model(),
            child.texture() != null ? child.texture() : parent.texture(),
            child.bone() != null ? child.bone() : parent.bone(),
            mergeTransforms(parent.idleTransforms(), child.idleTransforms()),
            mergeTransforms(parent.blockingTransforms(), child.blockingTransforms())
        );
    }

    /**
     * Per-perspective merge — child wins on each present context key, parent fills the rest. Empty objects in the child
     * are NOT a way to "clear" a perspective: the codec only emits keys with content, so a missing context just means
     * "inherit" rather than "override to identity." If an author needs identity, they can set explicit zero/one values
     * on that perspective.
     */
    private static @Nullable BLibItemTransforms mergeTransforms(
        @Nullable BLibItemTransforms parent,
        @Nullable BLibItemTransforms child
    ) {
        if (parent == null) {
            return child;
        }

        if (child == null) {
            return parent;
        }

        var builder = BLibItemTransforms.builder();

        for (var ctx : ItemDisplayContext.values()) {
            var overlay = child.getOrNull(ctx);

            if (overlay != null) {
                builder.set(ctx, overlay);
                continue;
            }

            var base = parent.getOrNull(ctx);

            if (base != null) {
                builder.set(ctx, base);
            }
        }

        var wall = child.getFixedWallOrNull();

        if (wall == null) {
            wall = parent.getFixedWallOrNull();
        }

        if (wall != null) {
            builder.fixedWall(wall);
        }

        return builder.build();
    }

}
