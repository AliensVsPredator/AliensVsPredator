package com.blib.neoforge.internal.client.service.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibNeoForgeClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        // Silently drop when there's no active server connection — see the Fabric impl for rationale. The engine
        // workspace can be opened from the TitleScreen and panels may attempt to send fetch packets before a world
        // exists; we don't want those to crash the click handler that triggered them.
        if (Minecraft.getInstance().getConnection() == null) {
            return;
        }
        PacketDistributor.sendToServer(payload);
    }
}
