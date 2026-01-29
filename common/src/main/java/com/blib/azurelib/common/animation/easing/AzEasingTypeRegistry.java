package com.blib.azurelib.common.animation.easing;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AzEasingTypeRegistry {

    private static final Map<String, AzEasingType> EASING_TYPES = new HashMap<>();

    public static AzEasingType register(
        String name,
        Function<Double, Double2DoubleFunction> transformer
    ) {
        return EASING_TYPES.computeIfAbsent(name, ($) -> new AzEasingType() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Double2DoubleFunction buildTransformer(Double value) {
                return transformer.apply(value);
            }
        });
    }

    public static AzEasingType register(
        String name,
        AzEasingType easingType
    ) {
        return register(name, easingType::buildTransformer);
    }

    public static AzEasingType getOrDefault(
        String name,
        @NotNull AzEasingType defaultValue
    ) {
        return EASING_TYPES.getOrDefault(name, defaultValue);
    }

    public static @Nullable AzEasingType getOrNull(String name) {
        return EASING_TYPES.get(name);
    }

    public static Collection<AzEasingType> getValues() {
        return Collections.unmodifiableCollection(EASING_TYPES.values());
    }
}
