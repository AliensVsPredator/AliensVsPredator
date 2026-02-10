package com.blib.api.common.event.v1;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface BLibFactionRemoveEvent {

    void invoke(ResourceLocation factionId);
}
