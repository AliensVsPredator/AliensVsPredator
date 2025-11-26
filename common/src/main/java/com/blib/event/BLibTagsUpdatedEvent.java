package com.blib.event;

import net.minecraft.core.RegistryAccess;

public record BLibTagsUpdatedEvent(
    RegistryAccess registryAccess,
    boolean isClient
) {}
