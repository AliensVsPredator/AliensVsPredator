package com.avp.core.common.config.property.serializer;

@FunctionalInterface
public interface ConfigPropertySerializer<T> {

    String serialize(T value);
}
