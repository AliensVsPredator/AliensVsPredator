package com.blib.mod.common.network;

import com.blib.api.common.network.v1.PacketDirection;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddPoolElementPayload;
import com.blib.mod.common.network.packet.C2SCaptureBlocksPayload;
import com.blib.mod.common.network.packet.C2SCopySelectionPayload;
import com.blib.mod.common.network.packet.C2SCreateProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteCapturePayload;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SListCapturesPayload;
import com.blib.mod.common.network.packet.C2SListProjectsPayload;
import com.blib.mod.common.network.packet.C2SMoveSelectionPayload;
import com.blib.mod.common.network.packet.C2SOpenProjectPayload;
import com.blib.mod.common.network.packet.C2SPasteFromClipboardPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRemovePoolElementPayload;
import com.blib.mod.common.network.packet.C2SRequestPoolDraftPayload;
import com.blib.mod.common.network.packet.C2SSavePoolPayload;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2SSpawnEntityPayload;
import com.blib.mod.common.network.packet.C2STranslateEntityPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;
import com.blib.mod.common.network.packet.C2SUpdatePoolElementPayload;
import com.blib.mod.common.network.packet.S2CCaptureListPayload;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CClipboardStatusPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CProjectListPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;

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
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CProjectListPayload.TYPE, S2CProjectListPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CProjectOpResultPayload.TYPE, S2CProjectOpResultPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CPoolDraftPayload.TYPE, S2CPoolDraftPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CCaptureListPayload.TYPE, S2CCaptureListPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CMoveSelectionResultPayload.TYPE, S2CMoveSelectionResultPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CClipboardStatusPayload.TYPE, S2CClipboardStatusPayload.CODEC)
        );

        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SGOAPTrackPayload.TYPE, C2SGOAPTrackPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SRemoveEntityPayload.TYPE, C2SRemoveEntityPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SSpawnEntityPayload.TYPE, C2SSpawnEntityPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2STranslateEntityPayload.TYPE, C2STranslateEntityPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SSetEntityScalePayload.TYPE, C2SSetEntityScalePayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SPlaceJigsawPiecePayload.TYPE, C2SPlaceJigsawPiecePayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SUndoPlacementPayload.TYPE, C2SUndoPlacementPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SUpdateJigsawBlockPayload.TYPE, C2SUpdateJigsawBlockPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SUpdatePoolElementPayload.TYPE, C2SUpdatePoolElementPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SAddPoolElementPayload.TYPE, C2SAddPoolElementPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRemovePoolElementPayload.TYPE, C2SRemovePoolElementPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SSavePoolPayload.TYPE, C2SSavePoolPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SListProjectsPayload.TYPE, C2SListProjectsPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SCreateProjectPayload.TYPE, C2SCreateProjectPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SDeleteProjectPayload.TYPE, C2SDeleteProjectPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SOpenProjectPayload.TYPE, C2SOpenProjectPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SReloadProjectPayload.TYPE, C2SReloadProjectPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestPoolDraftPayload.TYPE, C2SRequestPoolDraftPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SCaptureBlocksPayload.TYPE, C2SCaptureBlocksPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SListCapturesPayload.TYPE, C2SListCapturesPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SDeleteCapturePayload.TYPE, C2SDeleteCapturePayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SMoveSelectionPayload.TYPE, C2SMoveSelectionPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SCopySelectionPayload.TYPE, C2SCopySelectionPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SPasteFromClipboardPayload.TYPE, C2SPasteFromClipboardPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SDeleteSelectionPayload.TYPE, C2SDeleteSelectionPayload.CODEC)
        );
    }
}
