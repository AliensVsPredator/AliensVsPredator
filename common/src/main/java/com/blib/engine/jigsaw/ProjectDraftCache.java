package com.blib.engine.jigsaw;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.mod.common.network.packet.DraftPoolElement;

/**
 * Client-side cache of pool drafts pushed by the server via {@code S2CPoolDraftPayload}. The Pool Editor reads from
 * this in place of the live registry so the user sees the project's authoritative state — registry stays frozen at
 * pre-reload values until they run Reload Project.
 * <p>
 * Cleared on Reload Project (so the next read re-fetches from a freshly-imported registry through the request packet)
 * and on engine workspace close. Keyed by pool id only; the active project name is implicit (changing projects clears
 * the cache via {@code ProjectSession.clear()} or workspace close).
 */
@ApiStatus.Internal
public final class ProjectDraftCache {

    private static final Map<ResourceLocation, List<DraftPoolElement>> ELEMENTS_BY_POOL = new HashMap<>();

    private ProjectDraftCache() {}

    /** Replace the cached element list for {@code poolId} with {@code elements}. Called from the S2C handler. */
    public static void update(ResourceLocation poolId, List<DraftPoolElement> elements) {
        ELEMENTS_BY_POOL.put(poolId, List.copyOf(elements));
    }

    /** Returns the cached element list for {@code poolId}, or {@code null} if no draft is cached. */
    public static @Nullable List<DraftPoolElement> get(ResourceLocation poolId) {
        return ELEMENTS_BY_POOL.get(poolId);
    }

    public static void clear() {
        ELEMENTS_BY_POOL.clear();
    }

    /**
     * Convenience: convert a {@link DraftPoolElement} to a {@link JigsawPoolLibrary.PoolElementInfo} for the editor.
     */
    public static JigsawPoolLibrary.PoolElementInfo toPoolElementInfo(DraftPoolElement draft) {
        var values = StructureTemplatePool.Projection.values();
        var ord = draft.projectionOrdinal();
        var projection = ord >= 0 && ord < values.length ? values[ord] : values[0];
        return new JigsawPoolLibrary.PoolElementInfo(draft.rawIndex(), draft.templateId(), draft.weight(), projection);
    }
}
