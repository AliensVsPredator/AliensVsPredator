package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.blib.api.common.faction.v1.FactionPair;
import com.blib.api.common.faction.v1.RelationshipState;

@ApiStatus.Internal
public class FactionRelationshipTable {

    private final Map<FactionPair, RelationshipState> edges;

    private boolean dirty;

    public FactionRelationshipTable() {
        this.edges = new HashMap<>();
    }

    public RelationshipState getRelationship(ResourceLocation factionA, ResourceLocation factionB) {
        var pair = FactionPair.of(factionA, factionB);

        return edges.getOrDefault(pair, RelationshipState.NEUTRAL);
    }

    public void setRelationship(ResourceLocation factionA, ResourceLocation factionB, RelationshipState state) {
        var pair = FactionPair.of(factionA, factionB);

        if (state == RelationshipState.NEUTRAL) {
            edges.remove(pair);
        } else {
            edges.put(pair, state);
        }

        dirty = true;
    }

    public Set<ResourceLocation> getFactionsWithState(ResourceLocation factionId, RelationshipState state) {
        return edges.entrySet()
            .stream()
            .filter(entry -> entry.getValue() == state && entry.getKey().contains(factionId))
            .map(entry -> entry.getKey().other(factionId))
            .collect(Collectors.toUnmodifiableSet());
    }

    public void removeFaction(ResourceLocation factionId) {
        var removed = edges.keySet().removeIf(pair -> pair.contains(factionId));

        if (removed) {
            dirty = true;
        }
    }

    public Map<FactionPair, RelationshipState> getAllEdges() {
        return Collections.unmodifiableMap(edges);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void clear() {
        edges.clear();
        dirty = false;
    }
}
