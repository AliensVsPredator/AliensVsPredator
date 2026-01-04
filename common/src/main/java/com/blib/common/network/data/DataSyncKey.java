package com.blib.common.network.data;

import com.just.codec.stream.StreamCodec;
import com.just.core.functional.option.Option;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record DataSyncKey<T>(
    ResourceLocation id,
    T initialValue,
    Option<PersistenceMetadata<T>> persistenceMetadata,
    Option<StreamCodec<T>> streamCodec
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

        var dataSyncKey = (DataSyncKey<?>) object;

        return Objects.equals(id, dataSyncKey.id);
    }

    public static class Builder<U> {

        private final ResourceLocation id;

        private Option<PersistenceMetadata<U>> persistDataOption;

        private Option<StreamCodec<U>> streamCodecOption;

        public Builder(ResourceLocation id) {
            this.id = id;
            this.persistDataOption = Option.none();
            this.streamCodecOption = Option.none();
        }

        public Builder<U> networkSynchronized(StreamCodec<U> streamCodec) {
            this.streamCodecOption = Option.some(streamCodec);
            return this;
        }

        public Builder<U> persistent(String key, Codec<U> codec) {
            this.persistDataOption = Option.some(new PersistenceMetadata<>(key, codec));
            return this;
        }

        public DataSyncKey<U> build(U initialValue) {
            return new DataSyncKey<>(id, initialValue, persistDataOption, streamCodecOption);
        }
    }
}
