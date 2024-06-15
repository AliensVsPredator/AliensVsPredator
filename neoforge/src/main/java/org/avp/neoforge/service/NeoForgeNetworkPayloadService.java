package org.avp.neoforge.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.common.network.ClientboundPacket;
import org.avp.api.common.network.NetworkSide;
import org.avp.api.common.network.PayloadHandlerData;
import org.avp.api.common.network.ServerboundPacket;
import org.avp.api.service.NetworkPayloadService;
import org.avp.common.AVPConstants;

public class NeoForgeNetworkPayloadService implements NetworkPayloadService {

    public static final List<PayloadHandlerData<?>> PAYLOAD_HANDLER_DATA_LIST = new ArrayList<>();

    public static void registerPayloadHandlers(RegisterPayloadHandlerEvent registerPayloadHandlerEvent) {
        var registrar = registerPayloadHandlerEvent.registrar(AVPConstants.MOD_ID);

        PAYLOAD_HANDLER_DATA_LIST.forEach(payloadHandlerData -> {
            var id = payloadHandlerData.payloadId();
            var reader = payloadHandlerData.factory();
            var handlingSide = payloadHandlerData.networkSide();

            registrar.play(id, reader, handler -> {
                Runnable registerClientHandle = () -> handler.client(
                    (data, ctx) -> ctx.workHandler().execute(((ClientboundPacket) data)::handleClient)
                );
                Runnable registerServerHandle = () -> handler.server(
                    (data, ctx) -> ctx.workHandler()
                        .execute(
                            () -> ((ServerboundPacket) data).handleServer((ServerLevel) ctx.level().orElse(null))
                        )
                );

                switch (handlingSide) {
                    case CLIENT -> registerClientHandle.run();
                    case COMMON -> {
                        registerClientHandle.run();
                        registerServerHandle.run();
                    }
                    case SERVER -> registerServerHandle.run();
                }
            });
        });
    }

    @Override
    public <T extends CustomPacketPayload> void register(
        ResourceLocation payloadId,
        FriendlyByteBuf.Reader<T> reader,
        NetworkSide handlingSide
    ) {
        PAYLOAD_HANDLER_DATA_LIST.add(new PayloadHandlerData<>(payloadId, reader, handlingSide));
    }
}
