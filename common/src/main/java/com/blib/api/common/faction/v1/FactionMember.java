package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public sealed interface FactionMember permits EntityMember, SubFactionMember {

    boolean matches(Entity entity);

    boolean matches(ReadableFaction faction);

    static FactionMember entity(UUID uuid) {
        return new EntityMember(uuid);
    }

    static FactionMember entity(Entity entity) {
        return new EntityMember(entity.getUUID());
    }

    static FactionMember subFaction(ResourceLocation factionId) {
        return new SubFactionMember(factionId);
    }
}
