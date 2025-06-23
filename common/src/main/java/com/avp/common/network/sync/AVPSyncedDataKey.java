package com.avp.common.network.sync;

import com.lib.common.network.AbstractSyncedDataKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public record AVPSyncedDataKey<T>(
    ResourceLocation id,
    StreamCodec<? extends ByteBuf, T> codec
) implements AbstractSyncedDataKey<T> {

    public AVPSyncedDataKey(
        String path,
        StreamCodec<? extends ByteBuf, T> codec
    ) {
        this(AVPResources.location(path), codec);
    }
}
