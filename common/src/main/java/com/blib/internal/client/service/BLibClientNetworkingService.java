package com.blib.internal.client.service;

import com.just.core.functional.result.Result;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.network.v1.BLibSendError;

@ApiStatus.Internal
public interface BLibClientNetworkingService {

    /**
     * Send {@code payload} to the server. Returns {@code Result.ok(null)} on a successful enqueue, or
     * {@code Result.err} with a {@link BLibSendError} variant explaining why the send was skipped (e.g.
     * {@link BLibSendError.NotConnected} when no server connection exists yet). Callers that don't care about the
     * outcome can simply discard the result.
     */
    Result<Void, BLibSendError> sendToServer(CustomPacketPayload payload);
}
