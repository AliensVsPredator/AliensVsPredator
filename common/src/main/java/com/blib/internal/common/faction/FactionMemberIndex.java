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
import com.blib.api.common.faction.v1.FactionMembership;

@ApiStatus.Internal
public class FactionMemberIndex {

    private final Map<UUID, Set<ResourceLocation>> entityToFactions;

    public FactionMemberIndex() {
        this.entityToFactions = new HashMap<>();
    }

    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return Collections.unmodifiableSet(entityToFactions.getOrDefault(entityUuid, Set.of()));
    }

    public void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added) {
        if (member instanceof FactionMember.Entity(var uuid)) {
            if (added) {
                entityToFactions.computeIfAbsent(uuid, $ -> new HashSet<>()).add(factionId);
            } else {
                removeFromIndex(uuid, factionId);
            }
        }
    }

    public void removeFaction(ResourceLocation factionId, FactionMembership relationships) {
        for (var member : relationships.getMembers()) {
            if (member instanceof FactionMember.Entity(var uuid)) {
                removeFromIndex(uuid, factionId);
            }
        }
    }

    public void rebuild(Map<ResourceLocation, FactionMembership> relationships) {
        entityToFactions.clear();

        for (var factionRelationships : relationships.values()) {
            var factionId = factionRelationships.getId();

            for (var member : factionRelationships.getMembers()) {
                if (member instanceof FactionMember.Entity(var uuid)) {
                    entityToFactions.computeIfAbsent(uuid, $ -> new HashSet<>()).add(factionId);
                }
            }
        }
    }

    public void clear() {
        entityToFactions.clear();
    }

    private void removeFromIndex(UUID key, ResourceLocation value) {
        var set = entityToFactions.get(key);

        if (set != null) {
            set.remove(value);

            if (set.isEmpty()) {
                entityToFactions.remove(key);
            }
        }
    }
}
