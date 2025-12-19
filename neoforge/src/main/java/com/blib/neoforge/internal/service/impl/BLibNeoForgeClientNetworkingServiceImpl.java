package com.blib.neoforge.internal.service.impl;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibNeoForgeClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
