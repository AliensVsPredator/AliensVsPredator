package org.avp.fabric.service;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import org.avp.api.network.NetworkSide;
import org.avp.common.network.ClientboundPacket;
import org.avp.common.network.ServerboundPacket;
import org.avp.common.service.NetworkPayloadHandlerRegistry;

/**
 * @author Boston Vanseghi
 */
public class FabricNetworkPayloadHandlerRegistry implements NetworkPayloadHandlerRegistry {

    @Override
    public <T extends CustomPacketPayload> void register(
        ResourceLocation payloadId,
        FriendlyByteBuf.Reader<T> reader,
        NetworkSide handlingSide
    ) {
        ClientPlayNetworking.PlayChannelHandler clientHandler = (client, listener, buf, sender) -> {
            var payload = reader.apply(buf);
            client.execute(((ClientboundPacket) payload)::handleClient);
        };

        ServerPlayNetworking.PlayChannelHandler channelHandler = (
            minecraftServer,
            serverPlayer,
            listener,
            friendlyByteBuf,
            packetSender
        ) -> {
            var payload = reader.apply(friendlyByteBuf);
            minecraftServer.execute(((ServerboundPacket) payload)::handleServer);
        };

        switch (handlingSide) {
            case CLIENT -> ClientPlayNetworking.registerGlobalReceiver(payloadId, clientHandler);
            case COMMON -> {
                ClientPlayNetworking.registerGlobalReceiver(payloadId, clientHandler);
                ServerPlayNetworking.registerGlobalReceiver(payloadId, channelHandler);
            }
            case SERVER -> ServerPlayNetworking.registerGlobalReceiver(payloadId, channelHandler);
        }
    }
}
