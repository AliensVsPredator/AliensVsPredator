package com.lib.common.network;

import com.bvanseg.just.functional.option.Option;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record DataKey<T>(
    ResourceLocation id,
    T initialValue,
    Option<PersistenceMetadata<T>> persistenceMetadata,
    Option<StreamCodec<? extends ByteBuf, T>> streamCodec
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

    public static class Builder<U> {

        private final ResourceLocation id;

        private Option<PersistenceMetadata<U>> persistDataOption;

        private Option<StreamCodec<? extends ByteBuf, U>> streamCodecOption;

        public Builder(ResourceLocation id) {
            this.id = id;
            this.persistDataOption = Option.none();
            this.streamCodecOption = Option.none();
        }

        public Builder<U> networkSynchronized(StreamCodec<? extends ByteBuf, U> streamCodec) {
            this.streamCodecOption = Option.some(streamCodec);
            return this;
        }

        public Builder<U> persistent(String key, Codec<U> codec) {
            this.persistDataOption = Option.some(new PersistenceMetadata<>(key, codec));
            return this;
        }

        public DataKey<U> build(U initialValue) {
            return new DataKey<>(id, initialValue, persistDataOption, streamCodecOption);
        }
    }
}
