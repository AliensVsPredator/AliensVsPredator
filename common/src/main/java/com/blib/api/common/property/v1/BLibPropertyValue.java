package com.blib.api.common.property.v1;

import com.blib.api.common.property.v1.serializer.BLibPropertySerializer;

public sealed interface BLibPropertyValue<T> {

    String serialized(BLibPropertySerializer<T> serializer);

    T deserializedOrNull(BLibPropertySerializer<T> serializer);

    record Deserialized<T>(T value) implements BLibPropertyValue<T> {

        @Override
        public String serialized(BLibPropertySerializer<T> serializer) {
            return serializer.serialize(value);
        }

        @Override
        public T deserializedOrNull(BLibPropertySerializer<T> serializer) {
            return value;
        }
    }

    record Serialized<T>(String value) implements BLibPropertyValue<T> {

        @Override
        public String serialized(BLibPropertySerializer<T> serializer) {
            return value;
        }

        @Override
        public T deserializedOrNull(BLibPropertySerializer<T> serializer) {
            return serializer.deserializeOrNull(value);
        }

    }
}
