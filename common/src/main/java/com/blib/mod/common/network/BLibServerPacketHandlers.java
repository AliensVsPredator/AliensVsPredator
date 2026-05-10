package com.blib.mod.common.network;

import com.blib.api.common.network.v1.NetworkHandler;
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

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SUndoPlacementPayload.TYPE,
                C2SUndoPlacementPayload.CODEC,
                BLibServerListener::handleUndoPlacement
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SUpdateJigsawBlockPayload.TYPE,
                C2SUpdateJigsawBlockPayload.CODEC,
                BLibServerListener::handleUpdateJigsawBlock
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SUpdatePoolElementPayload.TYPE,
                C2SUpdatePoolElementPayload.CODEC,
                BLibServerListener::handleUpdatePoolElement
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SAddPoolElementPayload.TYPE,
                C2SAddPoolElementPayload.CODEC,
                BLibServerListener::handleAddPoolElement
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRemovePoolElementPayload.TYPE,
                C2SRemovePoolElementPayload.CODEC,
                BLibServerListener::handleRemovePoolElement
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SSavePoolPayload.TYPE,
                C2SSavePoolPayload.CODEC,
                BLibServerListener::handleSavePool
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SListProjectsPayload.TYPE,
                C2SListProjectsPayload.CODEC,
                BLibServerListener::handleListProjects
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SCreateProjectPayload.TYPE,
                C2SCreateProjectPayload.CODEC,
                BLibServerListener::handleCreateProject
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeleteProjectPayload.TYPE,
                C2SDeleteProjectPayload.CODEC,
                BLibServerListener::handleDeleteProject
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SOpenProjectPayload.TYPE,
                C2SOpenProjectPayload.CODEC,
                BLibServerListener::handleOpenProject
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SReloadProjectPayload.TYPE,
                C2SReloadProjectPayload.CODEC,
                BLibServerListener::handleReloadProject
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRequestPoolDraftPayload.TYPE,
                C2SRequestPoolDraftPayload.CODEC,
                BLibServerListener::handleRequestPoolDraft
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SCaptureBlocksPayload.TYPE,
                C2SCaptureBlocksPayload.CODEC,
                BLibServerListener::handleCaptureBlocks
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SListCapturesPayload.TYPE,
                C2SListCapturesPayload.CODEC,
                BLibServerListener::handleListCaptures
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeleteCapturePayload.TYPE,
                C2SDeleteCapturePayload.CODEC,
                BLibServerListener::handleDeleteCapture
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SMoveSelectionPayload.TYPE,
                C2SMoveSelectionPayload.CODEC,
                BLibServerListener::handleMoveSelection
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SCopySelectionPayload.TYPE,
                C2SCopySelectionPayload.CODEC,
                BLibServerListener::handleCopySelection
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SPasteFromClipboardPayload.TYPE,
                C2SPasteFromClipboardPayload.CODEC,
                BLibServerListener::handlePasteFromClipboard
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeleteSelectionPayload.TYPE,
                C2SDeleteSelectionPayload.CODEC,
                BLibServerListener::handleDeleteSelection
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

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CProjectListPayload.TYPE,
                S2CProjectListPayload.CODEC,
                BLibClientListener::handleProjectList
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CProjectOpResultPayload.TYPE,
                S2CProjectOpResultPayload.CODEC,
                BLibClientListener::handleProjectOpResult
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CPoolDraftPayload.TYPE,
                S2CPoolDraftPayload.CODEC,
                BLibClientListener::handlePoolDraft
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CCaptureListPayload.TYPE,
                S2CCaptureListPayload.CODEC,
                BLibClientListener::handleCaptureList
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CMoveSelectionResultPayload.TYPE,
                S2CMoveSelectionResultPayload.CODEC,
                BLibClientListener::handleMoveSelectionResult
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CClipboardStatusPayload.TYPE,
                S2CClipboardStatusPayload.CODEC,
                BLibClientListener::handleClipboardStatus
            )
        );
    }
}
