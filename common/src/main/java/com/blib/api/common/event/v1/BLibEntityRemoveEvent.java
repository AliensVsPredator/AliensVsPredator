package com.blib.api.common.event.v1;

import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface BLibEntityRemoveEvent {

    void invoke(Entity entity, Entity.RemovalReason reason);
}
