package org.avp.common.network;

import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.network.payload.ServerboundWeaponReloadRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapAmmunitionTypeRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapFireModeRequestPayload;
import org.avp.common.service.Services;

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

        // Serverbound
        Services.PAYLOAD_HANDLER_REGISTRY.registerServerbound(
            ServerboundWeaponReloadRequestPayload.PAYLOAD_ID,
            ServerboundWeaponReloadRequestPayload::new
        );
        Services.PAYLOAD_HANDLER_REGISTRY.registerServerbound(
            ServerboundWeaponSwapAmmunitionTypeRequestPayload.PAYLOAD_ID,
            ServerboundWeaponSwapAmmunitionTypeRequestPayload::new
        );
        Services.PAYLOAD_HANDLER_REGISTRY.registerServerbound(
            ServerboundWeaponSwapFireModeRequestPayload.PAYLOAD_ID,
            ServerboundWeaponSwapFireModeRequestPayload::new
        );
    }
}
