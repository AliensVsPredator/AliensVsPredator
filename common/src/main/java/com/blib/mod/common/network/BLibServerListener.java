package com.blib.mod.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.gameplay.jigsaw.PlacementHistory;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;

/**
 * Server-side handlers for client → server packets. Mirror of {@link BLibClientListener} for the C2S direction — each
 * handler is invoked on the server thread with the payload + the sending {@link Player}.
 */
@ApiStatus.Internal
public final class BLibServerListener {

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
