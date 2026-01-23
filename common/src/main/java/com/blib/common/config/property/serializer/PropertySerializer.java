package com.blib.common.config.property.serializer;

import com.just.core.functional.option.Option;

/**
 * Serializes and deserializes property values to/from their raw string representation.
 *
 * @param <T> The Java type this serializer handles
 */
public interface PropertySerializer<T> {

    /**
     * Deserializes a raw string value to the target type. Returns Option.none() if the value cannot be deserialized to
     * this type.
     *
     * @param raw The raw string value from the properties file
     */
    Option<T> deserialize(String raw);

    /**
     * Serializes a value to its raw string representation for storage in the properties file.
     */
    String serialize(T value);
}
