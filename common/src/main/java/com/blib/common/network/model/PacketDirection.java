package com.blib.common.network.model;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public sealed interface PacketDirection<T extends CustomPacketPayload> {

    StreamCodec<T> codec();

    CustomPacketPayload.Type<T> type();

    record S2C<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<T> codec
    ) implements PacketDirection<T> {}

    record C2S<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<T> codec
    ) implements PacketDirection<T> {}

    record BI<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<T> codec
    ) implements PacketDirection<T> {}
}
