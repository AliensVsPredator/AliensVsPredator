package org.avp.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientboundPacket extends CustomPacketPayload {
    void handleClient();
}
