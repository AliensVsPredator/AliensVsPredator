package com.avp.common.gene.behavior;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class GeneDecoders {

    // Helper functions
    private static final UnaryOperator<Byte> IDENTITY_VALUE = UnaryOperator.identity();

    private static final Function<Byte, Float> NORMALIZED_VALUE = value -> Math.clamp(value / ((float) Byte.MAX_VALUE), -1, 1);

    // Base
    public static final GeneDecoder<Byte> ATTACK_DAMAGE = IDENTITY_VALUE::apply;

    public static final GeneDecoder<Float> COLD_RESISTANCE = NORMALIZED_VALUE::apply;

    public static final GeneDecoder<Float> FIRE_RESISTANCE = NORMALIZED_VALUE::apply;

    public static final GeneDecoder<Float> GROWTH_SPEED = NORMALIZED_VALUE::apply;

    // Resin
    public static final GeneDecoder<Byte> BONUS_RESIN_PRODUCTION = value -> {
        var normalizedValue = NORMALIZED_VALUE.apply(value);
        // Additive value
        return (byte) (normalizedValue * 3);
    };

    public static final GeneDecoder<Float> BONUS_RESIN_RECYCLE = value -> {
        var normalizedValue = NORMALIZED_VALUE.apply(value);
        // Multiplicative value
        return normalizedValue * 4;
    };
}
