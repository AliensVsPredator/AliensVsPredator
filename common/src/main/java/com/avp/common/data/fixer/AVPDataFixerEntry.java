package com.avp.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public record AVPDataFixerEntry(
    Registry<?> registry,
    ResourceLocation from,
    ResourceLocation to
) {

    public AVPDataFixerEntry(
        Registry<?> registry,
        String from,
        String to
    ) {
        this(registry, AVPResources.location(from), AVPResources.location(to));
    }
}
