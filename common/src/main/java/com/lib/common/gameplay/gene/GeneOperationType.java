package com.lib.common.gameplay.gene;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum GeneOperationType {

    ADDITIVE,
    MULTIPLICATIVE;

    public static final Codec<GeneOperationType> CODEC = Codec.STRING.xmap(
        GeneOperationType::valueOf,
        GeneOperationType::name
    );

    public static final StreamCodec<FriendlyByteBuf, GeneOperationType> STREAM_CODEC =
        StreamCodec.of(
            (buf, value) -> buf.writeVarInt(value.ordinal()),
            buf -> {
                int ordinal = buf.readVarInt();
                GeneOperationType[] values = GeneOperationType.values();
                if (ordinal < 0 || ordinal >= values.length) {
                    throw new IllegalArgumentException("Invalid GeneOperationType ordinal: " + ordinal);
                }
                return values[ordinal];
            }
        );
}
