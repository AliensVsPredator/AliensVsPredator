package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.service.Services;

public class AVPDecoratedPotPatterns {

    public static final Supplier<DecoratedPotPattern> OVOID = register("ovoid_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> PARASITE = register("parasite_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> ROYALTY = register("royalty_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> VECTOR = register("vector_pottery_pattern");

    private static Supplier<DecoratedPotPattern> register(String id) {
        return Services.REGISTRY.register(
            BuiltInRegistries.DECORATED_POT_PATTERN,
            id,
            () -> new DecoratedPotPattern(AVPResources.location(id))
        );
    }

    public static void initialize() {}
}
