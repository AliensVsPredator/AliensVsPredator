package com.avp.core.common.network;

import com.avp.core.platform.PacketHandleContext;

import com.avp.core.common.item.GunItem;
import com.avp.core.common.network.packet.C2SGunReloadPayload;

public class ServerListener {

    public static void handleGunReloadPayload(PacketHandleContext.Server<C2SGunReloadPayload> context) {
        var player = context.player();
        GunItem.reload(player);
    }

    private ServerListener() {
        throw new UnsupportedOperationException();
    }
}
