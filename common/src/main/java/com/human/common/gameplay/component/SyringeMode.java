package com.human.common.gameplay.component;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public enum SyringeMode {

    // Allows player to empty the syringe on demand.
    EMPTY,
    // Allows player to extract mob genes and merge with current genes.
    EXTRACT,
    // Allows player to inject genes into a mob.
    INJECT;

    public static final Codec<SyringeMode> CODEC = Codec.STRING.xmap(
        SyringeMode::valueOf,
        SyringeMode::name
    );

    public static final StreamCodec<SyringeMode> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull <T> SyringeMode decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var ordinal = streamCodecSchema.readVarInt(input);
            var values = SyringeMode.values();

            if (ordinal < 0 || ordinal >= values.length) {
                throw new IllegalArgumentException("Invalid SyringeMode ordinal: " + ordinal);
            }

            return values[ordinal];
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull SyringeMode value) {
            streamCodecSchema.writeVarInt(input, value.ordinal());
        }
    };
}
