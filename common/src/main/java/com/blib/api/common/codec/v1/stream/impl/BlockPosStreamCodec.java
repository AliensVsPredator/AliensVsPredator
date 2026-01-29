package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class BlockPosStreamCodec implements StreamCodec<BlockPos> {

    @Override
    public @NotNull <T> BlockPos decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        return BlockPos.of(streamCodecSchema.readLong(input));
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull BlockPos value) {
        streamCodecSchema.writeLong(input, value.asLong());
    }

}
