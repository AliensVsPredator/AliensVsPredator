package com.blib.mod.common.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.blockselection.BlockSelectionClipboard;
import com.blib.engine.jigsaw.ProjectDraftCache;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.selection.TagSelectable;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.RegistryEntriesCache;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagDraftCache;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.BLib;
import com.blib.mod.client.render.debug.PathfindingNavDebugHUD;
import com.blib.mod.client.render.debug.PathfindingSearchDebugRenderer;
import com.blib.mod.client.render.goap.GOAPDebugHUD;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.ProjectOp;
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
import com.blib.mod.common.network.packet.S2CRegistryEntriesPayload;
import com.blib.mod.common.network.packet.S2CTagCatalogPayload;
import com.blib.mod.common.network.packet.S2CTagDraftPayload;

@ApiStatus.Internal
public final class BLibClientListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibClientListener.class);

    public static void handleChunkClaimsSync(S2CChunkClaimsSyncPayload payload, Player player) {
        ClientTerritoryCache.INSTANCE.updateChunk(payload.chunkX(), payload.chunkZ(), payload.factionIds());
    }

    public static void handleFactionMetadataSync(S2CFactionMetadataSyncPayload payload, Player player) {
        ClientFactionCache.INSTANCE.update(payload.factionId(), payload.name(), payload.color());
    }

    public static void handleEntityDataSync(S2CEntityDataSyncPayload entityDataSyncPayload, Player player) {
        var targetEntity = player.level().getEntity(entityDataSyncPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var dataContainer = ((DataUser) targetEntity).getDataContainer();

        entityDataSyncPayload.rawDataSyncMap()
            .rawDataById()
            .forEach(dataContainer::set);
    }

    public static void handleGOAPDebug(S2CGOAPDebugPayload payload, Player player) {
        GOAPDebugHUD.INSTANCE.update(payload);
    }

    public static void handlePathfindingSearchDebug(S2CPathfindingSearchDebugPayload payload, Player player) {
        PathfindingSearchDebugRenderer.INSTANCE.update(payload);
    }

    public static void handlePathfindingNavDebug(S2CPathfindingNavDebugPayload payload, Player player) {
        PathfindingNavDebugHUD.INSTANCE.update(payload);
    }

    /**
     * Refresh the project picker's cached project list. The picker re-renders from {@link ProjectSession} on every
     * frame, so updating the field is enough — no explicit redraw call.
     */
    public static void handleProjectList(S2CProjectListPayload payload, Player player) {
        ProjectSession.setAvailableProjects(payload.projects());
    }

    /**
     * Result of a project-management op. SUCCESS for OPEN means the picker can transition to the workspace; SUCCESS for
     * RELOAD means the live registry now matches the project's on-disk state and the draft cache should be dropped so
     * the next pool view re-fetches fresh. FAILURE in any op surfaces as a logged error for now — Phase 4 wires
     * picker-side toast UI for user-facing feedback.
     */
    public static void handleProjectOpResult(S2CProjectOpResultPayload payload, Player player) {
        var op = payload.op();
        if (!payload.success()) {
            LOGGER.warn("[BLib] Project op {} on '{}' failed: {}", op, payload.projectName(), payload.errorMessage());
        }
        if (op == ProjectOp.RELOAD && payload.success()) {
            ProjectDraftCache.clear();
            TagDraftCache.clear();
            // The inspector's tag-view drift-detect only re-fetches on (registryKey, tagId) changes, not on cache
            // invalidation — so without an explicit refetch here, the inspector renders blank for the currently-
            // selected tag until the user clicks a different tag and back. Kick a fresh request immediately so the
            // post-reload state lands in the cache before the inspector's next render.
            var single = SelectionManager.current().single();
            if (single instanceof TagSelectable ts) {
                BLib.MOD.networking()
                    .sendToServer(new C2SRequestTagDraftPayload(ProjectSession.activeProjectName(), ts.registryKey(), ts.tagId()));
            }
        }
        // CAPTURE results go to BlockSelection so the Capture Panel can pick them up next render — the picker's
        // callback channel is for project create/delete/open/reload, and the panel is its own consumer.
        if (op == ProjectOp.CAPTURE) {
            BlockSelection.setPendingCaptureResult(payload);
        }
        // Hand off to whatever screen is currently waiting on op replies (typically the picker). The session no-ops
        // when no callback is registered, so a delayed reply that lands after the picker closed is harmless.
        ProjectSession.deliverOpResult(payload);
    }

    /**
     * Server-pushed authoritative state for a pool in the active project. Stored in {@link ProjectDraftCache}; the Pool
     * Editor reads from there instead of the live registry so user-typed edits are reflected pre-reload.
     */
    public static void handlePoolDraft(S2CPoolDraftPayload payload, Player player) {
        ProjectDraftCache.update(payload.poolId(), payload.elements());
    }

    /**
     * Server-pushed authoritative state for a tag in the active project. Stored in {@link TagDraftCache}; the Tag
     * Editor reads from there instead of the live registry so user-typed edits are reflected pre-reload.
     */
    public static void handleTagDraft(S2CTagDraftPayload payload, Player player) {
        TagDraftCache.update(payload.registryKey(), payload.tagId(), payload.replace(), payload.entries(), payload.resolvedMembers());
        // Sync the catalog's inProject flag with the server's on-disk truth. Read-only draft fetches keep
        // inProject=false (no file written); edit handlers may flip it true OR false (auto-cleanup deletes the
        // file when an edit leaves the JSON equivalent to upstream). setInProject handles either direction.
        TagCatalogCache.setInProject(payload.registryKey(), payload.tagId(), payload.inProject());
        // Keep equivalentToUpstream in sync with what the fresh draft tells us. Cheap to compute client-side:
        // replace=false AND every entry inUpstream ⇒ project's JSON is redundant. Lets the browser repaint the row
        // gray the moment the user's edits cancel out their previous additions, without waiting for the full
        // catalog re-push.
        var equivalent = payload.inProject()
            && !payload.replace()
            && payload.entries().stream().allMatch(com.blib.mod.common.network.packet.TagEntryDraft::inUpstream);
        TagCatalogCache.setEquivalentToUpstream(payload.registryKey(), payload.tagId(), equivalent);
    }

    /**
     * Server-pushed full tag catalog for the active project. Stored in {@link TagCatalogCache}; the Tag Browser reads
     * from there to render its collapsible-by-registry sections. Sent in response to a request and after each
     * Create/Delete tag operation.
     */
    public static void handleTagCatalog(S2CTagCatalogPayload payload, Player player) {
        TagCatalogCache.update(payload.entries());
    }

    /**
     * Server-pushed element + tag-name lists for one registry. Stored in {@link RegistryEntriesCache}; the Tag Editor's
     * Add-entry picker reads from there to populate its searchable item list (direct entries + tag refs).
     */
    public static void handleRegistryEntries(S2CRegistryEntriesPayload payload, Player player) {
        RegistryEntriesCache.update(payload.registryKey(), payload.entries(), payload.tagIds());
    }

    /**
     * Server-pushed list of captures for the active project. Stored in {@link BlockSelection} so the Capture Panel can
     * render the captures list without round-tripping. Sent in response to a list request, after a successful capture,
     * and after a delete.
     */
    public static void handleCaptureList(S2CCaptureListPayload payload, Player player) {
        BlockSelection.setCaptures(payload.captureNames());
    }

    /** Server-pushed list of project pools. Stored in {@link com.blib.engine.projectcontents.ProjectContents}. */
    public static void handlePoolList(com.blib.mod.common.network.packet.S2CPoolListPayload payload, Player player) {
        com.blib.engine.projectcontents.ProjectContents.setPools(payload.poolIds());
    }

    /** Server-pushed list of project structures. Stored in {@link com.blib.engine.projectcontents.ProjectContents}. */
    public static void handleStructureList(com.blib.mod.common.network.packet.S2CStructureListPayload payload, Player player) {
        com.blib.engine.projectcontents.ProjectContents.setStructures(payload.structureIds());
    }

    /** Server-pushed faction directory snapshot. Replaces the workspace's directory cache. */
    public static void handleFactionDirectory(
        com.blib.mod.common.network.packet.S2CFactionDirectoryPayload payload,
        Player player
    ) {
        com.blib.internal.client.faction.ClientFactionDirectoryCache.apply(payload);
    }

    /** Server-pushed inspector snapshot for a single faction. */
    public static void handleFactionInspection(
        com.blib.mod.common.network.packet.S2CFactionInspectionPayload payload,
        Player player
    ) {
        com.blib.internal.client.faction.ClientFactionInspectionCache.apply(payload);
    }

    /** Server-pushed member roster for a single faction. */
    public static void handleFactionMembers(
        com.blib.mod.common.network.packet.S2CFactionMembersPayload payload,
        Player player
    ) {
        com.blib.internal.client.faction.ClientFactionMembersCache.apply(payload);
    }

    /** Server-pushed reverse lookup: every faction this entity (UUID) currently belongs to. */
    public static void handleEntityFactions(
        com.blib.mod.common.network.packet.S2CEntityFactionsPayload payload,
        Player player
    ) {
        com.blib.internal.client.faction.ClientEntityFactionsCache.apply(payload);
    }

    /**
     * Server reply for a Move Blocks operation. On success and non-copy, shifts the AABB by the offset that was sent —
     * Photoshop's "marquee follows the dropped pixels" pattern, so the user's selection now wraps the moved blocks and
     * the next operation applies to them. Copy mode leaves the AABB at the original location since the source is still
     * there. In all cases clears {@code moveOffset} (ghost disappears) and stashes the payload for the panel's status
     * display.
     */
    public static void handleMoveSelectionResult(S2CMoveSelectionResultPayload payload, Player player) {
        var offset = BlockSelection.moveOffset();
        if (payload.success() && offset != null && !BlockSelection.moveCopyMode()) {
            // translateCorners rather than setBounds so the user's A/B labels stay attached to the same physical
            // corners — every successful move keeps the corner-marker positions stable relative to the moved volume.
            BlockSelection.translateCorners(offset.getX(), offset.getY(), offset.getZ());
        }
        BlockSelection.setMoveOffset(null);
        BlockSelection.setPendingMoveResult(payload);
    }

    /**
     * Server-pushed mirror of clipboard state. Updates {@link BlockSelectionClipboard} so the panel knows whether to
     * enable the Paste button and what size the pasted volume will be. Carries no NBT — only metadata.
     */
    public static void handleClipboardStatus(S2CClipboardStatusPayload payload, Player player) {
        BlockSelectionClipboard.update(payload.hasContents(), payload.sizeX(), payload.sizeY(), payload.sizeZ(), payload.filledAtMs());
    }

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
