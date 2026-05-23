package com.blib.neoforge.internal.client.service.impl;

import com.just.core.functional.result.Result;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.network.v1.BLibSendError;
import com.blib.internal.client.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibNeoForgeClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public Result<Void, BLibSendError> sendToServer(CustomPacketPayload payload) {
        // See the Fabric impl for the rationale. NotConnected covers the menu-overlay-pre-world-load case where a
        // panel's fetch packet would otherwise crash the click handler.
        if (Minecraft.getInstance().getConnection() == null) {
            return Result.err(BLibSendError.NotConnected.INSTANCE);
        }
        PacketDistributor.sendToServer(payload);
        return Result.ok(null);
    }
}
