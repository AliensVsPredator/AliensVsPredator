package com.blib.mod.common.network;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.network.v1.PacketDirection;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CBlockEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CItemStackDispatchCommandPayload;

public class BLibPacketDirections {

    private static final BLibNetworkRegistry REGISTRY = BLib.MOD.registries().createNetworkRegistry();

    public static void initialize() {
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityDataSyncPayload.TYPE, S2CEntityDataSyncPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CGOAPDebugPayload.TYPE, S2CGOAPDebugPayload.CODEC));

        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(
                S2CBlockEntityDispatchCommandPayload.TYPE,
                BLibCodecs.Stream.fromMojang(S2CBlockEntityDispatchCommandPayload.CODEC)
            )
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(
                S2CEntityDispatchCommandPayload.TYPE,
                BLibCodecs.Stream.fromMojang(S2CEntityDispatchCommandPayload.CODEC)
            )
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(
                S2CItemStackDispatchCommandPayload.TYPE,
                BLibCodecs.Stream.fromMojang(S2CItemStackDispatchCommandPayload.CODEC)
            )
        );
    }
}
