package com.blib.mod.common.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.internal.mixin.MixinStructureTemplatePool_Accessor;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.gameplay.jigsaw.PlacementHistory;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.internal.common.storage.PoolEditorSaveIO;
import com.blib.mod.common.network.packet.C2SAddPoolElementPayload;
import com.blib.mod.common.network.packet.C2SRemovePoolElementPayload;
import com.blib.mod.common.network.packet.C2SSavePoolPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;
import com.blib.mod.common.network.packet.C2SUpdatePoolElementPayload;

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
     * clients via {@link net.minecraft.world.level.Level#sendBlockUpdated} so any nearby observer sees the new NBT
     * on their next BE sync — without this, the inspector that just sent the packet would see stale state until the
     * chunk happened to re-sync for some other reason.
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
     * Apply a live mutation to a structure template pool — set the weight + projection of one top-level element.
     * The pool is mutated in place via {@link MixinStructureTemplatePool_Accessor}; new generations from this point
     * forward use the updated values. Edits do not persist to disk; the world's next reload reverts to JSON state.
     * Op-gated like the other engine packets.
     * <p>
     * After updating {@code rawTemplates}, we rebuild the expanded {@code templates} list — vanilla constructs that
     * once in {@code StructureTemplatePool}'s constructor as {@code element repeated weight times}; we redo the same
     * expansion so subsequent {@code getRandomTemplate} calls reflect the new weights.
     */
    public static void handleUpdatePoolElement(C2SUpdatePoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var registry = serverPlayer.serverLevel().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(payload.poolId());
        if (pool == null) {
            return;
        }

        var accessor = (MixinStructureTemplatePool_Accessor) (Object) pool;
        var rawTemplates = new ArrayList<>(accessor.getElementCounts());
        var idx = payload.rawIndex();
        if (idx < 0 || idx >= rawTemplates.size()) {
            return;
        }
        var oldPair = rawTemplates.get(idx);
        // Phase 2 only addresses top-level SinglePoolElements. ListPoolElement / EmptyPoolElement / etc. need
        // separate handling and aren't reachable from the editor's UI (their rawIndex is -1 there).
        if (!(oldPair.getFirst() instanceof SinglePoolElement element)) {
            return;
        }

        var newWeight = Math.max(1, payload.newWeight());
        var projValues = StructureTemplatePool.Projection.values();
        if (payload.newProjectionOrdinal() >= 0 && payload.newProjectionOrdinal() < projValues.length) {
            element.setProjection(projValues[payload.newProjectionOrdinal()]);
        }

        rawTemplates.set(idx, Pair.of(element, newWeight));
        accessor.setElementCounts(rawTemplates);
        rebuildExpandedTemplates(accessor, rawTemplates);
    }

    /**
     * Append a new {@link SinglePoolElement} (pointing at {@code templateId}) to the pool's {@code rawTemplates}.
     * Used by the Pool Editor's footer "Add piece" picker. Weight is clamped to ≥1; projection ordinal is
     * bounds-checked. Rebuilds the expanded templates list so generation reflects the addition.
     */
    public static void handleAddPoolElement(C2SAddPoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var registry = serverPlayer.serverLevel().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(payload.poolId());
        if (pool == null) {
            return;
        }

        var projValues = StructureTemplatePool.Projection.values();
        if (payload.projectionOrdinal() < 0 || payload.projectionOrdinal() >= projValues.length) {
            return;
        }

        var element = StructurePoolElement
            .single(payload.templateId().toString())
            .apply(projValues[payload.projectionOrdinal()]);

        var accessor = (MixinStructureTemplatePool_Accessor) (Object) pool;
        var rawTemplates = new ArrayList<>(accessor.getElementCounts());
        rawTemplates.add(Pair.of(element, Math.max(1, payload.weight())));
        accessor.setElementCounts(rawTemplates);
        rebuildExpandedTemplates(accessor, rawTemplates);
    }

    /**
     * Remove the entry at {@code rawIndex} from the pool's {@code rawTemplates}. Used by the Pool Editor's per-row
     * "×" button. Out-of-range indices are silently ignored — likely a stale client-side reference (e.g. user
     * clicked × on a row that was already removed by another packet). Rebuilds the expanded templates list so
     * generation skips the removed element.
     */
    public static void handleRemovePoolElement(C2SRemovePoolElementPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            return;
        }

        var registry = serverPlayer.serverLevel().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(payload.poolId());
        if (pool == null) {
            return;
        }

        var accessor = (MixinStructureTemplatePool_Accessor) (Object) pool;
        var rawTemplates = new ArrayList<>(accessor.getElementCounts());
        if (payload.rawIndex() < 0 || payload.rawIndex() >= rawTemplates.size()) {
            return;
        }
        rawTemplates.remove(payload.rawIndex());
        accessor.setElementCounts(rawTemplates);
        rebuildExpandedTemplates(accessor, rawTemplates);
    }

    /**
     * Save the current in-memory state of the pool to disk under the auto-managed {@code blib_engine} datapack,
     * then trigger a server-wide resource reload so the on-disk state replaces the live edits. Op-gated.
     * <p>
     * On first save the pack folder + {@code pack.mcmeta} are created and (if not already present) added to the
     * world's selected pack ids. Subsequent saves just overwrite the JSON. The reload is async; the file is on disk
     * by the time this method returns, but the registry refresh completes a few hundred ms later.
     */
    public static void handleSavePool(C2SSavePoolPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            LOGGER.warn("[BLib] handleSavePool: not a ServerPlayer; bailing.");
            return;
        }
        if (!serverPlayer.hasPermissions(2)) {
            LOGGER.warn("[BLib] handleSavePool: player {} lacks op-perm 2; bailing.", serverPlayer.getGameProfile().getName());
            return;
        }

        var server = serverPlayer.serverLevel().getServer();
        var registryAccess = server.registryAccess();
        var registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(payload.poolId());
        if (pool == null) {
            LOGGER.warn("[BLib] handleSavePool: pool {} not in registry; bailing.", payload.poolId());
            return;
        }

        com.google.gson.JsonElement json;
        try {
            // DIRECT_CODEC encodes the whole pool — but it has a Holder<StructureTemplatePool> fallback field, and
            // Holder serialization needs registry context. RegistryOps wraps JsonOps with the registry lookup so
            // the holder reference round-trips cleanly to its ResourceLocation. (Plain JsonOps.INSTANCE silently
            // throws on the holder, which is what was happening before — pool was never written, no error
            // surfaced because we caught Throwable and returned.)
            var ops = registryAccess.createSerializationContext(JsonOps.INSTANCE);
            json = StructureTemplatePool.DIRECT_CODEC
                .encodeStart(ops, pool)
                .getOrThrow();
        } catch (Throwable t) {
            LOGGER.error("[BLib] handleSavePool: codec encode failed for pool {}", payload.poolId(), t);
            return;
        }

        try {
            var written = PoolEditorSaveIO.writePool(server, payload.poolId(), json);
            LOGGER.info("[BLib] handleSavePool: wrote pool {} to {}", payload.poolId(), written);
        } catch (java.io.IOException e) {
            LOGGER.error("[BLib] handleSavePool: disk write failed for pool {}", payload.poolId(), e);
            return;
        }

        // Make sure the auto-managed pack is discovered + selected, then reload everything. reload() refreshes the
        // pack list (picks up our just-created folder); reloadResources then re-imports all selected packs.
        var packRepo = server.getPackRepository();
        packRepo.reload();
        // Pack id format depends on the source's PackSource — for world datapack folders, the id is normally
        // prefixed with "file/", but check both forms defensively in case the prefix differs across loader / MC
        // versions. Picking the first id ending in our pack name is robust to either.
        String actualPackId = packRepo.getAvailableIds().stream()
            .filter(id -> id.equals(PoolEditorSaveIO.PACK_NAME) || id.endsWith("/" + PoolEditorSaveIO.PACK_NAME))
            .findFirst()
            .orElse(null);
        if (actualPackId == null) {
            LOGGER.warn(
                "[BLib] handleSavePool: pack '{}' not found in available ids after reload (available: {}); "
                + "file is on disk but won't be loaded until enabled manually.",
                PoolEditorSaveIO.PACK_NAME,
                packRepo.getAvailableIds()
            );
            return;
        }
        var selected = new ArrayList<>(packRepo.getSelectedIds());
        if (!selected.contains(actualPackId)) {
            selected.add(actualPackId);
        }
        server.reloadResources(selected);
    }

    /**
     * Rebuild the weight-expanded {@code templates} list from {@code rawTemplates} — the same logic vanilla runs in
     * {@code StructureTemplatePool}'s constructor (each element repeated {@code weight} times). Doesn't re-shuffle:
     * we preserve the existing seed-driven pick order so generations stay reproducible across edits.
     */
    private static void rebuildExpandedTemplates(MixinStructureTemplatePool_Accessor accessor, List<Pair<StructurePoolElement, Integer>> rawTemplates) {
        ObjectArrayList<StructurePoolElement> expanded = accessor.getElements();
        expanded.clear();
        for (var pair : rawTemplates) {
            for (var i = 0; i < pair.getSecond(); i++) {
                expanded.add(pair.getFirst());
            }
        }
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
}
