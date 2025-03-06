package com.avp.core.common.block;

import com.avp.core.platform.service.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import com.avp.core.AVPResources;

import java.util.function.Supplier;

public class DecoratedPotPatterns {

    public static final Supplier<DecoratedPotPattern> OVOID = register("ovoid_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> PARASITE = register("parasite_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> ROYALTY = register("royalty_pottery_pattern");

    public static final Supplier<DecoratedPotPattern> VECTOR = register("vector_pottery_pattern");

    private static Supplier<DecoratedPotPattern> register(String name) {
        return Services.REGISTRY.registerDecoratedPotPattern(name, () -> new DecoratedPotPattern(AVPResources.location(name)));
    }

    public static void initialize() {}
}
