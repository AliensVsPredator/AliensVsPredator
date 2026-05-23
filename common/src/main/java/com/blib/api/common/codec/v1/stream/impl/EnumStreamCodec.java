package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

public final class EnumStreamCodec<E extends Enum<E>> implements StreamCodec<E> {

    private final E[] values;

    private final E defaultValue;

    private EnumStreamCodec(E[] values, E defaultValue) {
        this.values = values;
        this.defaultValue = defaultValue;
    }

    public static <E extends Enum<E>> EnumStreamCodec<E> of(Class<E> enumClass, E defaultValue) {
        return new EnumStreamCodec<>(enumClass.getEnumConstants(), defaultValue);
    }

    @Override
    public @NotNull <T> E decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
        var ordinal = schema.readByte(input);

        if (ordinal < 0 || ordinal >= values.length) {
            return defaultValue;
        }

        return values[ordinal];
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull E value) {
        schema.writeByte(input, (byte) value.ordinal());
    }
}
