package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

public class DataComponentTypeStreamCodec implements StreamCodec<DataComponentType<?>> {

    @Override
    public @NotNull <T> DataComponentType<?> decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        var typeId = streamCodecSchema.readVarInt(input);
        return BuiltInRegistries.DATA_COMPONENT_TYPE.byIdOrThrow(typeId);
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull DataComponentType<?> value) {
        var typeId = BuiltInRegistries.DATA_COMPONENT_TYPE.getIdOrThrow(value);
        streamCodecSchema.writeVarInt(input, typeId);
    }

}
