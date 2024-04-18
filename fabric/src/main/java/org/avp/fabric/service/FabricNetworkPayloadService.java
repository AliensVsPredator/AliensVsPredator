package org.avp.fabric.service;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import org.avp.api.network.NetworkSide;
import org.avp.common.network.ClientboundPacket;
import org.avp.common.network.ServerboundPacket;
import org.avp.common.service.NetworkPayloadService;
import org.avp.common.service.Services;

public class FabricNetworkPayloadService implements NetworkPayloadService {

    @Override
    public <T extends CustomPacketPayload> void register(
        ResourceLocation payloadId,
        FriendlyByteBuf.Reader<T> reader,
        NetworkSide handlingSide
    ) {
        if (!Services.PLATFORM_SERVICE.isServerEnvironment()) {
            ClientPlayNetworking.PlayChannelHandler clientHandler = (client, listener, buf, sender) -> {
                var payload = reader.apply(buf);
                client.execute(((ClientboundPacket) payload)::handleClient);
            };

            ClientPlayNetworking.registerGlobalReceiver(payloadId, clientHandler);
        }

        ServerPlayNetworking.PlayChannelHandler channelHandler = (
            minecraftServer,
            serverPlayer,
            listener,
            friendlyByteBuf,
            packetSender
        ) -> {
            var payload = reader.apply(friendlyByteBuf);
            minecraftServer.execute(() -> ((ServerboundPacket) payload).handleServer(serverPlayer.serverLevel()));
        };

        ServerPlayNetworking.registerGlobalReceiver(payloadId, channelHandler);
    }
}
