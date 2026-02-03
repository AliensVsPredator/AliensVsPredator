package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public interface WritableFaction extends ReadableFaction {

    void addMember(FactionMember member);

    void removeMember(FactionMember member);

    default void addEntity(UUID uuid) {
        addMember(FactionMember.entity(uuid));
    }

    default void addEntity(Entity entity) {
        addMember(FactionMember.entity(entity));
    }

    default void addSubFaction(ResourceLocation factionId) {
        addMember(FactionMember.subFaction(factionId));
    }

    default void removeEntity(UUID uuid) {
        removeMember(FactionMember.entity(uuid));
    }

    default void removeEntity(Entity entity) {
        removeMember(FactionMember.entity(entity));
    }

    default void removeSubFaction(ResourceLocation factionId) {
        removeMember(FactionMember.subFaction(factionId));
    }
}
