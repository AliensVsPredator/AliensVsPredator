package org.avp.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ServerboundPacket extends CustomPacketPayload {
    void handleServer();
}
