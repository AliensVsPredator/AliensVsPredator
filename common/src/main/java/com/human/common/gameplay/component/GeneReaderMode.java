package com.human.common.gameplay.component;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public enum GeneReaderMode {

    CLEAR,
    ACTIVE_GENES,
    BONUS_GENES,
    DORMANT_GENES;

    public static final Codec<GeneReaderMode> CODEC = Codec.STRING.xmap(
        GeneReaderMode::valueOf,
        GeneReaderMode::name
    );

    public static final StreamCodec<GeneReaderMode> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull <T> GeneReaderMode decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var ordinal = streamCodecSchema.readVarInt(input);
            var values = GeneReaderMode.values();

            if (ordinal < 0 || ordinal >= values.length) {
                throw new IllegalArgumentException("Invalid GeneReaderMode ordinal: " + ordinal);
            }

            return values[ordinal];
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull GeneReaderMode value) {
            streamCodecSchema.writeVarInt(input, value.ordinal());
        }
    };
}
