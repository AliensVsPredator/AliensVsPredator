package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.FactionRelationships;

@ApiStatus.Internal
public class FactionMemberIndex {

    private final Map<UUID, Set<ResourceLocation>> entityToFactions;

    private final Map<ResourceLocation, Set<ResourceLocation>> subfactionToParentFactions;

    public FactionMemberIndex() {
        this.entityToFactions = new HashMap<>();
        this.subfactionToParentFactions = new HashMap<>();
    }

    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return Collections.unmodifiableSet(entityToFactions.getOrDefault(entityUuid, Set.of()));
    }

    public Set<ResourceLocation> getParentFactionIds(ResourceLocation subfactionId) {
        return Collections.unmodifiableSet(subfactionToParentFactions.getOrDefault(subfactionId, Set.of()));
    }

    public void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added) {
        switch (member) {
            case FactionMember.Entity(var uuid) -> {
                if (added) {
                    entityToFactions.computeIfAbsent(uuid, $ -> new HashSet<>()).add(factionId);
                } else {
                    removeFromIndex(entityToFactions, uuid, factionId);
                }
            }
            case FactionMember.SubFaction(var subfactionId) -> {
                if (added) {
                    subfactionToParentFactions.computeIfAbsent(subfactionId, $ -> new HashSet<>()).add(factionId);
                } else {
                    removeFromIndex(subfactionToParentFactions, subfactionId, factionId);
                }
            }
        }
    }

    public void removeFaction(ResourceLocation factionId, FactionRelationships relationships) {
        for (var member : relationships.getMembers()) {
            switch (member) {
                case FactionMember.Entity(var uuid) -> removeFromIndex(entityToFactions, uuid, factionId);
                case FactionMember.SubFaction(var subfactionId) ->
                    removeFromIndex(subfactionToParentFactions, subfactionId, factionId);
            }
        }
    }

    public void rebuild(Map<ResourceLocation, FactionRelationships> relationships) {
        entityToFactions.clear();
        subfactionToParentFactions.clear();

        for (var factionRelationships : relationships.values()) {
            var factionId = factionRelationships.getId();

            for (var member : factionRelationships.getMembers()) {
                switch (member) {
                    case FactionMember.Entity(var uuid) ->
                        entityToFactions.computeIfAbsent(uuid, $ -> new HashSet<>()).add(factionId);
                    case FactionMember.SubFaction(var subfactionId) ->
                        subfactionToParentFactions.computeIfAbsent(subfactionId, $ -> new HashSet<>()).add(factionId);
                }
            }
        }
    }

    public void clear() {
        entityToFactions.clear();
        subfactionToParentFactions.clear();
    }

    private <K> void removeFromIndex(Map<K, Set<ResourceLocation>> index, K key, ResourceLocation value) {
        var set = index.get(key);

        if (set != null) {
            set.remove(value);

            if (set.isEmpty()) {
                index.remove(key);
            }
        }
    }
}
