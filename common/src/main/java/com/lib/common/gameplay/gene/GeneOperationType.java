package com.lib.common.gameplay.gene;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public enum GeneOperationType {

    ADDITIVE,
    MULTIPLICATIVE;

    public static final GeneOperationType[] VALUES = values();

    public static final Codec<GeneOperationType> CODEC = Codec.STRING.xmap(
        GeneOperationType::valueOf,
        GeneOperationType::name
    );

    public static final StreamCodec<GeneOperationType> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull <T> GeneOperationType decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var ordinal = streamCodecSchema.readVarInt(input);

            if (ordinal < 0 || ordinal >= VALUES.length) {
                throw new IllegalArgumentException("Invalid GeneOperationType ordinal: " + ordinal);
            }

            return VALUES[ordinal];
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull GeneOperationType value) {
            streamCodecSchema.writeVarInt(input, value.ordinal());
        }
    };
}
