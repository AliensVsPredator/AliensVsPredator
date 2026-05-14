package com.blib.mod.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.dismemberment.v1.LimbCategory;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.api.common.dismemberment.v1.SpawnFunctionRegistry;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionClipboard;
import com.blib.engine.domain.selection.volume.PendingCaptureResult;
import com.blib.engine.domain.selection.volume.PendingMoveResult;
import com.blib.engine.history.ClientActionHistory;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.jigsaw.ProjectDraftCache;
import com.blib.engine.projectcontents.ProjectContents;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.RegistryEntriesCache;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagDraftCache;
import com.blib.internal.client.faction.ClientEntityFactionsCache;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.internal.client.faction.ClientFactionInspectionCache;
import com.blib.internal.client.faction.ClientFactionMembersCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.internal.common.storage.EngineProjectIO;
import com.blib.mod.BLib;
import com.blib.mod.client.render.debug.PathfindingDebugState;
import com.blib.mod.client.render.debug.PathfindingSearchDebugRenderer;
import com.blib.mod.client.render.goap.GOAPDebugState;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.ProjectOp;
import com.blib.mod.common.network.packet.S2CActionHistorySyncPayload;
import com.blib.mod.common.network.packet.S2CAddPlacedPiecePayload;
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
import com.blib.mod.common.network.packet.S2CLimbDefinitionsSyncPayload;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CPoolListPayload;
import com.blib.mod.common.network.packet.S2CProjectListPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;
import com.blib.mod.common.network.packet.S2CRegistryEntriesPayload;
import com.blib.mod.common.network.packet.S2CRemovePlacedPiecePayload;
import com.blib.mod.common.network.packet.S2CStructureListPayload;
import com.blib.mod.common.network.packet.S2CSyncPlacedPiecesPayload;
import com.blib.mod.common.network.packet.S2CTagCatalogPayload;
import com.blib.mod.common.network.packet.S2CTagDraftPayload;
import com.blib.mod.common.network.packet.TagEntryDraft;

@ApiStatus.Internal
public final class BLibClientListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibClientListener.class);

    public static void handleChunkClaimsSync(S2CChunkClaimsSyncPayload payload, Player player) {
        if (payload.replaceArea()) {
            ClientTerritoryCache.INSTANCE.replaceArea(
                payload.dimension(),
                payload.minChunkX(),
                payload.minChunkZ(),
                payload.maxChunkX(),
                payload.maxChunkZ(),
                payload.entries()
            );
        } else {
            ClientTerritoryCache.INSTANCE.updateChunks(payload.dimension(), payload.entries());
        }
    }

    public static void handleActionHistorySync(S2CActionHistorySyncPayload payload, Player player) {
        ClientActionHistory.INSTANCE.update(payload.entries(), payload.undoCursor());
    }

    public static void handleFactionMetadataSync(S2CFactionMetadataSyncPayload payload, Player player) {
        ClientFactionCache.INSTANCE.update(payload.factionId(), payload.name(), payload.color());
    }

    /**
     * Replace tier-2 of {@link LimbDefinitionRegistry} with the server's snapshot. The reconstructed
     * {@link LimbDefinition}s carry {@link SpawnFunctionRegistry#DEFAULT_PROVIDER} as a sentinel; the client never
     * invokes the provider (spawn position is server-computed and arrives baked into the limb entity's position), so
     * the no-op default is safe.
     */
    public static void handleLimbDefinitionsSync(S2CLimbDefinitionsSyncPayload payload, Player player) {
        var next =
            new LinkedHashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var bucket : payload.entries()) {
            var perEntity = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            for (var entry : bucket.limbs()) {
                var def = new LimbDefinition(
                    entry.limbId(),
                    new LimbCategory(entry.categoryId()),
                    SpawnFunctionRegistry.DEFAULT_PROVIDER,
                    entry.fatal()
                );
                perEntity.put(entry.limbId(), def);
            }
            next.put(bucket.entityTypeId(), perEntity);
        }
        LimbDefinitionRegistry.replaceTier2(next);
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
        GOAPDebugState.INSTANCE.update(payload);
    }

    public static void handlePathfindingSearchDebug(S2CPathfindingSearchDebugPayload payload, Player player) {
        PathfindingSearchDebugRenderer.INSTANCE.update(payload);
    }

    public static void handlePathfindingNavDebug(S2CPathfindingNavDebugPayload payload, Player player) {
        PathfindingDebugState.INSTANCE.update(payload);
    }

    /**
     * Full snapshot of placed pieces for the current dimension — replaces, not merges. Sent in response to a client's
     * engine-mode-entry request. Pieces for a different dimension shouldn't appear in {@code payload.pieces()} because
     * the server filters, but we don't double-check here; the hover raycast filters by dimension on every frame anyway.
     */
    public static void handleSyncPlacedPieces(S2CSyncPlacedPiecesPayload payload, Player player) {
        ClientPlacedPieceRegistry.replaceAll(payload.pieces());
    }

    public static void handleAddPlacedPiece(S2CAddPlacedPiecePayload payload, Player player) {
        ClientPlacedPieceRegistry.add(payload.piece());
    }

    public static void handleRemovePlacedPiece(S2CRemovePlacedPiecePayload payload, Player player) {
        ClientPlacedPieceRegistry.remove(payload.id());
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
            // Server-side reload only refreshes the data pack — the client's resource pack (where the engine writes
            // item-renderer configs and other client assets) needs its own reload to pick up files that landed under
            // <project>/resourcepack/. Mirrors the datapack pipeline in EngineProjectIO.reloadProject: rescan the
            // repo, ensure the project pack id is in the selected list, then trigger a full reload. Scheduled on the
            // render thread; reloadResourcePacks and setSelected are not safe from the network thread.
            Minecraft.getInstance().execute(() -> {
                var mc = Minecraft.getInstance();
                var packRepo = mc.getResourcePackRepository();
                var packId = EngineProjectIO.PACK_ID_PREFIX + payload.projectName();

                // Rescan first so a pack whose resourcepack/ subdir was created lazily on first auto-save (see
                // EngineProjectIO.writeAssetJson) becomes visible to the setSelected call below.
                packRepo.reload();

                if (packRepo.getAvailableIds().contains(packId)) {
                    var selected = new ArrayList<>(packRepo.getSelectedIds());

                    if (!selected.contains(packId)) {
                        // Append: lowest priority, same as the datapack side. Other resource packs (mods, vanilla)
                        // continue to override entries the project pack hasn't redefined.
                        selected.add(packId);
                        packRepo.setSelected(selected);
                    }
                }

                mc.reloadResourcePacks();
            });
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
        // Deselect a deleted project's resource pack from the client repo before vanilla notices the files are gone.
        // Mirrors EngineProjectIO.deleteProject which deselects the datapack server-side before deleting files;
        // without this, a previously-Reload-selected project pack would linger in the selected list after deletion
        // and the next reload would warn about a missing pack id.
        if (op == ProjectOp.DELETE && payload.success()) {
            Minecraft.getInstance().execute(() -> {
                var mc = Minecraft.getInstance();
                var packRepo = mc.getResourcePackRepository();
                var packId = EngineProjectIO.PACK_ID_PREFIX + payload.projectName();
                var selected = new ArrayList<>(packRepo.getSelectedIds());

                if (selected.remove(packId)) {
                    packRepo.setSelected(selected);
                    mc.reloadResourcePacks();
                }
            });
        }
        // CAPTURE results go to BlockSelection so the Capture Panel can pick them up next render — the picker's
        // callback channel is for project create/delete/open/reload, and the panel is its own consumer. Wrap the
        // network payload in the domain {@link PendingCaptureResult} so BlockSelection doesn't import packet types.
        if (op == ProjectOp.CAPTURE) {
            BlockSelection.setPendingCaptureResult(
                new PendingCaptureResult(payload.success(), payload.projectName(), payload.errorMessage())
            );
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
            && payload.entries().stream().allMatch(TagEntryDraft::inUpstream);
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
    public static void handlePoolList(S2CPoolListPayload payload, Player player) {
        ProjectContents.setPools(payload.poolIds());
    }

    /** Server-pushed list of project structures. Stored in {@link com.blib.engine.projectcontents.ProjectContents}. */
    public static void handleStructureList(S2CStructureListPayload payload, Player player) {
        ProjectContents.setStructures(payload.structureIds());
    }

    /** Server-pushed faction directory snapshot. Replaces the workspace's directory cache. */
    public static void handleFactionDirectory(
        S2CFactionDirectoryPayload payload,
        Player player
    ) {
        ClientFactionDirectoryCache.apply(payload);
    }

    /** Server-pushed inspector snapshot for a single faction. */
    public static void handleFactionInspection(
        S2CFactionInspectionPayload payload,
        Player player
    ) {
        ClientFactionInspectionCache.apply(payload);
    }

    /** Server-pushed member roster for a single faction. */
    public static void handleFactionMembers(
        S2CFactionMembersPayload payload,
        Player player
    ) {
        ClientFactionMembersCache.apply(payload);
    }

    /** Server-pushed reverse lookup: every faction this entity (UUID) currently belongs to. */
    public static void handleEntityFactions(
        S2CEntityFactionsPayload payload,
        Player player
    ) {
        ClientEntityFactionsCache.apply(payload);
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
        BlockSelection.setPendingMoveResult(new PendingMoveResult(payload.success(), payload.message(), payload.blockCount()));
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
