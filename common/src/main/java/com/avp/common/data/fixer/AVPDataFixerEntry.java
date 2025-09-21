package com.avp.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public record AVPDataFixerEntry(
    Registry<?> registry,
    ResourceLocation from,
    ResourceLocation to
) {

    public static AVPDataFixerEntry avpToAvp(Registry<?> registry, String from, String to) {
        return new AVPDataFixerEntry(registry, AVPResources.location(from), AVPResources.location(to));
    }

    public static AVPDataFixerEntry avpToMc(Registry<?> registry, String from, String to) {
        return new AVPDataFixerEntry(registry, AVPResources.location(from), ResourceLocation.fromNamespaceAndPath("minecraft", to));
    }
}
