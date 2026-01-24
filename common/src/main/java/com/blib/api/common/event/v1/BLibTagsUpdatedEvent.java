package com.blib.api.common.event.v1;

import net.minecraft.core.RegistryAccess;

public interface BLibTagsUpdatedEvent {

    void invoke(RegistryAccess registryAccess, boolean fromClientPacket);
}
