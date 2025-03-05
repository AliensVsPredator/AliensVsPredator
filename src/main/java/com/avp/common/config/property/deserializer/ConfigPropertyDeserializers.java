package com.avp.common.config.property.deserializer;

import java.util.function.Function;

public class ConfigPropertyDeserializers {

    public static final ConfigPropertyDeserializer<Boolean> BOOLEAN = rawValue -> tryOrElse(rawValue, Boolean::parseBoolean, false);

    public static final ConfigPropertyDeserializer<Byte> BYTE = rawValue -> tryOrElse(rawValue, Byte::parseByte, (byte) 0);

    public static final ConfigPropertyDeserializer<Double> DOUBLE = rawValue -> tryOrElse(rawValue, Double::parseDouble, 0D);

    public static final ConfigPropertyDeserializer<Float> FLOAT = rawValue -> tryOrElse(rawValue, Float::parseFloat, 0F);

    public static final ConfigPropertyDeserializer<Integer> INT = rawValue -> tryOrElse(rawValue, Integer::parseInt, 0);

    public static final ConfigPropertyDeserializer<Long> LONG = rawValue -> tryOrElse(rawValue, Long::parseUnsignedLong, 0L);

    public static final ConfigPropertyDeserializer<Short> SHORT = rawValue -> tryOrElse(rawValue, Short::parseShort, (short) 0);

    public static final ConfigPropertyDeserializer<String> STRING = rawValue -> rawValue;

    private static <T> T tryOrElse(String rawValue, Function<String, T> factory, T fallbackValue) {
        try {
            return factory.apply(rawValue);
        } catch (NumberFormatException e) {
            return fallbackValue;
        }
    }

    private ConfigPropertyDeserializers() {
        throw new UnsupportedOperationException();
    }
}
