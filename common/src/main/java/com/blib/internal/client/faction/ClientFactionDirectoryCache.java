package com.blib.internal.client.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.api.common.faction.v1.FactionPair;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.mod.common.network.packet.S2CFactionDirectoryPayload;

/**
 * Workspace-only client-side mirror of the server's faction directory: every faction's id / name / color / member count
 * / type id, plus the full pairwise relationship table. Populated by {@link S2CFactionDirectoryPayload}, sent on
 * workspace open and pushed proactively after any add/remove/relationship/metadata mutation. Read by the Faction
 * Browser (entries) and Diplomacy Matrix (relationships).
 * <p>
 * Lighter-weight surface than {@link ClientFactionCache} (which only mirrors {@code (id, name, color)} for the Xaero
 * territory-color path) — the directory adds member counts, type ids, and the full relationship table the Diplomacy
 * Matrix needs. Both caches happily coexist; the directory is the authoritative source for the workspace's panels and
 * is cleared on workspace close.
 */
@ApiStatus.Internal
public final class ClientFactionDirectoryCache {

    /**
     * Reused from the wire payload — the cache and the payload share the same shape, so re-declaring a parallel record
     * would be redundant. The Faction Browser reads {@link S2CFactionDirectoryPayload.FactionEntry} fields directly.
     */
    public static List<S2CFactionDirectoryPayload.FactionEntry> factionEntries() {
        return entries;
    }

    private static List<S2CFactionDirectoryPayload.FactionEntry> entries = List.of();

    private static final Map<ResourceLocation, S2CFactionDirectoryPayload.FactionEntry> entryById = new HashMap<>();

    private static final Map<FactionPair, RelationshipState> relationshipByPair = new HashMap<>();

    private ClientFactionDirectoryCache() {}

    /** Faction list, sorted by display name (case-insensitive). Empty until the first directory payload lands. */
    public static List<S2CFactionDirectoryPayload.FactionEntry> entries() {
        return entries;
    }

    public static @Nullable S2CFactionDirectoryPayload.FactionEntry get(ResourceLocation factionId) {
        return entryById.get(factionId);
    }

    /**
     * Look up the relationship between two factions, returning {@link RelationshipState#NEUTRAL} when no entry is
     * stored. Pair direction doesn't matter — the underlying map is keyed by {@link FactionPair} which lex-normalizes.
     */
    public static RelationshipState relationship(ResourceLocation a, ResourceLocation b) {
        return relationshipByPair.getOrDefault(new FactionPair(a, b), RelationshipState.NEUTRAL);
    }

    public static void apply(S2CFactionDirectoryPayload payload) {
        var sorted = new ArrayList<>(payload.factions());
        sorted.sort((x, y) -> String.CASE_INSENSITIVE_ORDER.compare(x.name(), y.name()));
        entries = Collections.unmodifiableList(sorted);
        entryById.clear();
        for (var e : sorted) {
            entryById.put(e.id(), e);
        }
        relationshipByPair.clear();
        var states = RelationshipState.values();
        for (var r : payload.relationships()) {
            var ord = r.stateOrdinal();
            var state = ord >= 0 && ord < states.length ? states[ord] : RelationshipState.NEUTRAL;
            relationshipByPair.put(new FactionPair(r.factionA(), r.factionB()), state);
        }
    }

    public static void clear() {
        entries = List.of();
        entryById.clear();
        relationshipByPair.clear();
    }
}
