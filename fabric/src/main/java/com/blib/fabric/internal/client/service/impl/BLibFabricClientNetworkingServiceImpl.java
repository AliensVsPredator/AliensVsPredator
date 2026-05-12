package com.blib.fabric.internal.client.service.impl;

import com.just.core.functional.result.Result;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.network.v1.BLibSendError;
import com.blib.internal.client.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibFabricClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public Result<Void, BLibSendError> sendToServer(CustomPacketPayload payload) {
        // Drop with NotConnected when no active server connection. The engine workspace can be opened from the
        // TitleScreen (menu-overlay mode), at which point panels may try to send fetch / state-sync packets in
        // their onShown / render hooks before any world is loaded. ClientPlayNetworking.send throws
        // IllegalStateException in that case. Surfacing this as Result.err(NotConnected) lets callers that care
        // (e.g. retry-on-world-load logic) react, while existing callers that discard the result remain unchanged.
        if (Minecraft.getInstance().getConnection() == null) {
            return Result.err(BLibSendError.NotConnected.INSTANCE);
        }
        ClientPlayNetworking.send(payload);
        return Result.ok(null);
    }
}
