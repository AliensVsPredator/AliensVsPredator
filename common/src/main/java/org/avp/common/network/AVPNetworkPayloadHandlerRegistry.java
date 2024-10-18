package org.avp.common.network;

import org.avp.api.service.Services;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.network.payload.ServerboundWeaponReloadRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapAmmunitionTypeRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapFireModeRequestPayload;

public class AVPNetworkPayloadHandlerRegistry {

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPNetworkPayloadHandlerRegistry() {
        throw new UnsupportedOperationException();
    }

    static {
        // Clientbound
        Services.NETWORK_PAYLOAD_SERVICE.registerClientbound(
            ClientboundBulletHitBlockPayload.PAYLOAD_ID,
            ClientboundBulletHitBlockPayload::new
        );

        // Serverbound
        Services.NETWORK_PAYLOAD_SERVICE.registerServerbound(
            ServerboundWeaponReloadRequestPayload.PAYLOAD_ID,
            ServerboundWeaponReloadRequestPayload::new
        );
        Services.NETWORK_PAYLOAD_SERVICE.registerServerbound(
            ServerboundWeaponSwapAmmunitionTypeRequestPayload.PAYLOAD_ID,
            ServerboundWeaponSwapAmmunitionTypeRequestPayload::new
        );
        Services.NETWORK_PAYLOAD_SERVICE.registerServerbound(
            ServerboundWeaponSwapFireModeRequestPayload.PAYLOAD_ID,
            ServerboundWeaponSwapFireModeRequestPayload::new
        );
    }
}
