package com.avp.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPDecoratedPotPatterns {

    public static final AVPDeferredHolder<DecoratedPotPattern> OVOID = register("ovoid_pottery_pattern");

    public static final AVPDeferredHolder<DecoratedPotPattern> PARASITE = register("parasite_pottery_pattern");

    public static final AVPDeferredHolder<DecoratedPotPattern> ROYALTY = register("royalty_pottery_pattern");

    public static final AVPDeferredHolder<DecoratedPotPattern> VECTOR = register("vector_pottery_pattern");

    private static AVPDeferredHolder<DecoratedPotPattern> register(String id) {
        return Services.REGISTRY.register(
            BuiltInRegistries.DECORATED_POT_PATTERN,
            id,
            () -> new DecoratedPotPattern(AVPResources.location(id))
        );
    }

    public static void initialize() {}
}
