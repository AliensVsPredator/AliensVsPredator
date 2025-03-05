package com.avp.common.config.property.deserializer;

@FunctionalInterface
public interface ConfigPropertyDeserializer<T> {

    T deserialize(String rawValue);
}
