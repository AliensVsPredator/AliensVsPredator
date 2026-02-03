package com.blib.api.common.faction.v1;

import net.minecraft.world.entity.Entity;

import java.util.UUID;

public record EntityMember(UUID uuid) implements FactionMember {

    @Override
    public boolean matches(Entity entity) {
        return entity.getUUID().equals(uuid);
    }

    @Override
    public boolean matches(ReadableFaction faction) {
        return false;
    }
}
