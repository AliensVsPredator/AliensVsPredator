package com.blib.mod.common.network;

import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SAddFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SAddPoolElementPayload;
import com.blib.mod.common.network.packet.C2SCaptureBlocksPayload;
import com.blib.mod.common.network.packet.C2SCopySelectionPayload;
import com.blib.mod.common.network.packet.C2SCreateFactionPayload;
import com.blib.mod.common.network.packet.C2SCreateProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteCapturePayload;
import com.blib.mod.common.network.packet.C2SDeleteFactionPayload;
import com.blib.mod.common.network.packet.C2SDeletePoolPayload;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SDeleteStructurePayload;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SListCapturesPayload;
import com.blib.mod.common.network.packet.C2SListPoolsPayload;
import com.blib.mod.common.network.packet.C2SListProjectsPayload;
import com.blib.mod.common.network.packet.C2SListStructuresPayload;
import com.blib.mod.common.network.packet.C2SMoveSelectionPayload;
import com.blib.mod.common.network.packet.C2SOpenProjectPayload;
import com.blib.mod.common.network.packet.C2SPasteFromClipboardPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRemoveFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SRemovePoolElementPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionInspectionPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionMembersPayload;
import com.blib.mod.common.network.packet.C2SRequestPoolDraftPayload;
import com.blib.mod.common.network.packet.C2SSavePoolPayload;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2SSetFactionRelationshipPayload;
import com.blib.mod.common.network.packet.C2SSpawnEntityPayload;
import com.blib.mod.common.network.packet.C2STranslateEntityPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;
import com.blib.mod.common.network.packet.C2SUpdateFactionFieldPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;
import com.blib.mod.common.network.packet.C2SUpdatePoolElementPayload;
import com.blib.mod.common.network.packet.S2CCaptureListPayload;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CClipboardStatusPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionDirectoryPayload;
import com.blib.mod.common.network.packet.S2CFactionInspectionPayload;
import com.blib.mod.common.network.packet.S2CFactionMembersPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CPoolListPayload;
import com.blib.mod.common.network.packet.S2CProjectListPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;
import com.blib.mod.common.network.packet.S2CStructureListPayload;

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
                C2SSpawnEntityPayload.TYPE,
                C2SSpawnEntityPayload.CODEC,
                BLibServerListener::handleSpawnEntity
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2STranslateEntityPayload.TYPE,
                C2STranslateEntityPayload.CODEC,
                BLibServerListener::handleTranslateEntity
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SSetEntityScalePayload.TYPE,
                C2SSetEntityScalePayload.CODEC,
                BLibServerListener::handleSetEntityScale
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
                C2SListPoolsPayload.TYPE,
                C2SListPoolsPayload.CODEC,
                BLibServerListener::handleListPools
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SListStructuresPayload.TYPE,
                C2SListStructuresPayload.CODEC,
                BLibServerListener::handleListStructures
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeletePoolPayload.TYPE,
                C2SDeletePoolPayload.CODEC,
                BLibServerListener::handleDeletePool
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeleteStructurePayload.TYPE,
                C2SDeleteStructurePayload.CODEC,
                BLibServerListener::handleDeleteStructure
            )
        );

        // Faction layout handlers — 3 request packets + 6 mutation packets.
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRequestFactionDirectoryPayload.TYPE,
                C2SRequestFactionDirectoryPayload.CODEC,
                BLibServerListener::handleRequestFactionDirectory
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRequestFactionInspectionPayload.TYPE,
                C2SRequestFactionInspectionPayload.CODEC,
                BLibServerListener::handleRequestFactionInspection
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRequestFactionMembersPayload.TYPE,
                C2SRequestFactionMembersPayload.CODEC,
                BLibServerListener::handleRequestFactionMembers
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SCreateFactionPayload.TYPE,
                C2SCreateFactionPayload.CODEC,
                BLibServerListener::handleCreateFaction
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SDeleteFactionPayload.TYPE,
                C2SDeleteFactionPayload.CODEC,
                BLibServerListener::handleDeleteFaction
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SUpdateFactionFieldPayload.TYPE,
                C2SUpdateFactionFieldPayload.CODEC,
                BLibServerListener::handleUpdateFactionField
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SSetFactionRelationshipPayload.TYPE,
                C2SSetFactionRelationshipPayload.CODEC,
                BLibServerListener::handleSetFactionRelationship
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SAddFactionMemberPayload.TYPE,
                C2SAddFactionMemberPayload.CODEC,
                BLibServerListener::handleAddFactionMember
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRemoveFactionMemberPayload.TYPE,
                C2SRemoveFactionMemberPayload.CODEC,
                BLibServerListener::handleRemoveFactionMember
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SAddChunkClaimPayload.TYPE,
                C2SAddChunkClaimPayload.CODEC,
                BLibServerListener::handleAddChunkClaim
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRemoveChunkClaimPayload.TYPE,
                C2SRemoveChunkClaimPayload.CODEC,
                BLibServerListener::handleRemoveChunkClaim
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
                S2CPoolListPayload.TYPE,
                S2CPoolListPayload.CODEC,
                BLibClientListener::handlePoolList
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CStructureListPayload.TYPE,
                S2CStructureListPayload.CODEC,
                BLibClientListener::handleStructureList
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CFactionDirectoryPayload.TYPE,
                S2CFactionDirectoryPayload.CODEC,
                BLibClientListener::handleFactionDirectory
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CFactionInspectionPayload.TYPE,
                S2CFactionInspectionPayload.CODEC,
                BLibClientListener::handleFactionInspection
            )
        );
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CFactionMembersPayload.TYPE,
                S2CFactionMembersPayload.CODEC,
                BLibClientListener::handleFactionMembers
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
