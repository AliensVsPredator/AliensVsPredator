package com.blib.mod.common.network;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
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
import com.blib.internal.common.storage.ProjectTagDraftStore;
import com.blib.internal.common.territory.BLibTerritoryManager;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.gameplay.history.ActionHistory;
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
import com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload;
import com.blib.mod.common.network.packet.C2SDeletePoolPayload;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SDeleteStructurePayload;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SListCapturesPayload;
import com.blib.mod.common.network.packet.C2SListPoolsPayload;
import com.blib.mod.common.network.packet.C2SListProjectsPayload;
import com.blib.mod.common.network.packet.C2SListStructuresPayload;
import com.blib.mod.common.network.packet.C2SMovePlacedPiecePayload;
import com.blib.mod.common.network.packet.C2SMoveSelectionPayload;
import com.blib.mod.common.network.packet.C2SOpenProjectPayload;
import com.blib.mod.common.network.packet.C2SPasteFromClipboardPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SRedoActionPayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveBlockTagPayload;
import com.blib.mod.common.network.packet.C2SRemoveChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRemoveFactionMemberPayload;
import com.blib.mod.common.network.packet.C2SRemovePoolElementPayload;
import com.blib.mod.common.network.packet.C2SRemoveTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRequestEntityFactionsPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionInspectionPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionMembersPayload;
import com.blib.mod.common.network.packet.C2SRequestPlacedPiecesPayload;
import com.blib.mod.common.network.packet.C2SRequestPoolDraftPayload;
import com.blib.mod.common.network.packet.C2SRequestRegistryEntriesPayload;
import com.blib.mod.common.network.packet.C2SRequestTagCatalogPayload;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.C2SSetBlockStatePropertyPayload;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2SSetFactionRelationshipPayload;
import com.blib.mod.common.network.packet.C2SSetTagEntryRequiredPayload;
import com.blib.mod.common.network.packet.C2SSetTagReplacePayload;
import com.blib.mod.common.network.packet.C2SSpawnEntityPayload;
import com.blib.mod.common.network.packet.C2STranslateEntityPayload;
import com.blib.mod.common.network.packet.C2SUndoActionPayload;
import com.blib.mod.common.network.packet.C2SUpdateFactionFieldPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;
import com.blib.mod.common.network.packet.C2SUpdatePoolElementPayload;
import com.blib.mod.common.network.packet.ProjectOp;
import com.blib.mod.common.network.packet.S2CActionHistorySyncPayload;
import com.blib.mod.common.network.packet.S2CCaptureListPayload;
import com.blib.mod.common.network.packet.S2CClipboardStatusPayload;
import com.blib.mod.common.network.packet.S2CEntityFactionsPayload;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CPoolListPayload;
import com.blib.mod.common.network.packet.S2CProjectListPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;
import com.blib.mod.common.network.packet.S2CRegistryEntriesPayload;
import com.blib.mod.common.network.packet.S2CStructureListPayload;
import com.blib.mod.common.network.packet.S2CTagCatalogPayload;
import com.blib.mod.common.network.packet.S2CTagDraftPayload;
import com.blib.mod.common.network.packet.TagCatalogEntry;

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

        var serverLevel = serverPlayer.serverLevel();
        Entity entity = serverLevel.getEntity(payload.entityId());
        if (entity == null || entity instanceof Player) {
            return;
        }

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        var nbt = new net.minecraft.nbt.CompoundTag();
        entity.saveWithoutId(nbt);
        var pos = entity.blockPosition();
        var entityUuid = entity.getUUID();
        var description = "Remove " + entity.getName().getString();

        entity.discard();

        var action = new com.blib.mod.common.gameplay.history.EntityRemoveAction(
            serverLevel.dimension(),
            typeId,
            entityUuid,
            nbt,
            pos,
            description,
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
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

        var spawned = type.spawn(level, payload.anchor(), MobSpawnType.COMMAND);
        if (spawned == null) {
            return;
        }
        var nbt = new net.minecraft.nbt.CompoundTag();
        spawned.saveWithoutId(nbt);
        var action = new com.blib.mod.common.gameplay.history.EntitySpawnAction(
            level.dimension(),
            payload.entityTypeId(),
            spawned.getUUID(),
            nbt,
            payload.anchor(),
            "Spawn " + spawned.getName().getString(),
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
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
        var beforeX = entity.getX();
        var beforeY = entity.getY();
        var beforeZ = entity.getZ();
        entity.teleportTo(payload.x(), payload.y(), payload.z());
        var action = new com.blib.mod.common.gameplay.history.EntityTranslateAction(
            dimKey,
            entity.getUUID(),
            beforeX,
            beforeY,
            beforeZ,
            payload.x(),
            payload.y(),
            payload.z(),
            "Translate " + entity.getName().getString(),
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
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
        var beforeScale = attr.getBaseValue();
        var clamped = Math.max(0.1, Math.min(4.0, payload.scale()));
        attr.setBaseValue(clamped);

        var action = new com.blib.mod.common.gameplay.history.EntityScaleAction(
            dimKey,
            le.getUUID(),
            beforeScale,
            clamped,
            "Scale " + le.getName().getString(),
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
    }

    /**
     * Place a structure template at the requested anchor. Authorized for ops only — placement writes blocks into the
     * world, same destructiveness threshold as {@code /setblock}. Looks up the template by id from the world's
     * {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager}; silently no-ops if
     * the id doesn't resolve (the client and server might be out of sync if the user reloaded resources mid-session).
     * The {@code Block.UPDATE_CLIENTS} flag (2) ensures placed blocks sync back to the client without triggering
     * neighbor updates that would corrupt placed structures (e.g. lit redstone repeaters firing on placement).
     * <p>
     * After a successful placement we register a {@link com.blib.mod.common.gameplay.jigsaw.PlacedPiece} so engine-mode
     * users can hover, select, and right-click the piece as a single thing. The whole gesture (blocks + piece
     * registration) is pushed to {@link ActionHistory} as one {@code BlockRegionEdit} so undo reverses both.
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
        // AABB derived from the same settings we're about to feed placeInWorld so it matches exactly.
        var aabb = template.getBoundingBox(settings, anchor);
        var pieceId = java.util.UUID.randomUUID();
        var piece = new com.blib.mod.common.gameplay.jigsaw.PlacedPiece(
            pieceId,
            payload.templateId(),
            serverLevel.dimension(),
            aabb,
            anchor,
            rotation,
            mirror,
            serverLevel.getGameTime()
        );

        // Capture pre-state for undo, then place, then capture post-state for redo.
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, aabb, beforeStates, beforeBE);

        template.placeInWorld(serverLevel, anchor, anchor, settings, serverLevel.getRandom(), Block.UPDATE_CLIENTS);

        var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, aabb, afterStates, afterBE);

        com.blib.internal.common.storage.BLibDataStoreManager.INSTANCE
            .getLevel(serverLevel, com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes.PLACED_PIECES)
            .add(piece);
        com.blib.mod.common.gameplay.jigsaw.PlacedPieceSync.onPieceAdded(serverLevel, piece);

        var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            serverLevel.dimension(),
            java.util.List.of(region),
            "Place piece " + payload.templateId(),
            System.currentTimeMillis(),
            com.blib.mod.common.gameplay.history.PieceLink.placement(piece)
        );
        ActionHistory.push(action);
    }

    /**
     * Capture the current block states + block-entity NBT for every cell in {@code aabb}, populating the supplied maps.
     * Shared helper used by every block-region action push site so the snapshot capture stays consistent.
     */
    private static void captureRegion(
        net.minecraft.server.level.ServerLevel level,
        net.minecraft.world.level.levelgen.structure.BoundingBox aabb,
        java.util.Map<net.minecraft.core.BlockPos, BlockState> states,
        java.util.Map<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag> beNbt
    ) {
        for (
            var pos : net.minecraft.core.BlockPos.betweenClosed(
                aabb.minX(),
                aabb.minY(),
                aabb.minZ(),
                aabb.maxX(),
                aabb.maxY(),
                aabb.maxZ()
            )
        ) {
            var p = pos.immutable();
            states.put(p, level.getBlockState(p));
            var be = level.getBlockEntity(p);
            if (be != null) {
                beNbt.put(p, be.saveWithFullMetadata(level.registryAccess()));
            }
        }
    }

    /**
     * Pop the most recent action from the unified {@link ActionHistory} and revert it. Op-gated. Filters by the
     * player's current dimension for world actions so cross-dimension undos don't write blocks back at the same coords
     * in the wrong level.
     */
    public static void handleUndoAction(C2SUndoActionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        ActionHistory.undo(serverPlayer.server, serverPlayer.serverLevel());
    }

    /** Mirror of {@link #handleUndoAction} for the redo direction. */
    public static void handleRedoAction(C2SRedoActionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        ActionHistory.redo(serverPlayer.server, serverPlayer.serverLevel());
    }

    /**
     * Broadcast a fresh {@link S2CActionHistorySyncPayload} mirroring the current undo+redo stacks. Wired into
     * {@link ActionHistory#setChangeListener} on server start so the panel updates on every push / undo / redo / clear.
     */
    public static void broadcastActionHistorySync(MinecraftServer server) {
        if (server == null) {
            return;
        }
        var undo = ActionHistory.undoSnapshot();
        var redo = ActionHistory.redoSnapshot();
        var entries = new java.util.ArrayList<com.blib.mod.common.network.packet.ActionDescriptor>(undo.size() + redo.size());
        for (var action : undo) {
            entries.add(action.toDescriptor());
        }
        for (var action : redo) {
            entries.add(action.toDescriptor());
        }
        var payload = new S2CActionHistorySyncPayload(entries, undo.size());
        BLib.MOD.networking().sendToAllClients(server, payload);
    }

    /**
     * Engine-mode entry handshake — the client requests the full set of placed pieces for its current dimension so it
     * can populate its mirror without waiting for incremental adds. We don't gate on the {@code clientDimensionHint};
     * the server's authoritative dimension is the player's actual {@code serverLevel}. Op-gated because the response
     * leaks placement metadata (template ids, bounding boxes) that's an authoring concern, not a vanilla-player
     * affordance.
     */
    public static void handleRequestPlacedPieces(C2SRequestPlacedPiecesPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        com.blib.mod.common.gameplay.jigsaw.PlacedPieceSync.sendFullSyncTo(serverPlayer);
    }

    /**
     * Delete a previously-placed piece. Clears every block in the piece's AABB to air and removes the piece from the
     * level's store. Pushes a {@code BlockRegionEdit} with a deletion {@code PieceLink} so undo restores blocks AND
     * re-registers the piece (snapshot included). Op-gated.
     */
    public static void handleDeletePlacedPiece(C2SDeletePlacedPiecePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        var serverLevel = serverPlayer.serverLevel();
        var store = com.blib.internal.common.storage.BLibDataStoreManager.INSTANCE
            .getLevel(serverLevel, com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes.PLACED_PIECES);
        var piece = store.get(payload.id());
        if (piece == null || !piece.dimension().equals(serverLevel.dimension())) {
            return;
        }
        var aabb = piece.aabb();

        // Capture pre-delete state; after-state is air everywhere so we can short-circuit it.
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, aabb, beforeStates, beforeBE);

        var air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        for (var entry : beforeStates.entrySet()) {
            afterStates.put(entry.getKey(), air);
        }

        for (
            var pos : net.minecraft.core.BlockPos.betweenClosed(
                aabb.minX(),
                aabb.minY(),
                aabb.minZ(),
                aabb.maxX(),
                aabb.maxY(),
                aabb.maxZ()
            )
        ) {
            serverLevel.setBlock(pos.immutable(), air, Block.UPDATE_CLIENTS);
        }

        store.remove(payload.id());
        com.blib.mod.common.gameplay.jigsaw.PlacedPieceSync.onPieceRemoved(serverLevel, payload.id());

        var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(
            aabb,
            beforeStates,
            beforeBE,
            afterStates,
            java.util.Map.of()
        );
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            serverLevel.dimension(),
            java.util.List.of(region),
            "Delete piece " + piece.templateId(),
            System.currentTimeMillis(),
            com.blib.mod.common.gameplay.history.PieceLink.deletion(piece)
        );
        ActionHistory.push(action);
    }

    /**
     * Identity-preserving translation of a placed piece. Picks up every block (with block-entity NBT) inside the
     * piece's current AABB, clears the source cells, and stamps them at {@code newMin}-relative positions. The piece
     * record's AABB and anchor are updated and re-broadcast as an Add — clients keyed by UUID overwrite their entry, so
     * hover / selection / inspector stay coherent across the move.
     * <p>
     * Block-entity NBT is preserved end-to-end so chests keep their contents, signs keep their text, etc. Undo support
     * is wired in step 5 via a composite {@code BlockRegionEdit} covering both AABBs.
     */
    public static void handleMovePlacedPiece(C2SMovePlacedPiecePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        var serverLevel = serverPlayer.serverLevel();
        var store = com.blib.internal.common.storage.BLibDataStoreManager.INSTANCE
            .getLevel(serverLevel, com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes.PLACED_PIECES);
        var piece = store.get(payload.id());
        if (piece == null || !piece.dimension().equals(serverLevel.dimension())) {
            return;
        }

        var oldAabb = piece.aabb();
        var newMin = payload.newMin();
        var dx = newMin.getX() - oldAabb.minX();
        var dy = newMin.getY() - oldAabb.minY();
        var dz = newMin.getZ() - oldAabb.minZ();
        if (dx == 0 && dy == 0 && dz == 0) {
            return;
        }

        var sizeX = oldAabb.maxX() - oldAabb.minX();
        var sizeY = oldAabb.maxY() - oldAabb.minY();
        var sizeZ = oldAabb.maxZ() - oldAabb.minZ();
        var newAabb = new net.minecraft.world.level.levelgen.structure.BoundingBox(
            newMin.getX(),
            newMin.getY(),
            newMin.getZ(),
            newMin.getX() + sizeX,
            newMin.getY() + sizeY,
            newMin.getZ() + sizeZ
        );

        // Capture pre-move state of both AABBs so undo can restore them. AABBs may overlap (small shift); the two
        // captures still work because both record absolute world positions — the maps just have shared keys with
        // identical pre-state values.
        var sourceBeforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var sourceBeforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, oldAabb, sourceBeforeStates, sourceBeforeBE);
        var destBeforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var destBeforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, newAabb, destBeforeStates, destBeforeBE);

        // Original move logic, unchanged: capture relative offsets, clear source, stamp at dest.
        var savedStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var savedBeNbt = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        var oldMin = new net.minecraft.core.BlockPos(oldAabb.minX(), oldAabb.minY(), oldAabb.minZ());
        for (
            var pos : net.minecraft.core.BlockPos.betweenClosed(
                oldAabb.minX(),
                oldAabb.minY(),
                oldAabb.minZ(),
                oldAabb.maxX(),
                oldAabb.maxY(),
                oldAabb.maxZ()
            )
        ) {
            var rel = pos.subtract(oldMin).immutable();
            savedStates.put(rel, serverLevel.getBlockState(pos));
            var be = serverLevel.getBlockEntity(pos);
            if (be != null) {
                savedBeNbt.put(rel, be.saveWithFullMetadata(serverLevel.registryAccess()));
            }
        }

        var air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        for (
            var pos : net.minecraft.core.BlockPos.betweenClosed(
                oldAabb.minX(),
                oldAabb.minY(),
                oldAabb.minZ(),
                oldAabb.maxX(),
                oldAabb.maxY(),
                oldAabb.maxZ()
            )
        ) {
            serverLevel.setBlock(pos.immutable(), air, Block.UPDATE_CLIENTS);
        }

        for (var entry : savedStates.entrySet()) {
            var dest = newMin.offset(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
            serverLevel.setBlock(dest, entry.getValue(), Block.UPDATE_CLIENTS);
        }
        for (var entry : savedBeNbt.entrySet()) {
            var dest = newMin.offset(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
            if (serverLevel.getBlockState(dest).is(net.minecraft.world.level.block.Blocks.AIR)) {
                continue;
            }
            var be = serverLevel.getBlockEntity(dest);
            if (be != null) {
                be.loadWithComponents(entry.getValue(), serverLevel.registryAccess());
                be.setChanged();
            }
        }

        var updated = new com.blib.mod.common.gameplay.jigsaw.PlacedPiece(
            piece.id(),
            piece.templateId(),
            piece.dimension(),
            newAabb,
            piece.anchor().offset(dx, dy, dz),
            piece.rotation(),
            piece.mirror(),
            piece.placedAtTick()
        );
        store.remove(piece.id());
        store.add(updated);
        com.blib.mod.common.gameplay.jigsaw.PlacedPieceSync.onPieceAdded(serverLevel, updated);

        // Capture post-move state for redo. Same dual-region pattern.
        var sourceAfterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var sourceAfterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, oldAabb, sourceAfterStates, sourceAfterBE);
        var destAfterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var destAfterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(serverLevel, newAabb, destAfterStates, destAfterBE);

        var sourceRegion = new com.blib.mod.common.gameplay.history.RegionSnapshot(
            oldAabb,
            sourceBeforeStates,
            sourceBeforeBE,
            sourceAfterStates,
            sourceAfterBE
        );
        var destRegion = new com.blib.mod.common.gameplay.history.RegionSnapshot(
            newAabb,
            destBeforeStates,
            destBeforeBE,
            destAfterStates,
            destAfterBE
        );
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            serverLevel.dimension(),
            java.util.List.of(sourceRegion, destRegion),
            "Move piece " + piece.templateId(),
            System.currentTimeMillis(),
            com.blib.mod.common.gameplay.history.PieceLink.move(piece, updated)
        );
        ActionHistory.push(action);
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
        var pos = payload.pos();
        if (!(level.getBlockEntity(pos) instanceof JigsawBlockEntity jigsaw)) {
            return;
        }

        var jointValues = JigsawBlockEntity.JointType.values();
        var jointOrdinal = payload.jointOrdinal();
        if (jointOrdinal < 0 || jointOrdinal >= jointValues.length) {
            return;
        }

        var aabb = new net.minecraft.world.level.levelgen.structure.BoundingBox(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            pos.getX(),
            pos.getY(),
            pos.getZ()
        );
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(level, aabb, beforeStates, beforeBE);

        jigsaw.setName(payload.name());
        jigsaw.setTarget(payload.target());
        jigsaw.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, payload.pool()));
        jigsaw.setJoint(jointValues[jointOrdinal]);
        jigsaw.setFinalState(payload.finalState());

        jigsaw.setChanged();
        var state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);

        var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(level, aabb, afterStates, afterBE);

        var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            level.dimension(),
            java.util.List.of(region),
            "Edit jigsaw block",
            System.currentTimeMillis(),
            null
        );
        ActionHistory.push(action);
    }

    /**
     * Apply a single {@link Property} edit from the generic block inspector. The inspector only emits values it
     * generated from {@code property.getPossibleValues()}, so a missing property or unparseable value here means the
     * world diverged after the client snapshotted — silently drop rather than rolling the client back, since the
     * inspector re-reads on the next frame anyway.
     * <p>
     * Op-gated like the other block-mutating handlers. {@link Block#UPDATE_ALL} flags cover neighbor updates, client
     * sync, and lighting refresh — the inspector's "facing changed on a stair" case needs all three.
     */
    public static void handleSetBlockStateProperty(C2SSetBlockStatePropertyPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var level = serverPlayer.serverLevel();
        var pos = payload.pos();
        var state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }

        Property<?> property = null;
        for (var p : state.getProperties()) {
            if (p.getName().equals(payload.propertyName())) {
                property = p;
                break;
            }
        }
        if (property == null) {
            return;
        }

        var newState = applyParsedProperty(state, property, payload.valueString());
        if (newState == null || newState == state) {
            return;
        }

        var aabb = new net.minecraft.world.level.levelgen.structure.BoundingBox(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            pos.getX(),
            pos.getY(),
            pos.getZ()
        );
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(level, aabb, beforeStates, beforeBE);

        level.setBlock(pos, newState, Block.UPDATE_ALL);

        var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(level, aabb, afterStates, afterBE);

        var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            level.dimension(),
            java.util.List.of(region),
            "Edit block property " + property.getName(),
            System.currentTimeMillis(),
            null
        );
        ActionHistory.push(action);
    }

    /**
     * Generic-typed bridge: parse {@code value} via the property's own string codec and apply it. Returns {@code null}
     * when the value can't be parsed (caller treats this as "stale client snapshot" and drops the edit).
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState applyParsedProperty(BlockState state, Property<?> property, String value) {
        var typed = (Property<T>) property;
        var parsed = typed.getValue(value);
        if (parsed.isEmpty()) {
            return null;
        }
        return state.setValue(typed, parsed.get());
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
        var beforeJson = pool.deepCopy();
        if (!ProjectDraftStore.applyUpdate(pool, payload.rawIndex(), payload.newWeight(), payload.newProjectionOrdinal())) {
            return;
        }
        var afterJson = pool.deepCopy();
        com.blib.mod.common.gameplay.history.exec.ProjectActionExec.applyPoolDraft(server, projectName, payload.poolId(), pool);

        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.ProjectJsonEdit(
                projectName,
                com.blib.mod.common.gameplay.history.ProjectJsonEdit.Kind.POOL,
                payload.poolId(),
                null,
                null,
                beforeJson,
                afterJson,
                "Update pool element",
                System.currentTimeMillis()
            )
        );
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
        var beforeJson = pool.deepCopy();
        ProjectDraftStore.applyAdd(pool, payload.templateId(), payload.weight(), payload.projectionOrdinal());
        var afterJson = pool.deepCopy();
        com.blib.mod.common.gameplay.history.exec.ProjectActionExec.applyPoolDraft(server, projectName, payload.poolId(), pool);

        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.ProjectJsonEdit(
                projectName,
                com.blib.mod.common.gameplay.history.ProjectJsonEdit.Kind.POOL,
                payload.poolId(),
                null,
                null,
                beforeJson,
                afterJson,
                "Add pool element " + payload.templateId(),
                System.currentTimeMillis()
            )
        );
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
        var beforeJson = pool.deepCopy();
        if (!ProjectDraftStore.applyRemove(pool, payload.rawIndex())) {
            return;
        }
        var afterJson = pool.deepCopy();
        com.blib.mod.common.gameplay.history.exec.ProjectActionExec.applyPoolDraft(server, projectName, payload.poolId(), pool);

        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.ProjectJsonEdit(
                projectName,
                com.blib.mod.common.gameplay.history.ProjectJsonEdit.Kind.POOL,
                payload.poolId(),
                null,
                null,
                beforeJson,
                afterJson,
                "Remove pool element",
                System.currentTimeMillis()
            )
        );
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
     * Reply to {@link C2SRequestTagCatalogPayload} with every tag known across all live registries plus any
     * project-only tags from the active project's datapack. Project tags get {@code inProject=true} (whether they exist
     * in upstream packs or not); upstream-only tags get {@code inProject=false}. Sorted by registry id then tag id so
     * the browser's section + row order is deterministic. Read-only; no op-gating.
     */
    public static void handleRequestTagCatalog(C2SRequestTagCatalogPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var server = serverPlayer.serverLevel().getServer();
        var projectName = payload.projectName();
        var entries = new java.util.ArrayList<TagCatalogEntry>();

        // Build a set of project-tag keys for fast inProject lookup.
        var projectTags = validProjectFor(projectName) ? EngineProjectIO.listProjectTags(server, projectName) : List.<TagCatalogEntry>of();
        var projectKeys = new java.util.HashSet<ProjectTagDraftStore.TagDraftKey>();
        for (var pt : projectTags) {
            projectKeys.add(new ProjectTagDraftStore.TagDraftKey(pt.registryKey(), pt.tagId()));
        }
        var emittedKeys = new java.util.HashSet<ProjectTagDraftStore.TagDraftKey>();
        var projectPackId = EngineProjectIO.PACK_ID_PREFIX + projectName;
        var resourceManager = server.getResourceManager();

        server.registryAccess().registries().forEach(entry -> {
            var registryLoc = entry.key().location();
            var tagsDir = net.minecraft.core.registries.Registries.tagsDirPath(entry.key());
            entry.value().getTagNames().forEach(tk -> {
                var key = new ProjectTagDraftStore.TagDraftKey(registryLoc, tk.location());
                emittedKeys.add(key);
                // inUpstream = at least one non-project pack ships a JSON file at this tag's data path. Drives the
                // browser's "modified" (project + upstream) vs "new" (project-only) color distinction.
                var resourcePath = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    tk.location().getNamespace(),
                    tagsDir + "/" + tk.location().getPath() + ".json"
                );
                var hasUpstream = resourceManager.getResourceStack(resourcePath)
                    .stream()
                    .anyMatch(r -> !projectPackId.equals(r.sourcePackId()));
                var inProject = projectKeys.contains(key);
                // Only compute equivalentToUpstream for project-owned tags — checking it for upstream-only rows is
                // wasted I/O (the project doesn't have a JSON, so it can't be redundant).
                var equivalentToUpstream = inProject
                    && ProjectTagDraftStore.isEquivalentToUpstream(server, projectName, entry.key(), tk.location());
                entries.add(new TagCatalogEntry(registryLoc, tk.location(), inProject, hasUpstream, equivalentToUpstream));
            });
        });
        // Append project-only tags (those whose registry has no live tag of that name yet — typically because the
        // user just created the tag and hasn't reloaded). inUpstream=false since no other pack ships them either;
        // equivalentToUpstream=false because by definition there's no upstream to equate to.
        for (var pt : projectTags) {
            var key = new ProjectTagDraftStore.TagDraftKey(pt.registryKey(), pt.tagId());
            if (!emittedKeys.contains(key)) {
                entries.add(new TagCatalogEntry(pt.registryKey(), pt.tagId(), true, false, false));
            }
        }
        entries.sort((a, b) -> {
            var c = a.registryKey().toString().compareTo(b.registryKey().toString());
            return c != 0 ? c : a.tagId().toString().compareTo(b.tagId().toString());
        });
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CTagCatalogPayload(projectName, entries));
    }

    /**
     * Reply to {@link C2SRequestRegistryEntriesPayload} with the registry's element ids + existing tag names so the Tag
     * Editor's footer Add-entry picker can show every valid choice. Read-only; no op-gating. Returns silently if the
     * registry id doesn't resolve (stale client).
     */
    public static void handleRequestRegistryEntries(C2SRequestRegistryEntriesPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var server = serverPlayer.serverLevel().getServer();
        ResourceKey<? extends Registry<Object>> registryKey = ResourceKey.createRegistryKey(payload.registryKey());
        var registry = server.registryAccess().registry(registryKey).orElse(null);
        if (registry == null) {
            return;
        }
        var elements = new java.util.ArrayList<>(registry.keySet());
        elements.sort(java.util.Comparator.comparing(ResourceLocation::toString));
        var tagIds = registry.getTagNames()
            .map(tk -> tk.location())
            .sorted(java.util.Comparator.comparing(ResourceLocation::toString))
            .toList();
        BLib.MOD.networking().sendToClient(serverPlayer, new S2CRegistryEntriesPayload(payload.registryKey(), elements, tagIds));
    }

    /**
     * Send the active project's authoritative state for one tag back to the client. Lazy-seeds an empty {@code values}
     * array if the project has no override yet (vanilla / upstream entries are preserved by the additive merge at
     * reload time).
     */
    public static void handleRequestTagDraft(C2SRequestTagDraftPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }
        sendTagDraft(serverPlayer, payload.projectName(), payload.registryKey(), payload.tagId(), tag);
    }

    /** Append a new entry (direct or tag-ref) to a tag's project override JSON. Disk-only; echoes a fresh draft. */
    public static void handleAddTagEntry(C2SAddTagEntryPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }
        var beforeJson = readDiskTagJson(payload.projectName(), registryKey, payload.tagId());
        ProjectTagDraftStore.applyAddEntry(tag, payload.isTagRef(), payload.entryId(), payload.required());
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            beforeJson,
            tag,
            "Add tag entry " + payload.entryId()
        );
    }

    /**
     * Remove a registry-element entry from a tag's project override JSON by id (rather than by raw array index — the
     * block inspector edits "from the block's POV" and doesn't carry the tag's on-disk layout). Scans the tag's draft
     * for the first direct (non-tag-ref) entry whose id matches the payload's, and removes it. Entries that are only
     * present via tag-refs or via upstream packs aren't touched (vanilla JSON has no negation primitive), so the remove
     * silently no-ops in those cases. Disk-only; echoes a fresh draft either way.
     */
    public static void handleRemoveBlockTag(C2SRemoveBlockTagPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }

        var entries = ProjectTagDraftStore.extractDraftEntries(tag);
        var rawIndex = -1;
        for (var entry : entries) {
            if (!entry.isTagRef() && entry.id().equals(payload.entryId())) {
                rawIndex = entry.rawIndex();
                break;
            }
        }
        if (rawIndex < 0) {
            // No direct entry to remove — the block is in this tag only via a tag-ref or upstream pack. Echo the
            // current draft so the client's source view stays consistent (it didn't change, but the round-trip
            // re-syncs in case the client was stale).
            sendTagDraft(serverPlayer, payload.projectName(), payload.registryKey(), payload.tagId(), tag);
            return;
        }
        var beforeJson = readDiskTagJson(payload.projectName(), registryKey, payload.tagId());
        if (!ProjectTagDraftStore.applyRemoveEntry(tag, rawIndex)) {
            return;
        }
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            beforeJson,
            tag,
            "Remove tag entry " + payload.entryId()
        );
    }

    /** Remove the entry at {@code rawIndex} from a tag's project override JSON. Disk-only; echoes a fresh draft. */
    public static void handleRemoveTagEntry(C2SRemoveTagEntryPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }
        var beforeJson = readDiskTagJson(payload.projectName(), registryKey, payload.tagId());
        if (!ProjectTagDraftStore.applyRemoveEntry(tag, payload.rawIndex())) {
            return;
        }
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            beforeJson,
            tag,
            "Remove tag entry"
        );
    }

    /** Set a tag's {@code replace} flag. Disk-only; echoes a fresh draft. */
    public static void handleSetTagReplace(C2SSetTagReplacePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }
        var beforeJson = readDiskTagJson(payload.projectName(), registryKey, payload.tagId());
        ProjectTagDraftStore.applySetReplace(tag, payload.replace());
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            beforeJson,
            tag,
            "Set tag replace=" + payload.replace()
        );
    }

    /**
     * Toggle a tag entry's {@code required} flag in place. Equivalent to an in-place rewrite of one element of the
     * tag's {@code values} array — bare-string form for required, object form with {@code required: false} for
     * optional. Disk-only; echoes a fresh draft.
     */
    public static void handleSetTagEntryRequired(C2SSetTagEntryRequiredPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        var tag = ProjectTagDraftStore.INSTANCE.getOrSeedTag(
            serverPlayer.serverLevel().getServer(),
            payload.projectName(),
            registryKey,
            payload.tagId()
        );
        if (tag == null) {
            return;
        }
        var beforeJson = readDiskTagJson(payload.projectName(), registryKey, payload.tagId());
        if (!ProjectTagDraftStore.applySetEntryRequired(tag, payload.rawIndex(), payload.required())) {
            return;
        }
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            beforeJson,
            tag,
            "Toggle tag entry required"
        );
    }

    /**
     * Create a brand-new empty tag JSON. Refuses if the file already exists for this project. On success, pushes both a
     * refreshed {@link S2CTagCatalogPayload} (so the browser shows the new tag) and an {@link S2CTagDraftPayload} (so a
     * client that just opened the editor finds it populated).
     */
    public static void handleCreateTag(C2SCreateTagPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }
        if (ProjectDraftStore.INSTANCE.isReloading()) {
            return;
        }
        if (!validProjectFor(payload.projectName())) {
            return;
        }
        var registryKey = ResourceKey.<Registry<Object>>createRegistryKey(payload.registryKey());
        // Refuse if a project file already exists for this tag.
        if (EngineProjectIO.readTagJson(payload.projectName(), registryKey, payload.tagId()).isPresent()) {
            return;
        }
        var tag = new com.google.gson.JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", new com.google.gson.JsonArray());
        // beforeJson is null since we just refused to proceed if a file already existed. Persist via the helper
        // so undo collapses to the right delete-then-resync path.
        persistTagAndRecord(
            serverPlayer,
            payload.projectName(),
            registryKey,
            payload.tagId(),
            null,
            tag,
            "Create tag " + payload.tagId()
        );
        // The legacy create-tag path also pushed a catalog refresh because a new tag entered the project. Keep that
        // here so the Tag Browser updates immediately.
        pushCatalog(serverPlayer, payload.projectName());
    }

    /** Helper: encode a tag JSON + the live registry's resolved member set to the wire form and push to the client. */
    private static void sendTagDraft(
        ServerPlayer serverPlayer,
        String projectName,
        ResourceLocation registryKey,
        ResourceLocation tagId,
        com.google.gson.JsonObject tag
    ) {
        var rawEntries = ProjectTagDraftStore.extractDraftEntries(tag);
        var replace = ProjectTagDraftStore.readReplace(tag);
        var registryRk = ResourceKey.<Registry<Object>>createRegistryKey(registryKey);
        var server = serverPlayer.serverLevel().getServer();
        var resolved = ProjectTagDraftStore.extractResolvedMembers(server, registryRk, tagId);
        // Tag the wire entries with inUpstream so the inspector can hide no-op controls in merge mode: vanilla's
        // load-order merge means an upstream-duplicate entry sticks regardless of what the project's JSON says, so
        // removing or toggling required on it has no effect. The seed-from-upstream pass is repeated here (it's
        // also run inside getOrSeedTag) — the cost is per-edit-or-fetch, which is rare on the human timescale.
        var upstreamKeys = ProjectTagDraftStore.extractUpstreamEntryKeys(server, projectName, registryRk, tagId);
        var entries = rawEntries.stream()
            .map(
                e -> new com.blib.mod.common.network.packet.TagEntryDraft(
                    e.rawIndex(),
                    e.isTagRef(),
                    e.id(),
                    e.required(),
                    upstreamKeys.contains(ProjectTagDraftStore.entryKey(e.isTagRef(), e.id()))
                )
            )
            .toList();
        // inProject is sourced from disk, not from the in-memory draft cache: getOrSeedTag returns a synthesized
        // upstream-merge seed when the project hasn't authored the tag yet, so it isn't a reliable signal of
        // ownership. The disk-file existence check is. Read paths (handleRequestTagDraft) keep inProject=false
        // when no JSON has been written; edit paths flow through writeAndPersist first, so the file will exist by
        // the time we reach here.
        var inProject = EngineProjectIO.hasProjectTagJson(projectName, registryRk, tagId);
        BLib.MOD.networking()
            .sendToClient(serverPlayer, new S2CTagDraftPayload(projectName, registryKey, tagId, replace, entries, resolved, inProject));
    }

    /** Helper: rebuild and push the tag catalog to the client (used after create/delete). */
    private static void pushCatalog(ServerPlayer serverPlayer, String projectName) {
        handleRequestTagCatalog(new C2SRequestTagCatalogPayload(projectName), serverPlayer);
    }

    /**
     * Common end-of-edit pass for tag-mutating handlers: either persist the just-edited JSON or, when the edit leaves
     * the project's JSON equivalent to upstream (no net effect on the merged tag), delete the file instead.
     * Auto-cleanup keeps the datapack honest — a JSON that doesn't modify anything shouldn't claim to. Either way,
     * fires a fresh {@link S2CTagDraftPayload} so the client's view matches disk; the delete path also pushes a full
     * catalog refresh (the row may have become a project-only-no-upstream candidate for total removal, which the local
     * optimistic update can't represent).
     */
    private static void persistOrCleanupAndSend(
        ServerPlayer serverPlayer,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId,
        com.google.gson.JsonObject tag
    ) throws IOException {
        var server = serverPlayer.serverLevel().getServer();
        if (ProjectTagDraftStore.isEquivalentToUpstream(server, projectName, registryKey, tagId, tag)) {
            ProjectTagDraftStore.INSTANCE.deleteProjectTag(projectName, registryKey, tagId);
            var reseed = ProjectTagDraftStore.INSTANCE.getOrSeedTag(server, projectName, registryKey, tagId);
            if (reseed != null) {
                sendTagDraft(serverPlayer, projectName, registryKey.location(), tagId, reseed);
            }
            pushCatalog(serverPlayer, projectName);
        } else {
            ProjectTagDraftStore.INSTANCE.writeAndPersist(projectName, registryKey, tagId, tag);
            sendTagDraft(serverPlayer, projectName, registryKey.location(), tagId, tag);
        }
    }

    /**
     * Read the on-disk JSON for one tag override (or {@code null} if no project file exists). Used to capture the
     * before-state for {@link com.blib.mod.common.gameplay.history.ProjectJsonEdit}; the in-memory draft cache isn't a
     * reliable source since {@code getOrSeedTag} synthesizes seed contents that don't reflect file existence.
     */
    private static @Nullable com.google.gson.JsonObject readDiskTagJson(
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        return EngineProjectIO.readTagJson(projectName, registryKey, tagId)
            .filter(com.google.gson.JsonElement::isJsonObject)
            .map(com.google.gson.JsonElement::getAsJsonObject)
            .orElse(null);
    }

    /**
     * Post-mutation tail shared by all tag handlers: decide whether the final tag state collapses to "equivalent to
     * upstream" (and thus the override file should be deleted) or whether the file should be written; route the apply
     * through {@link com.blib.mod.common.gameplay.history.exec.ProjectActionExec#applyTagDraft}; push a matching
     * {@link com.blib.mod.common.gameplay.history.ProjectJsonEdit} for undo/redo.
     */
    private static void persistTagAndRecord(
        ServerPlayer serverPlayer,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId,
        @Nullable com.google.gson.JsonObject beforeJson,
        com.google.gson.JsonObject tag,
        String description
    ) {
        var server = serverPlayer.serverLevel().getServer();
        var afterJson = ProjectTagDraftStore.isEquivalentToUpstream(server, projectName, registryKey, tagId, tag)
            ? null
            : tag.deepCopy();
        com.blib.mod.common.gameplay.history.exec.ProjectActionExec.applyTagDraft(
            server,
            projectName,
            registryKey.location(),
            tagId,
            afterJson
        );
        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.ProjectJsonEdit(
                projectName,
                com.blib.mod.common.gameplay.history.ProjectJsonEdit.Kind.TAG,
                null,
                registryKey.location(),
                tagId,
                beforeJson,
                afterJson,
                description,
                System.currentTimeMillis()
            )
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
        var level = server.getLevel(dimensionKey);
        var sourceAabb = aabbFromCorners(payload.cornerA(), payload.cornerB());
        var destAabb = sourceAabb.moved(payload.dx(), payload.dy(), payload.dz());

        // Capture pre-state for both AABBs even when copy=true — destAabb still gets overwritten. Source pre-state is
        // load-bearing for cut (when copy=false) since the source is cleared.
        var sourceBeforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var sourceBeforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        var destBeforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var destBeforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        if (level != null) {
            captureRegion(level, sourceAabb, sourceBeforeStates, sourceBeforeBE);
            captureRegion(level, destAabb, destBeforeStates, destBeforeBE);
        }

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

        if (!result.success() || level == null) {
            return;
        }
        var sourceAfterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var sourceAfterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        var destAfterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var destAfterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        captureRegion(level, sourceAabb, sourceAfterStates, sourceAfterBE);
        captureRegion(level, destAabb, destAfterStates, destAfterBE);

        var regions = new java.util.ArrayList<com.blib.mod.common.gameplay.history.RegionSnapshot>(2);
        regions.add(
            new com.blib.mod.common.gameplay.history.RegionSnapshot(
                sourceAabb,
                sourceBeforeStates,
                sourceBeforeBE,
                sourceAfterStates,
                sourceAfterBE
            )
        );
        regions.add(
            new com.blib.mod.common.gameplay.history.RegionSnapshot(destAabb, destBeforeStates, destBeforeBE, destAfterStates, destAfterBE)
        );
        var description = payload.copy() ? "Copy selection" : "Move selection";
        var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
            dimensionKey,
            regions,
            description,
            System.currentTimeMillis(),
            null
        );
        ActionHistory.push(action);
    }

    /** Build an inclusive bounding box from two arbitrary corners (the corners can be in any order on each axis). */
    private static net.minecraft.world.level.levelgen.structure.BoundingBox aabbFromCorners(
        net.minecraft.core.BlockPos a,
        net.minecraft.core.BlockPos b
    ) {
        return new net.minecraft.world.level.levelgen.structure.BoundingBox(
            Math.min(a.getX(), b.getX()),
            Math.min(a.getY(), b.getY()),
            Math.min(a.getZ(), b.getZ()),
            Math.max(a.getX(), b.getX()),
            Math.max(a.getY(), b.getY()),
            Math.max(a.getZ(), b.getZ())
        );
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
        var level = server.getLevel(dimensionKey);

        // Only push history when this is a cut (deleteSource=true) — pure copy doesn't mutate the world. We capture the
        // pre-cut state from the source AABB; the post-cut state is air for every cell, no BE NBT.
        var aabb = aabbFromCorners(payload.cornerA(), payload.cornerB());
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        if (payload.deleteSource() && level != null) {
            captureRegion(level, aabb, beforeStates, beforeBE);
        }

        var result = BlockClipboardEngine.copy(server, payload.cornerA(), payload.cornerB(), payload.deleteSource(), dimensionKey);

        if (payload.deleteSource() && result.success() && level != null) {
            var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
            var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
            captureRegion(level, aabb, afterStates, afterBE);

            var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
            var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
                dimensionKey,
                java.util.List.of(region),
                "Cut selection",
                System.currentTimeMillis(),
                null
            );
            ActionHistory.push(action);
        }
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
        var level = server.getLevel(dimensionKey);
        var size = ServerBlockClipboard.size();
        net.minecraft.world.level.levelgen.structure.BoundingBox aabb = null;
        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        if (size != null && level != null) {
            var dest = payload.destination();
            aabb = new net.minecraft.world.level.levelgen.structure.BoundingBox(
                dest.getX(),
                dest.getY(),
                dest.getZ(),
                dest.getX() + size.getX() - 1,
                dest.getY() + size.getY() - 1,
                dest.getZ() + size.getZ() - 1
            );
            captureRegion(level, aabb, beforeStates, beforeBE);
        }

        var result = BlockClipboardEngine.paste(server, payload.destination(), dimensionKey);

        if (aabb != null && result.success()) {
            var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
            var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
            captureRegion(level, aabb, afterStates, afterBE);

            var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
            var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
                dimensionKey,
                java.util.List.of(region),
                "Paste from clipboard",
                System.currentTimeMillis(),
                null
            );
            ActionHistory.push(action);
        }
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
        var level = server.getLevel(dimensionKey);
        var aabb = aabbFromCorners(payload.cornerA(), payload.cornerB());

        var beforeStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
        var beforeBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
        if (level != null) {
            captureRegion(level, aabb, beforeStates, beforeBE);
        }

        var result = BlockClipboardEngine.delete(server, payload.cornerA(), payload.cornerB(), dimensionKey);

        if (result.success() && level != null) {
            var afterStates = new java.util.HashMap<net.minecraft.core.BlockPos, BlockState>();
            var afterBE = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.nbt.CompoundTag>();
            captureRegion(level, aabb, afterStates, afterBE);

            var region = new com.blib.mod.common.gameplay.history.RegionSnapshot(aabb, beforeStates, beforeBE, afterStates, afterBE);
            var action = new com.blib.mod.common.gameplay.history.BlockRegionEdit(
                dimensionKey,
                java.util.List.of(region),
                "Delete selection",
                System.currentTimeMillis(),
                null
            );
            ActionHistory.push(action);
        }
    }

    /**
     * Common reload path used by {@link #handleReloadProject}. Sets the {@code reloading} guard, kicks off
     * {@code reloadResources}, chains the success/failure reply onto its completion, and clears the project's draft
     * cache so the next edit re-seeds from the freshly-imported registry.
     */
    private static void runReload(ServerPlayer serverPlayer, String projectName) {
        var server = serverPlayer.serverLevel().getServer();
        ProjectDraftStore.INSTANCE.setReloading(true);
        try {
            var future = EngineProjectIO.reloadProject(server, projectName);
            future.whenComplete((v, t) -> {
                try {
                    ProjectDraftStore.INSTANCE.clearProject(projectName);
                    ProjectTagDraftStore.INSTANCE.clearProject(projectName);
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
        var beforeValue = readFactionFieldAsString(faction, field);
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

        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.FactionEdit(
                payload.factionId(),
                com.blib.mod.common.gameplay.history.FactionEdit.Kind.FIELD,
                payload.fieldOrdinal(),
                beforeValue,
                value,
                null,
                0,
                0,
                null,
                "Edit faction " + field.name().toLowerCase(),
                System.currentTimeMillis()
            )
        );
    }

    /**
     * Read the current value of a faction field in the same string form the wire payload uses. Used to capture the
     * before-value for undo. Mirrors the parsing in the switch above — color is hex, enums are {@code .name()},
     * booleans are {@code String.valueOf}.
     */
    private static String readFactionFieldAsString(
        com.blib.api.common.faction.v1.Faction<?> faction,
        C2SUpdateFactionFieldPayload.Field field
    ) {
        return switch (field) {
            case NAME -> faction.name();
            case COLOR -> String.format("#%06X", faction.color() & 0xFFFFFF);
            case CLAIM_VISIBILITY -> faction.claimVisibility().name();
            case BLOCK_BREAK_PROTECTION -> faction.blockBreakProtection().name();
            case BLOCK_INTERACT_PROTECTION -> faction.blockInteractProtection().name();
            case ENTITY_INTERACT_PROTECTION -> faction.entityInteractProtection().name();
            case NONLIVING_ENTITY_ATTACK_PROTECTION -> faction.nonLivingEntityAttackProtection().name();
            case ALLOW_PVP -> String.valueOf(faction.allowPvp());
            case ALLOW_EXPLOSIONS -> String.valueOf(faction.allowExplosions());
            case ALLOW_MOB_GRIEFING -> String.valueOf(faction.allowMobGriefing());
        };
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
        var beforeState = BLibFactionManager.INSTANCE.getRelationship(payload.factionA(), payload.factionB());
        BLibFactionManager.INSTANCE.setRelationship(payload.factionA(), payload.factionB(), states[payload.stateOrdinal()]);
        BLibFactionManager.INSTANCE.pushDirectoryToAllClients(sp.server);

        ActionHistory.push(
            new com.blib.mod.common.gameplay.history.FactionEdit(
                payload.factionA(),
                com.blib.mod.common.gameplay.history.FactionEdit.Kind.RELATIONSHIP,
                0,
                null,
                null,
                payload.factionB(),
                beforeState.ordinal(),
                payload.stateOrdinal(),
                null,
                "Set faction relationship",
                System.currentTimeMillis()
            )
        );
    }

    /** Add a member to a faction. Pushes the directory (member count) + members roster + entity reverse-lookup. */
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
            pushEntityFactionsTo(sp, payload.memberUuid());

            ActionHistory.push(
                new com.blib.mod.common.gameplay.history.FactionEdit(
                    payload.factionId(),
                    com.blib.mod.common.gameplay.history.FactionEdit.Kind.ADD_MEMBER,
                    0,
                    null,
                    null,
                    null,
                    0,
                    0,
                    payload.memberUuid(),
                    "Add faction member",
                    System.currentTimeMillis()
                )
            );
        }
    }

    /** Remove a member from a faction. Pushes the directory + members roster + entity reverse-lookup on success. */
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
            pushEntityFactionsTo(sp, payload.memberUuid());

            ActionHistory.push(
                new com.blib.mod.common.gameplay.history.FactionEdit(
                    payload.factionId(),
                    com.blib.mod.common.gameplay.history.FactionEdit.Kind.REMOVE_MEMBER,
                    0,
                    null,
                    null,
                    null,
                    0,
                    0,
                    payload.memberUuid(),
                    "Remove faction member",
                    System.currentTimeMillis()
                )
            );
        }
    }

    /**
     * Reverse lookup: list every faction whose membership contains the given UUID, send the result back. Reads only —
     * the workspace's op gate upstream is already what determines who can ask. Powers the engine Inspector "Factions"
     * section and the right-click "Manage Factions" popup.
     */
    public static void handleRequestEntityFactions(C2SRequestEntityFactionsPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp)) {
            return;
        }
        pushEntityFactionsTo(sp, payload.memberUuid());
    }

    /** Helper: query the manager's reverse index for {@code uuid} and ship the result to this one player. */
    private static void pushEntityFactionsTo(ServerPlayer sp, java.util.UUID uuid) {
        var factionIds = BLibFactionManager.INSTANCE.getFactionIds(uuid);
        BLib.MOD
            .networking()
            .sendToClient(sp, new S2CEntityFactionsPayload(uuid, java.util.List.copyOf(factionIds)));
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
        var level = sp.serverLevel();
        var pos = new ChunkPos(payload.chunkX(), payload.chunkZ());
        var added = BLibTerritoryManager.INSTANCE.addClaim(level, pos, payload.factionId());
        if (!added) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(payload.factionId());
        var factionName = faction != null ? faction.name() : payload.factionId().toString();
        var action = new com.blib.mod.common.gameplay.history.ChunkClaimEdit(
            level.dimension(),
            payload.chunkX(),
            payload.chunkZ(),
            payload.factionId(),
            com.blib.mod.common.gameplay.history.ChunkClaimEdit.Direction.ADDED,
            "Claim chunk for " + factionName,
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
    }

    /** Unclaim a chunk for a faction. Same auto-sync path as {@link #handleAddChunkClaim}. */
    public static void handleRemoveChunkClaim(C2SRemoveChunkClaimPayload payload, Player player) {
        if (!(player instanceof ServerPlayer sp) || !sp.hasPermissions(2)) {
            return;
        }
        var level = sp.serverLevel();
        var pos = new ChunkPos(payload.chunkX(), payload.chunkZ());
        var removed = BLibTerritoryManager.INSTANCE.removeClaim(level, pos, payload.factionId());
        if (!removed) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(payload.factionId());
        var factionName = faction != null ? faction.name() : payload.factionId().toString();
        var action = new com.blib.mod.common.gameplay.history.ChunkClaimEdit(
            level.dimension(),
            payload.chunkX(),
            payload.chunkZ(),
            payload.factionId(),
            com.blib.mod.common.gameplay.history.ChunkClaimEdit.Direction.REMOVED,
            "Unclaim chunk for " + factionName,
            System.currentTimeMillis()
        );
        ActionHistory.push(action);
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
