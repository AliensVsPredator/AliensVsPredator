package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reads the integrated server's {@link StructureTemplateManager} for the list of available structure templates and
 * caches lookups. Engine mode is dev-only and single-player only, so the singleplayer-server fast path is sufficient.
 * <p>
 * Two layers of cache:
 * <ul>
 * <li>{@link #cachedIds} — sorted list of every template id, recomputed on demand when the manager isn't seen yet or
 * when the user explicitly refreshes via {@link #invalidate}. Cheap to recompute (just a stream walk), but stable
 * enumeration matters for the panel's grid scroll position.</li>
 * <li>{@link #templateCache} — id → resolved {@link StructureTemplate}, populated lazily as the panel asks for
 * previews. Held permanently for the session so we don't re-decode the NBT each frame.</li>
 * </ul>
 */
@ApiStatus.Internal
public final class JigsawPieceLibrary {

    private static @Nullable List<ResourceLocation> cachedIds;

    private static final java.util.Map<ResourceLocation, StructureTemplate> templateCache = new java.util.HashMap<>();

    private JigsawPieceLibrary() {}

    /**
     * Lists every available template id, sorted by namespace then path so the panel layout is stable across reopens.
     * Returns an empty list when the integrated server isn't available (e.g. on the title screen).
     */
    public static List<ResourceLocation> listIds() {
        if (cachedIds != null) {
            return cachedIds;
        }

        var manager = manager();
        if (manager == null) {
            return List.of();
        }

        cachedIds = manager.listTemplates()
            .sorted(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath))
            .collect(Collectors.toUnmodifiableList());
        return cachedIds;
    }

    /** Resolve a template by id, caching the resolved instance for the session. Returns {@code null} on miss. */
    public static @Nullable StructureTemplate get(ResourceLocation id) {
        var cached = templateCache.get(id);
        if (cached != null) {
            return cached;
        }

        var manager = manager();
        if (manager == null) {
            return null;
        }

        Optional<StructureTemplate> resolved = manager.get(id);
        resolved.ifPresent(t -> templateCache.put(id, t));
        return resolved.orElse(null);
    }

    /**
     * Drop both caches. Called when the user opens the workspace fresh, or via a dedicated refresh button later — not
     * called automatically on every frame because listing all templates re-iterates the data resources.
     */
    public static void invalidate() {
        cachedIds = null;
        templateCache.clear();
    }

    private static @Nullable StructureTemplateManager manager() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        return server == null ? null : server.getStructureManager();
    }
}
