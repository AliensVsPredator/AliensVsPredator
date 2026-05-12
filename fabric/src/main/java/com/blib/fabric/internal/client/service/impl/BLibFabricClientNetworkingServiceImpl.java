package com.blib.fabric.internal.client.service.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibFabricClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        // Silently drop when there's no active server connection. The engine workspace can be opened from the
        // TitleScreen (menu-overlay mode), at which point panels may try to send fetch / state-sync packets in
        // their onShown / render hooks before any world is loaded. ClientPlayNetworking.send throws
        // IllegalStateException in that case, which would crash the click handler that triggered it. The dropped
        // packet is a no-op: the server isn't reachable and there's no responder to populate the panel either.
        if (Minecraft.getInstance().getConnection() == null) {
            return;
        }
        ClientPlayNetworking.send(payload);
    }
}
