package com.lib.common.gameplay.gene;

import com.bvanseg.just.serialization.codec.stream.StreamCodec;
import com.bvanseg.just.serialization.codec.stream.schema.StreamCodecSchema;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public enum GeneOperationType {

    ADDITIVE,
    MULTIPLICATIVE;

    public static final Codec<GeneOperationType> CODEC = Codec.STRING.xmap(
        GeneOperationType::valueOf,
        GeneOperationType::name
    );

    public static final StreamCodec<GeneOperationType> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull <T> GeneOperationType decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            int ordinal = streamCodecSchema.readVarInt(input);
            GeneOperationType[] values = GeneOperationType.values();
            if (ordinal < 0 || ordinal >= values.length) {
                throw new IllegalArgumentException("Invalid GeneOperationType ordinal: " + ordinal);
            }
            return values[ordinal];
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull GeneOperationType value) {
            streamCodecSchema.writeVarInt(input, value.ordinal());
        }
    };
}
