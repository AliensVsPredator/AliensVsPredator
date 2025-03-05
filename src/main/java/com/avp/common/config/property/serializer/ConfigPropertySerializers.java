package com.avp.common.config.property.serializer;

public class ConfigPropertySerializers {

    public static final ConfigPropertySerializer<Boolean> BOOLEAN = value -> Boolean.toString(value);

    public static final ConfigPropertySerializer<Byte> BYTE = value -> Byte.toString(value);

    public static final ConfigPropertySerializer<Double> DOUBLE = value -> Double.toString(value);

    public static final ConfigPropertySerializer<Float> FLOAT = value -> Float.toString(value);

    public static final ConfigPropertySerializer<Integer> INT = value -> Integer.toString(value);

    public static final ConfigPropertySerializer<Long> LONG = value -> Long.toString(value);

    public static final ConfigPropertySerializer<Short> SHORT = value -> Short.toString(value);

    public static final ConfigPropertySerializer<String> STRING = value -> value;

    private ConfigPropertySerializers() {
        throw new UnsupportedOperationException();
    }
}
