package com.avp.core.common.config.property.deserializer;

@FunctionalInterface
public interface ConfigPropertyDeserializer<T> {

    T deserialize(String rawValue);
}
