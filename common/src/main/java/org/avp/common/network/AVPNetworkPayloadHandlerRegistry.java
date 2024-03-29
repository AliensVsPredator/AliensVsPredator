package org.avp.common.network;

import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPNetworkPayloadHandlerRegistry {

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPNetworkPayloadHandlerRegistry() {
        throw new UnsupportedOperationException();
    }

    static {
        // Clientbound
        Services.PAYLOAD_HANDLER_REGISTRY.registerClientbound(
            ClientboundBulletHitBlockPayload.PAYLOAD_ID,
            ClientboundBulletHitBlockPayload::new
        );
    }
}
