package org.avp.api.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadHandlerData<T extends CustomPacketPayload>(
    ResourceLocation payloadId,
    FriendlyByteBuf.Reader<T> factory,
    NetworkSide networkSide
) {}
