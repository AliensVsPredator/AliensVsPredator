package com.avp.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public sealed interface NetworkHandler<T extends CustomPacketPayload> {

    StreamCodec<FriendlyByteBuf, T> codec();

    CustomPacketPayload.Type<T> type();

    record FromServer<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec,
        BiConsumer<T, Player> payloadConsumer
    ) implements NetworkHandler<T> {}

    record FromClient<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec,
        BiConsumer<T, Player> payloadConsumer
    ) implements NetworkHandler<T> {}

    record FromEither<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<FriendlyByteBuf, T> codec,
        BiConsumer<T, Player> fromClientPayloadConsumer,
        BiConsumer<T, Player> fromServerPayloadConsumer
    ) implements NetworkHandler<T> {}
}
