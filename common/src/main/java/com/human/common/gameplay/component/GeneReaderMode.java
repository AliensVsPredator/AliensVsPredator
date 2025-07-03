package com.human.common.gameplay.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum GeneReaderMode {

    CLEAR,
    ACTIVE_GENES,
    BONUS_GENES,
    DORMANT_GENES;

    public static final Codec<GeneReaderMode> CODEC = Codec.STRING.xmap(
        GeneReaderMode::valueOf,
        GeneReaderMode::name
    );

    public static final StreamCodec<FriendlyByteBuf, GeneReaderMode> STREAM_CODEC =
        StreamCodec.of(
            (buf, mode) -> buf.writeVarInt(mode.ordinal()),
            buf -> {
                var ordinal = buf.readVarInt();
                var values = GeneReaderMode.values();

                if (ordinal < 0 || ordinal >= values.length) {
                    throw new IllegalArgumentException("Invalid GeneReaderMode ordinal: " + ordinal);
                }

                return values[ordinal];
            }
        );
}
