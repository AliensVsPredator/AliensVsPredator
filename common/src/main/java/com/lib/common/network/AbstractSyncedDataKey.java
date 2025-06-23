package com.lib.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface AbstractSyncedDataKey<T> {

    ResourceLocation id();

    StreamCodec<? extends ByteBuf, T> codec();
}
