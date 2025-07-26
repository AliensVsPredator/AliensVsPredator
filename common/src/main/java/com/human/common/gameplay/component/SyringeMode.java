package com.human.common.gameplay.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

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

    public static final StreamCodec<FriendlyByteBuf, SyringeMode> STREAM_CODEC =
        StreamCodec.of(
            (buf, mode) -> buf.writeVarInt(mode.ordinal()),
            buf -> {
                var ordinal = buf.readVarInt();
                var values = SyringeMode.values();

                if (ordinal < 0 || ordinal >= values.length) {
                    throw new IllegalArgumentException("Invalid SyringeMode ordinal: " + ordinal);
                }

                return values[ordinal];
            }
        );
}
