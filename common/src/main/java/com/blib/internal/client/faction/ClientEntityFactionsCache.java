package com.blib.internal.client.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SRequestEntityFactionsPayload;
import com.blib.mod.common.network.packet.S2CEntityFactionsPayload;

/**
 * Reverse-lookup cache: which factions does a given entity UUID belong to. Populated by
 * {@link S2CEntityFactionsPayload} in response to {@link C2SRequestEntityFactionsPayload}, with proactive pushes from
 * the server whenever an add / remove member mutation touches a UUID this player has previously asked about.
 * <p>
 * Two consumers in the engine workspace:
 * <ul>
 * <li>{@code DetailsPanel}'s Inspector "Factions" section — renders the cached list, or "(loading…)" if absent.</li>
 * <li>{@code FactionManagePopup} — uses the cached list to draw check marks next to factions the entity already belongs
 * to.</li>
 * </ul>
 * Both call {@link #ensureRequested(UUID)} before rendering so the first frame after a fresh entity selection kicks off
 * the request, and an internal {@code REQUESTED} set keeps subsequent renders from re-sending. Workspace close clears
 * everything.
 */
@ApiStatus.Internal
public final class ClientEntityFactionsCache {

    private static final Map<UUID, List<ResourceLocation>> ENTRIES = new HashMap<>();

    private static final Set<UUID> REQUESTED = new HashSet<>();

    private ClientEntityFactionsCache() {}

    /**
     * Cached faction list for this entity, or {@code null} if the request hasn't replied yet (or was never sent).
     * Callers distinguish "loading" (returns null) from "no factions" (returns empty list).
     */
    public static @Nullable List<ResourceLocation> get(UUID entityUuid) {
        return ENTRIES.get(entityUuid);
    }

    /**
     * Fire the request if this UUID hasn't been asked yet. Idempotent: subsequent calls for the same UUID don't
     * re-send. Subsequent renders should see the populated entry via {@link #get}, or keep waiting on the reply.
     */
    public static void ensureRequested(UUID entityUuid) {
        if (REQUESTED.add(entityUuid)) {
            BLib.MOD.networking().sendToServer(new C2SRequestEntityFactionsPayload(entityUuid));
        }
    }

    public static void apply(S2CEntityFactionsPayload payload) {
        ENTRIES.put(payload.memberUuid(), List.copyOf(payload.factionIds()));
        // Cache hit guaranteed for future ensureRequested calls; keep the UUID in REQUESTED so we never re-send
        // unless explicitly invalidated.
        REQUESTED.add(payload.memberUuid());
    }

    public static void clear() {
        ENTRIES.clear();
        REQUESTED.clear();
    }
}
