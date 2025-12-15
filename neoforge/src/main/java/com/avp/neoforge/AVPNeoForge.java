package com.avp.neoforge;

import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;

import com.blib.BLib;
import com.blib.common.network.data.DataContainer;
import com.blib.common.network.data.DataUser;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;

@Mod(BLib.MOD_ID)
public class AVPNeoForge {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    public AVPNeoForge(IEventBus modBus) {
        BLib.initialize();

        // Mod bus events.
        modBus.addListener(AVPNeoForge::registerPayloadHandlers);

        // Game bus events.
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerPlayerTrackingEntityHandler);
    }

    public static void registerPlayerTrackingEntityHandler(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1")
            .executesOn(HandlerThread.NETWORK);

        REGISTRY.getNetworkHandlers()
            .forEach(networkHandler -> {
                @SuppressWarnings("unchecked")
                var typedNetworkHandler = (NetworkHandler<CustomPacketPayload>) networkHandler;

                switch (typedNetworkHandler) {
                    case NetworkHandler.FromClient<CustomPacketPayload> handler -> registrar.playToServer(
                        handler.type(),
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                    case NetworkHandler.FromEither<CustomPacketPayload> handler -> registrar.playBidirectional(
                        handler.type(),
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
                        new DirectionalPayloadHandler<>(
                            (payload, context) -> context.enqueueWork(
                                () -> handler.fromServerPayloadConsumer().accept(payload, context.player())
                            ),
                            (payload, context) -> context.enqueueWork(
                                () -> handler.fromClientPayloadConsumer().accept(payload, context.player())
                            )
                        )
                    );
                    case NetworkHandler.FromServer<CustomPacketPayload> handler -> registrar.playToClient(
                        handler.type(),
                        new JustStreamCodecToMojangStreamCodecAdapter<>(handler.codec()),
                        (payload, context) -> context.enqueueWork(() -> handler.payloadConsumer().accept(payload, context.player()))
                    );
                }
            });
    }
}
