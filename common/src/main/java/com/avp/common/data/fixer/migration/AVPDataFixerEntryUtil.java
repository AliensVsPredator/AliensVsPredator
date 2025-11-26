package com.avp.common.data.fixer.migration;

import com.blib.common.data.fixer.BLibDataFixerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class AVPDataFixerEntryUtil {

    public static BLibDataFixerRegistry.Entry avpToAvp(Registry<?> registry, String from, String to) {
        return new BLibDataFixerRegistry.Entry(registry, AVPResources.location(from), AVPResources.location(to));
    }

    public static BLibDataFixerRegistry.Entry avpToMc(Registry<?> registry, String from, String to) {
        return new BLibDataFixerRegistry.Entry(
            registry,
            AVPResources.location(from),
            ResourceLocation.fromNamespaceAndPath("minecraft", to)
        );
    }
}
