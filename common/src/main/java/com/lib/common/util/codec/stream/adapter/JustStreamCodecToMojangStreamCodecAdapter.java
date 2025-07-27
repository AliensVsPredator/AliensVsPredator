package com.lib.common.util.codec.stream.adapter;

import com.lib.common.util.codec.stream.schema.StreamCodecSchemas;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class JustStreamCodecToMojangStreamCodecAdapter<A> implements StreamCodec<FriendlyByteBuf, A> {

    private final com.bvanseg.just.serialization.codec.stream.StreamCodec<A> streamCodec;

    public JustStreamCodecToMojangStreamCodecAdapter(com.bvanseg.just.serialization.codec.stream.StreamCodec<A> streamCodec) {
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
