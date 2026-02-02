package com.blib.api.common.event.v1;

import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface BLibEntityTickEvent {

    void invoke(Entity entity);
}
