package com.lib.common.network;

import com.bvanseg.just.functional.option.Option;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.function.Consumer;

public record DataKey<T>(
    String id,
    Option<Codec<T>> codec,
    Option<StreamCodec<? extends ByteBuf, T>> streamCodec,
    Consumer<T> onChange,
    Consumer<T> onLoad
) {

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        var dataKey = (DataKey<?>) object;

        return Objects.equals(id, dataKey.id);
    }
}
