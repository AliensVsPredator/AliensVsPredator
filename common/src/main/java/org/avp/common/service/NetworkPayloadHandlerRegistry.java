package org.avp.common.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import org.avp.api.network.NetworkSide;
import org.avp.common.network.ClientboundPacket;
import org.avp.common.network.ServerboundPacket;

/**
 * @author Boston Vanseghi
 */
public interface NetworkPayloadHandlerRegistry {

    <T extends CustomPacketPayload> void register(ResourceLocation payloadId, FriendlyByteBuf.Reader<T> reader, NetworkSide handlingSide);

    default void registerClientbound(ResourceLocation payloadId, FriendlyByteBuf.Reader<ClientboundPacket> reader) {
        this.register(payloadId, reader, NetworkSide.CLIENT);
    }

    default void registerServerbound(ResourceLocation payloadId, FriendlyByteBuf.Reader<ServerboundPacket> reader) {
        this.register(payloadId, reader, NetworkSide.SERVER);
    }
}
