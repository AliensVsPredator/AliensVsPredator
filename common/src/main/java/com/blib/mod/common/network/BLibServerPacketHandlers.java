package com.blib.mod.common.network;

import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

public class BLibServerPacketHandlers {

    private static final BLibNetworkRegistry REGISTRY = BLib.MOD.registries().createNetworkRegistry();

    public static void initialize() {
        registerClientBoundPacketHandlers();
        registerServerBoundPacketHandlers();
    }

    private static void registerServerBoundPacketHandlers() {
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SGOAPTrackPayload.TYPE,
                C2SGOAPTrackPayload.CODEC,
                BLibServerListener::handleGOAPTrack
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRemoveEntityPayload.TYPE,
                C2SRemoveEntityPayload.CODEC,
                BLibServerListener::handleRemoveEntity
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SPlaceJigsawPiecePayload.TYPE,
                C2SPlaceJigsawPiecePayload.CODEC,
                BLibServerListener::handlePlaceJigsawPiece
            )
        );
    }

    private static void registerClientBoundPacketHandlers() {
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CChunkClaimsSyncPayload.TYPE,
                S2CChunkClaimsSyncPayload.CODEC,
                BLibClientListener::handleChunkClaimsSync
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CFactionMetadataSyncPayload.TYPE,
                S2CFactionMetadataSyncPayload.CODEC,
                BLibClientListener::handleFactionMetadataSync
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CEntityDataSyncPayload.TYPE,
                S2CEntityDataSyncPayload.CODEC,
                BLibClientListener::handleEntityDataSync
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CGOAPDebugPayload.TYPE,
                S2CGOAPDebugPayload.CODEC,
                BLibClientListener::handleGOAPDebug
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CPathfindingSearchDebugPayload.TYPE,
                S2CPathfindingSearchDebugPayload.CODEC,
                BLibClientListener::handlePathfindingSearchDebug
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CPathfindingNavDebugPayload.TYPE,
                S2CPathfindingNavDebugPayload.CODEC,
                BLibClientListener::handlePathfindingNavDebug
            )
        );
    }
}
