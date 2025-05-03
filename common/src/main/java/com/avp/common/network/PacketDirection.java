package com.avp.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public sealed interface PacketDirection<T extends CustomPacketPayload> {

    StreamCodec<FriendlyByteBuf, T> codec();

    CustomPacketPayload.Type<T> type();

    record S2C<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec
    ) implements PacketDirection<T> {}

    record C2S<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec
    ) implements PacketDirection<T> {}

    record BI<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec
    ) implements PacketDirection<T> {}
}
