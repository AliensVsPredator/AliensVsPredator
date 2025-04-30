package com.avp.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import com.avp.AVPResources;

public class DecoratedPotPatterns {

    public static final DecoratedPotPattern OVOID = register("ovoid_pottery_pattern");

    public static final DecoratedPotPattern PARASITE = register("parasite_pottery_pattern");

    public static final DecoratedPotPattern ROYALTY = register("royalty_pottery_pattern");

    public static final DecoratedPotPattern VECTOR = register("vector_pottery_pattern");

    private static DecoratedPotPattern register(String name) {
        var resourceLocation = AVPResources.location(name);
        var pattern = new DecoratedPotPattern(resourceLocation);
        return Registry.register(BuiltInRegistries.DECORATED_POT_PATTERN, resourceLocation, pattern);
    }

    public static void initialize() {}
}
