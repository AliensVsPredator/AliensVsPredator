package com.blib.common.event;

import net.minecraft.core.RegistryAccess;

public interface BLibTagsUpdatedEvent {

    void invoke(RegistryAccess registryAccess, boolean fromClientPacket);
}
