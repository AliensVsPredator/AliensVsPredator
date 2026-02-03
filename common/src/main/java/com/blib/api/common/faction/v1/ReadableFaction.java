package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.UUID;

public interface ReadableFaction {

    ResourceLocation getId();

    boolean hasMember(FactionMember member);

    Set<FactionMember> getMembers();

    default boolean hasEntity(UUID uuid) {
        return hasMember(FactionMember.entity(uuid));
    }

    default boolean hasEntity(Entity entity) {
        return hasMember(FactionMember.entity(entity));
    }

    default boolean hasSubFaction(ResourceLocation factionId) {
        return hasMember(FactionMember.subFaction(factionId));
    }
}
