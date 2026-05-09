package com.blib.mod.common.network;

import com.blib.api.common.network.v1.PacketDirection;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

public class BLibPacketDirections {

    private static final BLibNetworkRegistry REGISTRY = BLib.MOD.registries().createNetworkRegistry();

    public static void initialize() {
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CChunkClaimsSyncPayload.TYPE, S2CChunkClaimsSyncPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityDataSyncPayload.TYPE, S2CEntityDataSyncPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CFactionMetadataSyncPayload.TYPE, S2CFactionMetadataSyncPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CGOAPDebugPayload.TYPE, S2CGOAPDebugPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CPathfindingSearchDebugPayload.TYPE, S2CPathfindingSearchDebugPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CPathfindingNavDebugPayload.TYPE, S2CPathfindingNavDebugPayload.CODEC)
        );

        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SGOAPTrackPayload.TYPE, C2SGOAPTrackPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SRemoveEntityPayload.TYPE, C2SRemoveEntityPayload.CODEC));
    }
}
