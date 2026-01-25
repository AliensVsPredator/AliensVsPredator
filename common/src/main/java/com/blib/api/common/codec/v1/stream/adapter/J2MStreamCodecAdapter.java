package com.blib.api.common.codec.v1.stream.adapter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.stream.schema.StreamCodecSchemas;

public class J2MStreamCodecAdapter<A> implements StreamCodec<FriendlyByteBuf, A> {

    private final com.just.codec.stream.StreamCodec<A> streamCodec;

    public J2MStreamCodecAdapter(com.just.codec.stream.StreamCodec<A> streamCodec) {
        this.streamCodec = streamCodec;
    }

    @Override
    public @NotNull A decode(@NotNull FriendlyByteBuf byteBuf) {
        return streamCodec.decode(StreamCodecSchemas.BYTE_BUF, byteBuf);
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf o, @NotNull A a) {
        streamCodec.encode(StreamCodecSchemas.BYTE_BUF, o, a);
    }
}
