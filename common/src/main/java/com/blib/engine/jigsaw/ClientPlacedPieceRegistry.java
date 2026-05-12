package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.blib.mod.common.gameplay.jigsaw.PlacedPiece;

/**
 * Client-side mirror of the server's per-level {@code PlacedPieceStore}. Populated from
 * {@code S2CSyncPlacedPiecesPayload} on engine-mode entry and kept in sync by add / remove broadcasts as the user
 * places and deletes pieces.
 * <p>
 * Only pieces for the currently-loaded dimension are kept — dimension changes / re-syncs replace the whole map. Lookups
 * are O(1) by UUID; the hover raycast iterates every piece (cheap for the < ~hundreds we expect in an authoring
 * session).
 */
@ApiStatus.Internal
public final class ClientPlacedPieceRegistry {

    private static final Map<UUID, PlacedPiece> BY_ID = new HashMap<>();

    private ClientPlacedPieceRegistry() {}

    public static void replaceAll(Collection<PlacedPiece> pieces) {
        BY_ID.clear();
        for (var p : pieces) {
            BY_ID.put(p.id(), p);
        }
    }

    public static void add(PlacedPiece piece) {
        BY_ID.put(piece.id(), piece);
    }

    public static void remove(UUID id) {
        BY_ID.remove(id);
    }

    public static void clear() {
        BY_ID.clear();
    }

    public static @Nullable PlacedPiece get(UUID id) {
        return BY_ID.get(id);
    }

    public static Collection<PlacedPiece> all() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    /**
     * Raycast every known piece for the client's current dimension; return the closest AABB hit. {@code null} when no
     * piece intersects the ray within {@code maxDist}.
     */
    public static @Nullable HoverHit raycast(Vec3 origin, Vec3 dir, double maxDist) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        var currentDimension = mc.level.dimension();
        var end = origin.add(dir.scale(maxDist));

        UUID bestId = null;
        double bestDistSq = Double.MAX_VALUE;
        Vec3 bestHit = null;
        for (var piece : BY_ID.values()) {
            if (!piece.dimension().equals(currentDimension)) {
                continue;
            }
            var aabb = piece.worldAabb();
            var clip = aabb.clip(origin, end);
            if (clip.isEmpty()) {
                continue;
            }
            var hit = clip.get();
            var distSq = origin.distanceToSqr(hit);
            if (distSq < bestDistSq) {
                bestId = piece.id();
                bestDistSq = distSq;
                bestHit = hit;
            }
        }
        if (bestId == null) {
            return null;
        }
        return new HoverHit(bestId, bestHit, bestDistSq);
    }

    public record HoverHit(
        UUID pieceId,
        Vec3 hitVec,
        double distanceSq
    ) {}
}
