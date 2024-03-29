package org.avp.common.network;

import net.minecraft.client.Minecraft;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;

/**
 * @author Boston Vanseghi
 */
public class AVPClientListener {

    public static void handleBulletHitBlockPayload(ClientboundBulletHitBlockPayload bulletHitBlockPayload) {
        var blockPos = bulletHitBlockPayload.blockPos();
        var direction = bulletHitBlockPayload.direction();

        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, direction);
        }
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
