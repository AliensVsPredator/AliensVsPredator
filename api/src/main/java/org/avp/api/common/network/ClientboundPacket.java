package org.avp.api.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientboundPacket extends CustomPacketPayload {

    void handleClient();
}
