package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.worldgen.v1.StructureTemplatePoolAccessor;
import com.blib.internal.mixin.MixinListPoolElement_Accessor;
import com.blib.internal.mixin.MixinSinglePoolElement_Accessor;
import com.blib.internal.mixin.MixinStructureTemplatePool_Accessor;

/**
 * Reads the integrated server's {@link Registries#TEMPLATE_POOL} and exposes the pool ↔ template-id graph for the
 * {@link com.blib.engine.ui.panel.jigsaw.PiecePalettePanel}'s pool filter. Like {@link JigsawPieceLibrary}, this is
 * dev-only and single-player only — uses {@code Minecraft.getSingleplayerServer().registryAccess()}.
 * <p>
 * Two layers of cache: a sorted list of pool ids for filter dropdown enumeration, and a
 * {@code poolId → Set<templateId>} map built by walking each pool's {@link StructurePoolElement}s and recursing through
 * {@link ListPoolElement}s. Both are cleared by {@link #invalidate} on workspace open so datapack reloads between
 * sessions are picked up.
 * <p>
 * Element-type handling:
 * <ul>
 * <li>{@link SinglePoolElement} — read its {@code template} field (Left side of the {@code Either}); skip if Right
 * (pre-resolved templates without an id, which datapack-defined pools never use).</li>
 * <li>{@link ListPoolElement} — recurse into the nested elements list.</li>
 * <li>Other element types ({@code FeaturePoolElement}, {@code EmptyPoolElement}) — skipped; they don't contribute
 * template ids.</li>
 * </ul>
 */
@ApiStatus.Internal
public final class JigsawPoolLibrary {

    private static List<ResourceLocation> cachedPoolIds;

    private static Map<ResourceLocation, Set<ResourceLocation>> cachedPoolToTemplates;

    private JigsawPoolLibrary() {}

    /** Sorted list of every loaded pool id (namespace + path). Empty if the integrated server isn't available. */
    public static List<ResourceLocation> listPoolIds() {
        if (cachedPoolIds != null) {
            return cachedPoolIds;
        }

        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return List.of();
        }

        var registry = server.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var ids = new ArrayList<>(registry.keySet());
        ids.sort(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath));
        cachedPoolIds = List.copyOf(ids);
        return cachedPoolIds;
    }

    /** Set of template ids referenced by the given pool. Empty for unknown pools. */
    public static Set<ResourceLocation> templateIdsInPool(ResourceLocation poolId) {
        return poolToTemplatesMap().getOrDefault(poolId, Set.of());
    }

    /**
     * Snapshot of one pool element for the editor panel — the template id, the weight assigned by the parent pool, and
     * the projection mode. Children of a {@link ListPoolElement} share the parent's weight slot in vanilla's roll, so
     * this representation flattens nested lists with the same parent weight per leaf.
     * <p>
     * {@code rawIndex} is the position in the pool's {@code rawTemplates} list of the *top-level* element this row
     * belongs to — used by edit packets so the server can locate the right entry to mutate. Set to {@code -1} for
     * leaves nested inside a {@code ListPoolElement} (the editor displays these but won't allow direct edits; editing
     * the parent list's weight via JSON is still required to change them).
     * <p>
     * Element types without a template id ({@code FeaturePoolElement}, {@code EmptyPoolElement}) are excluded — they
     * have no piece to inspect; the editor's element list is "what could spawn here" not "every pool entry".
     */
    public record PoolElementInfo(
        int rawIndex,
        ResourceLocation templateId,
        int weight,
        StructureTemplatePool.Projection projection
    ) {

        public boolean editable() {
            return rawIndex >= 0;
        }
    }

    /**
     * List every template-bearing element in {@code poolId} with its weight + projection. Walks the same element tree
     * as {@link #templateIdsInPool} but preserves per-element metadata. Built fresh on each call (no caching) — pools
     * are typically small (≤ ~50 elements) and the editor panel only calls this on pool selection change. Empty for
     * unknown pools or when the integrated server isn't available.
     */
    public static List<PoolElementInfo> elementsInPool(ResourceLocation poolId) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return List.of();
        }
        var registry = server.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(poolId);
        if (pool == null) {
            return List.of();
        }

        // Defensive copy: the live list could be mutated by a server-side edit packet between us starting iteration
        // and reading element data. Copy keeps our walk independent.
        var rawTemplates = List.copyOf(((MixinStructureTemplatePool_Accessor) (Object) pool).getElementCounts());
        var out = new ArrayList<PoolElementInfo>();
        for (var i = 0; i < rawTemplates.size(); i++) {
            var pair = rawTemplates.get(i);
            extractInfoFromElement(pair.getFirst(), pair.getSecond(), i, out);
        }
        return List.copyOf(out);
    }

    public static void invalidate() {
        cachedPoolIds = null;
        cachedPoolToTemplates = null;
    }

    private static Map<ResourceLocation, Set<ResourceLocation>> poolToTemplatesMap() {
        if (cachedPoolToTemplates != null) {
            return cachedPoolToTemplates;
        }

        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return Map.of();
        }

        var registry = server.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var result = new HashMap<ResourceLocation, Set<ResourceLocation>>();
        for (var entry : registry.entrySet()) {
            var poolId = entry.getKey().location();
            var templateIds = new HashSet<ResourceLocation>();
            extractTemplateIds(entry.getValue(), templateIds);
            result.put(poolId, Set.copyOf(templateIds));
        }
        cachedPoolToTemplates = Map.copyOf(result);
        return cachedPoolToTemplates;
    }

    private static void extractTemplateIds(StructureTemplatePool pool, Set<ResourceLocation> out) {
        var elements = ((StructureTemplatePoolAccessor) pool).getElements();
        for (var element : elements) {
            extractFromElement(element, out);
        }
    }

    private static void extractFromElement(StructurePoolElement element, Set<ResourceLocation> out) {
        if (element instanceof SinglePoolElement single) {
            // Left = ResourceLocation reference (the datapack-defined case). Right = a fully-materialized template
            // without a stable id — only used by code that builds pools programmatically with synthetic templates.
            // For the panel filter we only care about the id case.
            ((MixinSinglePoolElement_Accessor) (Object) single).blib$getTemplate().ifLeft(out::add);
        } else if (element instanceof ListPoolElement list) {
            for (var nested : ((MixinListPoolElement_Accessor) (Object) list).blib$getElements()) {
                extractFromElement(nested, out);
            }
        }
        // FeaturePoolElement / EmptyPoolElement contribute no template ids — skip silently.
    }

    /**
     * Sibling of {@link #extractFromElement} that emits {@link PoolElementInfo} per leaf, propagating the parent pool
     * entry's {@code weight} into each. List children inherit the parent list's weight (matching vanilla's roll
     * semantics: the whole list is one weighted slot in the parent pool); their rawIndex is set to {@code -1} so the
     * editor knows they're not directly addressable for in-place edits.
     */
    private static void extractInfoFromElement(StructurePoolElement element, int weight, int rawIndex, List<PoolElementInfo> out) {
        if (element instanceof SinglePoolElement single) {
            ((MixinSinglePoolElement_Accessor) (Object) single).blib$getTemplate()
                .ifLeft(id -> out.add(new PoolElementInfo(rawIndex, id, weight, single.getProjection())));
        } else if (element instanceof ListPoolElement list) {
            for (var nested : ((MixinListPoolElement_Accessor) (Object) list).blib$getElements()) {
                // Nested children share the parent's weight but lose direct addressability — flagged with -1.
                extractInfoFromElement(nested, weight, -1, out);
            }
        }
    }
}
