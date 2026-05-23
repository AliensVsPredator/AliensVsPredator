package com.blib.mod.client.render.goap;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
public final class GOAPDebugState {

    public static final GOAPDebugState INSTANCE = new GOAPDebugState();

    private @Nullable S2CGOAPDebugPayload latestPayload;

    private GOAPDebugState() {}

    public void update(S2CGOAPDebugPayload payload) {
        this.latestPayload = payload;
    }

    /**
     * Latest payload received from the server, or {@code null} if no agent has been tracked yet.
     */
    public @Nullable S2CGOAPDebugPayload latestPayload() {
        return latestPayload;
    }

    public void clear() {
        this.latestPayload = null;
    }
}
