package com.lib.common.gameplay.gene;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GeneModifier(
    GeneOperationType operation,
    double value
) {

    public static final Codec<GeneModifier> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GeneOperationType.CODEC.fieldOf("operation").forGetter(GeneModifier::operation),
            Codec.DOUBLE.fieldOf("value").forGetter(GeneModifier::value)
        ).apply(instance, GeneModifier::new)
    );
}
