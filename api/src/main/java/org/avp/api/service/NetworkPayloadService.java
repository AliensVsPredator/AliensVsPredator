package org.avp.api.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import org.avp.api.common.network.NetworkSide;
import org.avp.api.common.network.ClientboundPacket;
import org.avp.api.common.network.ServerboundPacket;

public interface NetworkPayloadService {

    <T extends CustomPacketPayload> void register(ResourceLocation payloadId, FriendlyByteBuf.Reader<T> reader, NetworkSide handlingSide);

    default void registerClientbound(ResourceLocation payloadId, FriendlyByteBuf.Reader<ClientboundPacket> reader) {
        this.register(payloadId, reader, NetworkSide.CLIENT);
    }

    default void registerServerbound(ResourceLocation payloadId, FriendlyByteBuf.Reader<ServerboundPacket> reader) {
        this.register(payloadId, reader, NetworkSide.SERVER);
    }
}
