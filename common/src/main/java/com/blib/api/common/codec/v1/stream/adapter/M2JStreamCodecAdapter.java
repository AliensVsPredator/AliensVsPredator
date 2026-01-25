package com.blib.api.common.codec.v1.stream.adapter;

import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class M2JStreamCodecAdapter<A> implements com.just.codec.stream.StreamCodec<A> {

    private final StreamCodec<FriendlyByteBuf, A> streamCodec;

    public M2JStreamCodecAdapter(StreamCodec<FriendlyByteBuf, A> streamCodec) {
        this.streamCodec = streamCodec;
    }

    @Override
    public @NotNull <T> A decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T t) {
        return streamCodec.decode((FriendlyByteBuf) t);
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T t, @NotNull A a) {
        streamCodec.encode((FriendlyByteBuf) t, a);
    }
}
