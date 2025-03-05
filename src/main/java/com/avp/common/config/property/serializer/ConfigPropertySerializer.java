package com.avp.common.config.property.serializer;

@FunctionalInterface
public interface ConfigPropertySerializer<T> {

    String serialize(T value);
}
