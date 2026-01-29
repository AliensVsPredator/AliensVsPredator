package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ResourceLocationStreamCodec implements StreamCodec<ResourceLocation> {

    @Override
    public @NotNull <T> ResourceLocation decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        return ResourceLocation.parse(streamCodecSchema.read(input, StreamCodecs.STRING_UTF8));
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull ResourceLocation value) {
        streamCodecSchema.write(input, StreamCodecs.STRING_UTF8, value.toString());
    }

}
