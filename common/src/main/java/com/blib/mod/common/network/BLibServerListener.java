package com.blib.mod.common.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.internal.common.capture.BlockCaptureEngine;
import com.blib.internal.common.capture.CaptureMode;
import com.blib.internal.common.clipboard.BlockClipboardEngine;
import com.blib.internal.common.clipboard.ServerBlockClipboard;
import com.blib.internal.common.faction.BLibFactionManager;
import com.blib.internal.common.move.BlockMoveEngine;
import com.blib.internal.common.storage.EngineProjectIO;
import com.blib.internal.common.storage.ProjectDraftStore;
import com.blib.internal.common.territory.BLibTerritoryManager;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.gameplay.jigsaw.PlacementHistory;
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
import com.blib.mod.common.network.packet.ProjectOp;
import com.blib.mod.common.network.packet.S2CCaptureListPayload;
import com.blib.mod.common.network.packet.S2CClipboardStatusPayload;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CPoolListPayload;
import com.blib.mod.common.network.packet.S2CProjectListPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;
import com.blib.mod.common.network.packet.S2CStructureListPayload;

/**
 * Server-side handlers for client → server packets. Mirror of {@link BLibClientListener} for the C2S direction — each
 * handler is invoked on the server thread with the payload + the sending {@link Player}.
 */
@ApiStatus.Internal
public final class BLibServerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibServerListener.class);

    private BLibServerListener() {}

    public static void handleGOAPTrack(C2SGOAPTrackPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var entity = serverPlayer.serverLevel().getEntity(payload.entityId());
        if (!(entity instanceof LivingEntity living) || !(living instanceof GOAPUser<?>)) {
            return;
        }

        GOAPDebugTracker.INSTANCE.track(serverPlayer.getUUID(), List.of(living.getUUID()));
    }

    /**
     * Removes the entity referenced by {@code payload.entityId} from the world. Authorized for ops only (perm level 2)
     * since entity deletion is editor-grade destructive — same threshold as vanilla {@code /kill}. Players are never
     * targetable through this packet; the engine workspace's right-click menu also doesn't surface the option for
     * players, but the server enforces it as a defense-in-depth.
     */
    public static void handleRemoveEntity(C2SRemoveEntityPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        Entity entity = serverPlayer.serverLevel().getEntity(payload.entityId());
        if (entity == null || entity instanceof Player) {
            return;
        }

        entity.discard();
    }

    /**
     * Spawn an entity at the requested anchor as a UI replacement for {@code /summon}. Op-gated like the other
     * entity-mutating handlers — same threshold as the underlying command. Refuses spawns that the difficulty would
     * make pointless ({@code peaceful} + {@link MobCategory#MONSTER}) so the user gets a clean rejection at the UI
     * layer instead of a despawn-on-next-tick mystery; vanilla {@code /summon} permits these but they're confusing in
     * an authoring-tool context. Also respects {@link net.minecraft.world.entity.EntityType#canSummon} so
     * non-summonable types (lightning bolt, fishing bobber, etc.) silently no-op rather than crashing the spawn
     * pipeline.
     */
    public static void handleSpawnEntity(C2SSpawnEntityPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var server = serverPlayer.server;
        var dimKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var level = server.getLevel(dimKey);
        if (level == null) {
            return;
        }

        var typeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(payload.entityTypeId());
        if (typeOpt.isEmpty()) {
            return;
        }
        var type = typeOpt.get();

        if (!type.canSummon()) {
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL && type.getCategory() == MobCategory.MONSTER) {
            return;
        }

        type.spawn(level, payload.anchor(), MobSpawnType.COMMAND);
    }

    /**
     * Teleport the entity referenced by {@code payload} to the supplied world coordinates. Op-gated; refuses player
     * targets and silently no-ops if the dimension or entity can't be resolved (the engine's translate gizmo only fires
     * this packet on a real drag against a live entity, but the handler stays defensive).
     */
    public static void handleTranslateEntity(C2STranslateEntityPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var dimKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var level = sp.server.getLevel(dimKey);
        if (level == null) {
            return;
        }
        var entity = level.getEntity(payload.entityId());
        if (entity == null || entity instanceof Player) {
            return;
        }
        entity.teleportTo(payload.x(), payload.y(), payload.z());
    }

    /**
     * Set the entity's {@link net.minecraft.world.entity.ai.attributes.Attributes#SCALE} attribute. Op-gated; refuses
     * player targets and silently no-ops on entities whose attribute map doesn't include SCALE (some mob types may not
     * have it registered). Clamped to {@code [0.1, 4.0]} so a malformed packet can't push an entity to an unusable
     * size.
     */
    public static void handleSetEntityScale(C2SSetEntityScalePayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var dimKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var level = sp.server.getLevel(dimKey);
        if (level == null) {
            return;
        }
        var entity = level.getEntity(payload.entityId());
        if (!(entity instanceof LivingEntity le) || le instanceof Player) {
            return;
        }
        var attr = le.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        if (attr == null) {
            return;
        }
        var clamped = Math.max(0.1, Math.min(4.0, payload.scale()));
        attr.setBaseValue(clamped);
    }

    /**
     * Place a structure template at the requested anchor. Authorized for ops only — placement writes blocks into the
     * world, same destructiveness threshold as {@code /setblock}. Looks up the template by id from the world's
     * {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager}; silently no-ops if
     * the id doesn't resolve (the client and server might be out of sync if the user reloaded resources mid-session).
     * The {@code Block.UPDATE_CLIENTS} flag (2) ensures placed blocks sync back to the client without triggering
     * neighbor updates that would corrupt placed structures (e.g. lit redstone repeaters firing on placement).
     */
    public static void handlePlaceJigsawPiece(C2SPlaceJigsawPiecePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var serverLevel = serverPlayer.serverLevel();
        var manager = serverLevel.getStructureManager();
        var template = manager.get(payload.templateId()).orElse(null);
        if (template == null) {
            return;
        }

        var rotation = ordinalToRotation(payload.rotationOrdinal());
        var mirror = ordinalToMirror(payload.mirrorOrdinal());

        var settings = new StructurePlaceSettings()
            .setRotation(rotation)
            .setMirror(mirror)
            .setIgnoreEntities(false);

        var anchor = payload.anchor();
        // Capture the AABB the placement will touch BEFORE writing — otherwise the snapshot would record the placed
        // structure's blocks as the "original" state and undo would be a no-op. AABB derived from the same settings
        // we're about to feed placeInWorld so it matches exactly.
        var aabb = template.getBoundingBox(settings, anchor);
        PlacementHistory.push(serverLevel, aabb, payload.templateId());

        template.placeInWorld(serverLevel, anchor, anchor, settings, serverLevel.getRandom(), Block.UPDATE_CLIENTS);
    }

    /**
     * Pop the most recent placement off the {@link PlacementHistory} stack and restore the world. Op-gated like the
     * place packet — destructive write to the world. Filters by the player's current dimension so a player who placed
     * in the overworld and travelled to the nether before pressing undo doesn't accidentally restore overworld blocks
     * at the same coordinates in the nether.
     */
    public static void handleUndoPlacement(C2SUndoPlacementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        PlacementHistory.undo(serverPlayer.serverLevel());
    }

    /**
     * Apply the editable NBT fields from the engine workspace's jigsaw-block inspector to the live
     * {@link JigsawBlockEntity} at {@code payload.pos}. Op-gated like the other block-mutating handlers — same
     * threshold as {@code /data merge block}. Joint ordinal is bounds-checked so a malformed packet can't crash the
     * server with an {@link ArrayIndexOutOfBoundsException}.
     * <p>
     * After updating the block-entity fields we mark it changed (chunk save) and re-broadcast the block state to
     * clients via {@link net.minecraft.world.level.Level#sendBlockUpdated} so any nearby observer sees the new NBT on
     * their next BE sync — without this, the inspector that just sent the packet would see stale state until the chunk
     * happened to re-sync for some other reason.
     */
    public static void handleUpdateJigsawBlock(C2SUpdateJigsawBlockPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var level = serverPlayer.serverLevel();
        if (!(level.getBlockEntity(payload.pos()) instanceof JigsawBlockEntity jigsaw)) {
            return;
        }

        var jointValues = JigsawBlockEntity.JointType.values();
        var jointOrdinal = payload.jointOrdinal();
        if (jointOrdinal < 0 || jointOrdinal >= jointValues.length) {
            return;
        }

        jigsaw.setName(payload.name());
        jigsaw.setTarget(payload.target());
        jigsaw.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, payload.pool()));
        jigsaw.setJoint(jointValues[jointOrdinal]);
        jigsaw.setFinalState(payload.finalState());

        jigsaw.setChanged();
        var state = level.getBlockState(payload.pos());
        level.sendBlockUpdated(payload.pos(), state, state, Block.UPDATE_CLIENTS);
    }

    /**
     * Edit one element of a structure template pool — set its weight and projection. Disk-only: writes to the active
     * project's pool JSON via {@link ProjectDraftStore}; the live {@code Registries#TEMPLATE_POOL} object is left
     * untouched so existing structure generation keeps using the pre-reload state. The user runs Reload Project
     * (separate packet) to bring the registry up to date.
     * <p>
     * Op-gated. Rejected while a reload is in flight — see {@link ProjectDraftStore#isReloading} for why. After the
     * disk write, an {@link S2CPoolDraftPayload} echo gives the client the new authoritative element list.
     */
    public static void handleUpdatePoolElement(C2SUpdatePoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }

        var projectName = payload.projectName();
        var server = serverPlayer.serverLevel().getServer();
        if (!validProjectFor(projectName)) {
            return;
        }

        var pool = ProjectDraftStore.INSTANCE.getOrSeedPool(server, projectName, payload.poolId());
        if (pool == null) {
            return;
        }
        if (!ProjectDraftStore.applyUpdate(pool, payload.rawIndex(), payload.newWeight(), payload.newProjectionOrdinal())) {
            return;
        }
        try {
            ProjectDraftStore.INSTANCE.writeAndPersist(projectName, payload.poolId(), pool);
        } catch (IOException e) {
            LOGGER.error("[BLib] handleUpdatePoolElement: write failed for project {} pool {}", projectName, payload.poolId(), e);
            return;
        }

        var elements = ProjectDraftStore.extractDraftElements(pool);
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CPoolDraftPayload(projectName, payload.poolId(), elements));
    }

    /**
     * Append a new {@code single_pool_element} (pointing at {@code templateId}) to the project's pool JSON. Used by the
     * Pool Editor's footer "Add piece" picker. Disk-only — see {@link #handleUpdatePoolElement} for the model.
     */
    public static void handleAddPoolElement(C2SAddPoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }

        var projectName = payload.projectName();
        var server = serverPlayer.serverLevel().getServer();
        if (!validProjectFor(projectName)) {
            return;
        }

        var pool = ProjectDraftStore.INSTANCE.getOrSeedPool(server, projectName, payload.poolId());
        if (pool == null) {
            return;
        }
        ProjectDraftStore.applyAdd(pool, payload.templateId(), payload.weight(), payload.projectionOrdinal());
        try {
            ProjectDraftStore.INSTANCE.writeAndPersist(projectName, payload.poolId(), pool);
        } catch (IOException e) {
            LOGGER.error("[BLib] handleAddPoolElement: write failed for project {} pool {}", projectName, payload.poolId(), e);
            return;
        }

        var elements = ProjectDraftStore.extractDraftElements(pool);
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CPoolDraftPayload(projectName, payload.poolId(), elements));
    }

    /**
     * Remove the entry at {@code rawIndex} from the project's pool JSON. Used by the Pool Editor's per-row × button.
     * Out-of-range indices are silently ignored — likely a stale client-side reference. Disk-only.
     */
    public static void handleRemovePoolElement(C2SRemovePoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }

        var projectName = payload.projectName();
        var server = serverPlayer.serverLevel().getServer();
        if (!validProjectFor(projectName)) {
            return;
        }

        var pool = ProjectDraftStore.INSTANCE.getOrSeedPool(server, projectName, payload.poolId());
        if (pool == null) {
            return;
        }
        if (!ProjectDraftStore.applyRemove(pool, payload.rawIndex())) {
            return;
        }
        try {
            ProjectDraftStore.INSTANCE.writeAndPersist(projectName, payload.poolId(), pool);
        } catch (IOException e) {
            LOGGER.error("[BLib] handleRemovePoolElement: write failed for project {} pool {}", projectName, payload.poolId(), e);
            return;
        }

        var elements = ProjectDraftStore.extractDraftElements(pool);
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CPoolDraftPayload(projectName, payload.poolId(), elements));
    }

    /**
     * Pool Editor "Save & Reload" button — every pool edit already writes to disk, so this is just a reload trigger
     * dressed up as a header button. The {@code poolId} field is unused server-side; the client uses it only to track
     * which editor invoked the reload.
     */
    public static void handleSavePool(C2SSavePoolPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        runReload(serverPlayer, payload.projectName());
    }

    /**
     * Reply to {@link C2SListProjectsPayload} with the current set of BLib projects in the world. No op-gating on read
     * — the picker should be usable by anyone, and listProjects only reveals folder names + descriptions, not any data
     * the user couldn't see by opening the world's datapacks folder directly.
     */
    public static void handleListProjects(C2SListProjectsPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var projects = EngineProjectIO.listProjects();
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CProjectListPayload(projects));
    }

    /** Create a new project, then reply with op result + a refreshed list. Op-gated. */
    public static void handleCreateProject(C2SCreateProjectPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.CREATE, payload.name(), false, "Insufficient permissions");
            return;
        }
        var server = serverPlayer.serverLevel().getServer();
        try {
            EngineProjectIO.createProject(payload.name(), payload.description());
            // Force the active world's pack repo to re-scan now that a new project folder exists. Without this
            // the new pack only becomes available on the next reload — the picker would let the user Open it but
            // the runReload path would fail to find the pack id immediately afterward.
            server.getPackRepository().reload();
            replyOpResult(serverPlayer, ProjectOp.CREATE, payload.name(), true, "");
            BLib.MOD.networking().sendToClient(serverPlayer, new S2CProjectListPayload(EngineProjectIO.listProjects()));
        } catch (IllegalArgumentException | IOException e) {
            replyOpResult(serverPlayer, ProjectOp.CREATE, payload.name(), false, e.getMessage());
        }
    }

    /** Delete a project, then reply with op result + a refreshed list. Op-gated. */
    public static void handleDeleteProject(C2SDeleteProjectPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.DELETE, payload.name(), false, "Insufficient permissions");
            return;
        }
        var server = serverPlayer.serverLevel().getServer();
        try {
            EngineProjectIO.deleteProject(server, payload.name());
            ProjectDraftStore.INSTANCE.clearProject(payload.name());
            replyOpResult(serverPlayer, ProjectOp.DELETE, payload.name(), true, "");
            BLib.MOD.networking().sendToClient(serverPlayer, new S2CProjectListPayload(EngineProjectIO.listProjects()));
        } catch (IllegalArgumentException | IOException e) {
            replyOpResult(serverPlayer, ProjectOp.DELETE, payload.name(), false, e.getMessage());
        }
    }

    /**
     * "Open Project" handshake — the picker calls this to confirm the project still exists before transitioning to the
     * workspace screen. Server-side state for the active project per player is currently unused (every edit packet
     * carries its own projectName), but the handshake protects against picker-list / on-disk races and gives the client
     * a clean "OPEN failed" path for missing projects.
     */
    public static void handleOpenProject(C2SOpenProjectPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.OPEN, payload.name(), false, "Insufficient permissions");
            return;
        }
        try {
            EngineProjectIO.validateProjectName(payload.name());
        } catch (IllegalArgumentException e) {
            replyOpResult(serverPlayer, ProjectOp.OPEN, payload.name(), false, e.getMessage());
            return;
        }
        if (!EngineProjectIO.isBLibProject(EngineProjectIO.projectRoot(payload.name()))) {
            replyOpResult(serverPlayer, ProjectOp.OPEN, payload.name(), false, "Project '" + payload.name() + "' does not exist");
            return;
        }
        replyOpResult(serverPlayer, ProjectOp.OPEN, payload.name(), true, "");
    }

    /** "Reload Project" — make on-disk edits live in the registry. Op-gated. */
    public static void handleReloadProject(C2SReloadProjectPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), false, "Insufficient permissions");
            return;
        }
        runReload(serverPlayer, payload.projectName());
    }

    /**
     * Send the active project's authoritative state for one pool back to the client. Lazy-seeds from the registry if
     * the project has no override yet; either way the client's draft cache becomes the editor's display source.
     */
    public static void handleRequestPoolDraft(C2SRequestPoolDraftPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        var server = serverPlayer.serverLevel().getServer();
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var pool = ProjectDraftStore.INSTANCE.getOrSeedPool(server, payload.projectName(), payload.poolId());
        if (pool == null) {
            return;
        }
        var elements = ProjectDraftStore.extractDraftElements(pool);
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CPoolDraftPayload(payload.projectName(), payload.poolId(), elements));
    }

    /**
     * Capture a volume of blocks. Op-gated. The actual world walk runs on this thread (server thread); the volume cap
     * in {@link BlockCaptureEngine#MAX_TOTAL_VOLUME} keeps the stall bounded. On {@code JIGSAW} success we kick off a
     * project reload so the new structures + pool become live in the registry without a separate user click.
     */
    public static void handleCaptureBlocks(C2SCaptureBlocksPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), false, "Insufficient permissions");
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), false, "Project is reloading; try again in a moment");
            return;
        }

        var dimensionKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var mode = CaptureMode.fromOrdinal(payload.modeOrdinal());
        var server = serverPlayer.serverLevel().getServer();
        var request = new BlockCaptureEngine.CaptureRequest(
            payload.projectName(),
            payload.captureName(),
            payload.cornerA(),
            payload.cornerB(),
            mode,
            dimensionKey
        );
        var result = BlockCaptureEngine.run(server, request);
        if (!result.success()) {
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), false, result.message());
            return;
        }

        replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), true, result.message());

        if (mode == CaptureMode.JIGSAW) {
            // Make the new structures + pool live in the registry. Without this the user would have to click
            // Reload Project before the captured pieces could be `/place jigsaw`'d.
            runReload(serverPlayer, payload.projectName());
        } else {
            // General captures live in <project>/captures/ and aren't part of the datapack tree, so no reload is
            // needed — but the captures list on the panel still wants to refresh.
            BLib.MOD.networking()
                .sendToClient(
                    serverPlayer,
                    new S2CCaptureListPayload(payload.projectName(), EngineProjectIO.listCaptureNames(payload.projectName()))
                );
        }
    }

    /** List captures in the project's {@code captures/} folder. Read-only; no op-gating. */
    public static void handleListCaptures(C2SListCapturesPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var captures = EngineProjectIO.listCaptureNames(payload.projectName());
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CCaptureListPayload(payload.projectName(), captures));
    }

    /** Delete a capture file. Op-gated. Replies with success / failure and a fresh capture list. */
    public static void handleDeleteCapture(C2SDeleteCapturePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), false, "Insufficient permissions");
            return;
        }
        try {
            EngineProjectIO.deleteCapture(payload.projectName(), payload.captureName());
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), true, "Deleted '" + payload.captureName() + "'");
        } catch (IllegalArgumentException | IOException e) {
            replyOpResult(serverPlayer, ProjectOp.CAPTURE, payload.projectName(), false, e.getMessage());
        }
        // Always refresh the list so the panel mirrors disk state regardless of success.
        BLib.MOD.networking()
            .sendToClient(
                serverPlayer,
                new S2CCaptureListPayload(payload.projectName(), EngineProjectIO.listCaptureNames(payload.projectName()))
            );
    }

    /** List pools authored under the project's datapack. Read-only; no op-gating. */
    public static void handleListPools(C2SListPoolsPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var pools = EngineProjectIO.listProjectPools(payload.projectName());
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CPoolListPayload(payload.projectName(), pools));
    }

    /** List structures authored under the project's datapack. Read-only; no op-gating. */
    public static void handleListStructures(C2SListStructuresPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var structures = EngineProjectIO.listProjectStructures(payload.projectName());
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CStructureListPayload(payload.projectName(), structures));
    }

    /**
     * Delete a pool's JSON file. Op-gated. Replies with op result + a fresh pool list. The live registry still holds
     * the pool until the user runs Reload Project — same caveat as deleting a structure file.
     */
    public static void handleDeletePool(C2SDeletePoolPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), false, "Insufficient permissions");
            return;
        }
        try {
            EngineProjectIO.deleteProjectPool(payload.projectName(), payload.poolId());
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), true, "Deleted pool '" + payload.poolId() + "'");
        } catch (IOException e) {
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), false, e.getMessage());
        }
        BLib.MOD.networking()
            .sendToClient(
                serverPlayer,
                new S2CPoolListPayload(payload.projectName(), EngineProjectIO.listProjectPools(payload.projectName()))
            );
    }

    /**
     * Delete a structure's NBT file. Op-gated. Replies with op result + a fresh structure list. Same registry caveat as
     * {@link #handleDeletePool}: the loaded {@code StructureTemplate} stays in memory until the user runs Reload
     * Project.
     */
    public static void handleDeleteStructure(C2SDeleteStructurePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), false, "Insufficient permissions");
            return;
        }
        try {
            EngineProjectIO.deleteProjectStructure(payload.projectName(), payload.structureId());
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), true, "Deleted structure '" + payload.structureId() + "'");
        } catch (IOException e) {
            replyOpResult(serverPlayer, ProjectOp.RELOAD, payload.projectName(), false, e.getMessage());
        }
        BLib.MOD.networking()
            .sendToClient(
                serverPlayer,
                new S2CStructureListPayload(payload.projectName(), EngineProjectIO.listProjectStructures(payload.projectName()))
            );
    }

    /**
     * Move (or copy) a volume of blocks by an integer offset. Op-gated. Reads the source volume into a transient
     * StructureTemplate snapshot, optionally clears the source to air, and replaces the snapshot at the offset
     * destination — vanilla's template machinery handles block-entity NBT, light updates, and registry-aware state
     * copying. Replies with {@link S2CMoveSelectionResultPayload}; the client uses the success flag to shift the AABB
     * so the user's selection follows the moved blocks (Photoshop's marquee-follows-pixels pattern).
     */
    public static void handleMoveSelection(C2SMoveSelectionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            BLib.MOD.networking().sendToClient(serverPlayer, new S2CMoveSelectionResultPayload(false, "Insufficient permissions", 0));
            return;
        }
        var dimensionKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var server = serverPlayer.serverLevel().getServer();
        var request = new BlockMoveEngine.MoveRequest(
            payload.cornerA(),
            payload.cornerB(),
            payload.dx(),
            payload.dy(),
            payload.dz(),
            payload.copy(),
            dimensionKey
        );
        var result = BlockMoveEngine.run(server, request);
        BLib.MOD.networking()
            .sendToClient(serverPlayer, new S2CMoveSelectionResultPayload(result.success(), result.message(), result.blockCount()));
    }

    /**
     * Copy (or Cut, when {@code deleteSource=true}) the selection's blocks into {@link ServerBlockClipboard}. Op-gated.
     * Replies via {@link S2CClipboardStatusPayload} so the client knows whether to enable Paste — block NBT stays
     * server-side to avoid wire-cost on what may be a 256³ snapshot.
     */
    public static void handleCopySelection(C2SCopySelectionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            BLib.MOD.networking().sendToClient(serverPlayer, new S2CClipboardStatusPayload(false, 0, 0, 0, 0L));
            return;
        }
        var dimensionKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var server = serverPlayer.serverLevel().getServer();
        var result = BlockClipboardEngine.copy(server, payload.cornerA(), payload.cornerB(), payload.deleteSource(), dimensionKey);
        if (result.success()) {
            var size = ServerBlockClipboard.size();
            if (size != null) {
                BLib.MOD.networking()
                    .sendToClient(
                        serverPlayer,
                        new S2CClipboardStatusPayload(true, size.getX(), size.getY(), size.getZ(), ServerBlockClipboard.filledAtMs())
                    );
            } else {
                BLib.MOD.networking().sendToClient(serverPlayer, new S2CClipboardStatusPayload(false, 0, 0, 0, 0L));
            }
        } else {
            // Failure leaves the clipboard's previous state intact server-side; tell the client that whatever it had
            // is still valid by re-syncing the current state (or an empty status if there was no prior state).
            var size = ServerBlockClipboard.size();
            BLib.MOD.networking()
                .sendToClient(
                    serverPlayer,
                    new S2CClipboardStatusPayload(
                        ServerBlockClipboard.hasContents(),
                        size == null ? 0 : size.getX(),
                        size == null ? 0 : size.getY(),
                        size == null ? 0 : size.getZ(),
                        ServerBlockClipboard.filledAtMs()
                    )
                );
        }
    }

    /**
     * Paste from {@link ServerBlockClipboard} at {@code destination}. Op-gated. No reply on success — the world state
     * speaks for itself. Failures (empty clipboard, dimension miss) are silent for v1; the client gates the Paste
     * button on clipboard state, so the empty case shouldn't reach the server in normal flow.
     */
    public static void handlePasteFromClipboard(C2SPasteFromClipboardPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        var dimensionKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var server = serverPlayer.serverLevel().getServer();
        BlockClipboardEngine.paste(server, payload.destination(), dimensionKey);
    }

    /**
     * Clear the AABB to air without touching the clipboard. Distinct from Cut (which fills the clipboard before
     * clearing). Op-gated; no reply for v1.
     */
    public static void handleDeleteSelection(C2SDeleteSelectionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        var dimensionKey = ResourceKey.create(Registries.DIMENSION, payload.dimensionId());
        var server = serverPlayer.serverLevel().getServer();
        BlockClipboardEngine.delete(server, payload.cornerA(), payload.cornerB(), dimensionKey);
    }

    /**
     * Common reload path shared by {@link #handleSavePool} and {@link #handleReloadProject}. Sets the {@code reloading}
     * guard, kicks off {@code reloadResources}, chains the success/failure reply onto its completion, and clears the
     * project's draft cache so the next edit re-seeds from the freshly-imported registry.
     */
    private static void runReload(ServerPlayer serverPlayer, String projectName) {
        var server = serverPlayer.serverLevel().getServer();
        ProjectDraftStore.INSTANCE.setReloading(true);
        try {
            var future = EngineProjectIO.reloadProject(server, projectName);
            future.whenComplete((v, t) -> {
                try {
                    ProjectDraftStore.INSTANCE.clearProject(projectName);
                    if (t != null) {
                        LOGGER.error("[BLib] reloadProject failed (async) for {}", projectName, t);
                        replyOpResult(serverPlayer, ProjectOp.RELOAD, projectName, false, t.getMessage());
                    } else {
                        replyOpResult(serverPlayer, ProjectOp.RELOAD, projectName, true, "");
                    }
                } finally {
                    ProjectDraftStore.INSTANCE.setReloading(false);
                }
            });
        } catch (IllegalArgumentException | IOException e) {
            ProjectDraftStore.INSTANCE.setReloading(false);
            LOGGER.error("[BLib] reloadProject failed (sync) for {}", projectName, e);
            replyOpResult(serverPlayer, ProjectOp.RELOAD, projectName, false, e.getMessage());
        }
    }

    /**
     * Guard for edit handlers: validates that {@code projectName} parses and that a BLib project actually exists at
     * that path. Returns false (and silently no-ops on mismatch) so a stale client packet from before a delete doesn't
     * crash anything; logged at debug only since drops here are expected during normal session shutdown.
     */
    private static boolean validProjectFor(String projectName) {
        try {
            EngineProjectIO.validateProjectName(projectName);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return EngineProjectIO.isBLibProject(EngineProjectIO.projectRoot(projectName));
    }

    private static void replyOpResult(ServerPlayer serverPlayer, ProjectOp op, String projectName, boolean success, String message) {
        var payload = success
            ? S2CProjectOpResultPayload.success(op, projectName, message)
            : S2CProjectOpResultPayload.failure(op, projectName, message);
        BLib.MOD.networking().sendToClient(serverPlayer, payload);
    }

    private static Rotation ordinalToRotation(int ordinal) {
        var values = Rotation.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return Rotation.NONE;
        }
        return values[ordinal];
    }

    private static Mirror ordinalToMirror(int ordinal) {
        var values = Mirror.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return Mirror.NONE;
        }
        return values[ordinal];
    }

    // ---------------------------------------------------------------------------------------------
    // Faction authoring layout — handlers
    // ---------------------------------------------------------------------------------------------

    /** Reply with the workspace directory snapshot. Op-gated; idempotent (safe to call repeatedly). */
    public static void handleRequestFactionDirectory(C2SRequestFactionDirectoryPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        BLib.MOD.networking().sendToClient(sp, BLibFactionManager.INSTANCE.buildDirectorySnapshot());
    }

    /** Reply with one faction's full editable state. Op-gated; null result silently no-ops on missing factions. */
    public static void handleRequestFactionInspection(C2SRequestFactionInspectionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var snapshot = BLibFactionManager.INSTANCE.buildInspectionSnapshot(payload.factionId());
        if (snapshot != null) {
            BLib.MOD.networking().sendToClient(sp, snapshot);
        }
    }

    /** Reply with one faction's member roster. Op-gated. */
    public static void handleRequestFactionMembers(C2SRequestFactionMembersPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var snapshot = BLibFactionManager.INSTANCE.buildMembersSnapshot(sp.server, payload.factionId());
        if (snapshot != null) {
            BLib.MOD.networking().sendToClient(sp, snapshot);
        }
    }

    /**
     * Create a faction with the given id + type id. Resolves the type via the registry-backed lookup and silently
     * no-ops on unknown types; on success pushes the directory to all clients so the new faction appears in the Browser
     * everywhere.
     */
    public static void handleCreateFaction(C2SCreateFactionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.getOrCreateByTypeId(payload.factionId(), payload.typeId());
        if (faction == null) {
            return;
        }
        BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
    }

    /** Delete a faction. Op-gated; pushes the directory after a successful delete so all clients refresh. */
    public static void handleDeleteFaction(C2SDeleteFactionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        if (BLibFactionManager.INSTANCE.remove(payload.factionId())) {
            BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
        }
    }

    /**
     * Update one editable scalar on a faction. The discriminator + value pair maps to one of the {@code Faction}
     * setters; on success the inspection push fires (and the directory push too, when name/color changed). Bad inputs
     * (unparseable color, unknown enum name) silently no-op rather than throwing — the inspector resyncs from the next
     * directory/inspection push so users see whether their commit landed.
     */
    public static void handleUpdateFactionField(C2SUpdateFactionFieldPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(payload.factionId());
        if (faction == null) {
            return;
        }
        var fields = C2SUpdateFactionFieldPayload.Field.values();
        if (payload.fieldOrdinal() < 0 || payload.fieldOrdinal() >= fields.length) {
            return;
        }
        var field = fields[payload.fieldOrdinal()];
        var value = payload.value();
        boolean directoryChanged = false;
        switch (field) {
            case NAME -> {
                faction.setName(value);
                directoryChanged = true;
            }
            case COLOR -> {
                var parsed = parseColor(value);
                if (parsed == null) {
                    return;
                }
                faction.setColor(parsed);
                directoryChanged = true;
            }
            case CLAIM_VISIBILITY -> {
                var v = parseEnum(ClaimVisibility.class, value);
                if (v == null) {
                    return;
                }
                faction.setClaimVisibility(v);
            }
            case BLOCK_BREAK_PROTECTION -> {
                var v = parseEnum(ProtectionMode.class, value);
                if (v == null) {
                    return;
                }
                faction.setBlockBreakProtection(v);
            }
            case BLOCK_INTERACT_PROTECTION -> {
                var v = parseEnum(ProtectionMode.class, value);
                if (v == null) {
                    return;
                }
                faction.setBlockInteractProtection(v);
            }
            case ENTITY_INTERACT_PROTECTION -> {
                var v = parseEnum(ProtectionMode.class, value);
                if (v == null) {
                    return;
                }
                faction.setEntityInteractProtection(v);
            }
            case NONLIVING_ENTITY_ATTACK_PROTECTION -> {
                var v = parseEnum(ProtectionMode.class, value);
                if (v == null) {
                    return;
                }
                faction.setNonLivingEntityAttackProtection(v);
            }
            case ALLOW_PVP -> faction.setAllowPvp(Boolean.parseBoolean(value));
            case ALLOW_EXPLOSIONS -> faction.setAllowExplosions(Boolean.parseBoolean(value));
            case ALLOW_MOB_GRIEFING -> faction.setAllowMobGriefing(Boolean.parseBoolean(value));
        }
        if (directoryChanged) {
            BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
        }
        BLibFactionManager.INSTANCE.pushInspectionToAllClients(sp.server, payload.factionId());
    }

    /** Set the pairwise relationship between two factions. Pushes the directory (which carries the table). */
    public static void handleSetFactionRelationship(C2SSetFactionRelationshipPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var states = RelationshipState.values();
        if (payload.stateOrdinal() < 0 || payload.stateOrdinal() >= states.length) {
            return;
        }
        BLibFactionManager.INSTANCE.setRelationship(payload.factionA(), payload.factionB(), states[payload.stateOrdinal()]);
        BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
    }

    /** Add a member to a faction. Pushes the directory (member count) + members roster. */
    public static void handleAddFactionMember(C2SAddFactionMemberPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(payload.factionId());
        if (faction == null) {
            return;
        }
        if (faction.membership().addMember(FactionMember.entity(payload.memberUuid()))) {
            BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
            BLibFactionManager.INSTANCE.pushMembersToAllClients(sp.server, payload.factionId());
        }
    }

    /** Remove a member from a faction. Pushes the directory + members roster on success. */
    public static void handleRemoveFactionMember(C2SRemoveFactionMemberPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(payload.factionId());
        if (faction == null) {
            return;
        }
        if (faction.membership().removeMember(FactionMember.entity(payload.memberUuid()))) {
            BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);
            BLibFactionManager.INSTANCE.pushMembersToAllClients(sp.server, payload.factionId());
        }
    }

    /**
     * Claim a chunk for a faction. Server uses the caller's current dimension. The territory manager fires the
     * {@code CHUNK_CLAIM_ADDED} event on success, which an existing listener in {@code BLib.java} translates to a
     * visibility-filtered sync push per online player — no explicit push needed here.
     */
    public static void handleAddChunkClaim(C2SAddChunkClaimPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        if (BLibFactionManager.INSTANCE.get(payload.factionId()) == null) {
            return;
        }
        BLibTerritoryManager.INSTANCE.addClaim(
            sp.serverLevel(),
            new ChunkPos(payload.chunkX(), payload.chunkZ()),
            payload.factionId()
        );
    }

    /** Unclaim a chunk for a faction. Same auto-sync path as {@link #handleAddChunkClaim}. */
    public static void handleRemoveChunkClaim(C2SRemoveChunkClaimPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        BLibTerritoryManager.INSTANCE.removeClaim(
            sp.serverLevel(),
            new ChunkPos(payload.chunkX(), payload.chunkZ()),
            payload.factionId()
        );
    }

    /**
     * Parse a hex color string ("#RRGGBB", "RRGGBB", or "0xRRGGBB") into an int. Returns null on parse failure so the
     * caller can no-op the commit.
     */
    private static @Nullable Integer parseColor(String value) {
        if (value == null) {
            return null;
        }
        var trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith("#")) {
            trimmed = trimmed.substring(1);
        } else if (trimmed.startsWith("0x") || trimmed.startsWith("0X")) {
            trimmed = trimmed.substring(2);
        }
        try {
            return (int) Long.parseLong(trimmed, 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static <E extends Enum<E>> @Nullable E parseEnum(Class<E> enumClass, String value) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
