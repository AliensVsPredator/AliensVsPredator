package com.avp.common.network.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListStreamCodec<T> implements StreamCodec<FriendlyByteBuf, List<T>> {

    private final StreamCodec<FriendlyByteBuf, T> streamCodec;

    public ListStreamCodec(StreamCodec<FriendlyByteBuf, T> streamCodec) {
        this.streamCodec = streamCodec;
    }

    @Override
    public @NotNull List<T> decode(@NotNull FriendlyByteBuf friendlyByteBuf) {
        var length = friendlyByteBuf.readByte();
        var elements = new ArrayList<T>();

        for (var i = 0; i < length; i++) {
            elements.add(streamCodec.decode(friendlyByteBuf));
        }

        return elements;
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull List<T> elements) {
        friendlyByteBuf.writeByte(elements.size());
        elements.forEach(element -> streamCodec.encode(friendlyByteBuf, element));
    }
}
