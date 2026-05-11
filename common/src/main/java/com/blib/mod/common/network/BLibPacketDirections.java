package com.blib.mod.common.network;

import com.blib.api.common.network.v1.PacketDirection;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SAddFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SAddPoolElementPayload;
import com.blib.mod.common.network.packet.C2SAddTagEntryPayload;
import com.blib.mod.common.network.packet.C2SCaptureBlocksPayload;
import com.blib.mod.common.network.packet.C2SCopySelectionPayload;
import com.blib.mod.common.network.packet.C2SCreateFactionPayload;
import com.blib.mod.common.network.packet.C2SCreateProjectPayload;
import com.blib.mod.common.network.packet.C2SCreateTagPayload;
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
import com.blib.mod.common.network.packet.C2SRemoveTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRequestEntityFactionsPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionInspectionPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionMembersPayload;
import com.blib.mod.common.network.packet.C2SRequestPoolDraftPayload;
import com.blib.mod.common.network.packet.C2SRequestRegistryEntriesPayload;
import com.blib.mod.common.network.packet.C2SRequestTagCatalogPayload;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.C2SSavePoolPayload;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2SSetFactionRelationshipPayload;
import com.blib.mod.common.network.packet.C2SSetTagReplacePayload;
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
import com.blib.mod.common.network.packet.S2CEntityFactionsPayload;
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
import com.blib.mod.common.network.packet.S2CRegistryEntriesPayload;
import com.blib.mod.common.network.packet.S2CStructureListPayload;
import com.blib.mod.common.network.packet.S2CTagCatalogPayload;
import com.blib.mod.common.network.packet.S2CTagDraftPayload;

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
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SListPoolsPayload.TYPE, C2SListPoolsPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SListStructuresPayload.TYPE, C2SListStructuresPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SDeletePoolPayload.TYPE, C2SDeletePoolPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SDeleteStructurePayload.TYPE, C2SDeleteStructurePayload.CODEC)
        );

        // Faction authoring layout — three S2C list packets, three C2S request packets, six C2S mutation packets.
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CFactionDirectoryPayload.TYPE, S2CFactionDirectoryPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CFactionInspectionPayload.TYPE, S2CFactionInspectionPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CFactionMembersPayload.TYPE, S2CFactionMembersPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestFactionDirectoryPayload.TYPE, C2SRequestFactionDirectoryPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestFactionInspectionPayload.TYPE, C2SRequestFactionInspectionPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestFactionMembersPayload.TYPE, C2SRequestFactionMembersPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SCreateFactionPayload.TYPE, C2SCreateFactionPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SDeleteFactionPayload.TYPE, C2SDeleteFactionPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SUpdateFactionFieldPayload.TYPE, C2SUpdateFactionFieldPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SSetFactionRelationshipPayload.TYPE, C2SSetFactionRelationshipPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SAddFactionMemberPayload.TYPE, C2SAddFactionMemberPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRemoveFactionMemberPayload.TYPE, C2SRemoveFactionMemberPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestEntityFactionsPayload.TYPE, C2SRequestEntityFactionsPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityFactionsPayload.TYPE, S2CEntityFactionsPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SAddChunkClaimPayload.TYPE, C2SAddChunkClaimPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRemoveChunkClaimPayload.TYPE, C2SRemoveChunkClaimPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CPoolListPayload.TYPE, S2CPoolListPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CStructureListPayload.TYPE, S2CStructureListPayload.CODEC)
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

        // Tag editor — 3 S2C + 8 C2S.
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CTagDraftPayload.TYPE, S2CTagDraftPayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CTagCatalogPayload.TYPE, S2CTagCatalogPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CRegistryEntriesPayload.TYPE, S2CRegistryEntriesPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestTagCatalogPayload.TYPE, C2SRequestTagCatalogPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestRegistryEntriesPayload.TYPE, C2SRequestRegistryEntriesPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestTagDraftPayload.TYPE, C2SRequestTagDraftPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SAddTagEntryPayload.TYPE, C2SAddTagEntryPayload.CODEC));
        REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRemoveTagEntryPayload.TYPE, C2SRemoveTagEntryPayload.CODEC)
        );
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SSetTagReplacePayload.TYPE, C2SSetTagReplacePayload.CODEC));
        REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SCreateTagPayload.TYPE, C2SCreateTagPayload.CODEC));
    }
}
