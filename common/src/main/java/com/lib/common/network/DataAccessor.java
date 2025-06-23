package com.lib.common.network;

import com.bvanseg.just.functional.option.Option;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public record DataAccessor<T>(
    DataContainer dataContainer,
    DataKey<T> key,
    T initialValue
) {

    public T get() {
        return dataContainer.get(key);
    }

    public void set(T value) {
        dataContainer.set(key, value);
    }

    public void reset() {
        set(initialValue);
    }

    public static class Builder<U> {

        private final String id;

        private final DataContainer dataContainer;

        private Option<Codec<U>> persistentCodecOption;

        private Option<StreamCodec<? extends ByteBuf, U>> streamCodecOption;

        private Consumer<U> onChangeCallback;

        Builder(String id, DataContainer dataContainer) {
            this.id = id;
            this.dataContainer = dataContainer;
            this.persistentCodecOption = Option.none();
            this.streamCodecOption = Option.none();
            this.onChangeCallback = $ -> {};
        }

        public Builder<U> networkSynchronized(StreamCodec<? extends ByteBuf, U> streamCodec) {
            this.streamCodecOption = Option.some(streamCodec);
            return this;
        }

        public Builder<U> onChange(Consumer<U> onChangeCallback) {
            this.onChangeCallback = onChangeCallback;
            return this;
        }

        public Builder<U> persistent(Codec<U> codec) {
            this.persistentCodecOption = Option.some(codec);
            return this;
        }

        public DataAccessor<U> build(U initialValue) {
            var key = new DataKey<>(id, persistentCodecOption, streamCodecOption, onChangeCallback);

            dataContainer.define(key, initialValue);

            return new DataAccessor<>(dataContainer, key, initialValue);
        }
    }
}
