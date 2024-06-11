package org.avp.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface ServerboundPacket extends CustomPacketPayload {

    void handleServer(@Nullable ServerLevel serverLevel);
}
