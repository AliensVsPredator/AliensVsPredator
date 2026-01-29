package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class DirectionStreamCodec implements StreamCodec<Direction> {

    @Override
    public @NotNull <T> Direction decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        return Direction.from3DDataValue(streamCodecSchema.readByte(input));
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull Direction value) {
        streamCodecSchema.writeByte(input, (byte) value.get3DDataValue());
    }

}
