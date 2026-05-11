package com.blib.engine.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;

/**
 * {@link Selectable} wrapping a placed jigsaw piece — a region of blocks that was stamped into the world via the
 * engine's piece palette, tracked server-side and synced down for hover / selection. Holds only the piece's UUID; the
 * live record (template id, AABB, rotation, etc.) is looked up on demand from {@link ClientPlacedPieceRegistry} so the
 * selectable can't pin a stale copy of metadata after a remove broadcast.
 * <p>
 * Validity collapses to "this UUID is still in the client registry". Once the server broadcasts a removal (delete,
 * undo) the entry is gone from the registry and {@link SelectionManager} prunes us on next read.
 */
@ApiStatus.Internal
public final class PlacedJigsawPieceSelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final UUID id;

    public PlacedJigsawPieceSelectable(UUID id) {
        this.id = id;
    }

    public UUID id() {
        return id;
    }

    @Override
    public SelectableType type() {
        return SelectableType.JIGSAW_PIECE;
    }

    @Override
    public Component displayName() {
        var piece = ClientPlacedPieceRegistry.get(id);
        if (piece == null) {
            return Component.literal("(removed piece)");
        }
        var size = piece.aabb();
        var w = size.maxX() - size.minX() + 1;
        var h = size.maxY() - size.minY() + 1;
        var d = size.maxZ() - size.minZ() + 1;
        return Component.literal(piece.templateId() + " (" + w + "×" + h + "×" + d + ")");
    }

    @Override
    public AABB worldBounds() {
        var piece = ClientPlacedPieceRegistry.get(id);
        if (piece == null) {
            return EMPTY_BOUNDS;
        }
        return piece.worldAabb();
    }

    @Override
    public boolean isValid() {
        return ClientPlacedPieceRegistry.get(id) != null;
    }

    /**
     * Promote a piece selection to a block-volume selection over its AABB. Sets {@link BlockSelection}'s corners,
     * optionally seeds the gizmo mode (so e.g. clicking the Scale tool button in the piece inspector lands the user in
     * Scale mode directly), and selects a fresh {@link BlockVolumeSelectable}. After this, the inspector / hover /
     * gizmos all behave exactly as if the user had drawn the volume by hand.
     * <p>
     * No-op if the piece UUID is no longer in the client registry — the piece may have been removed between the user
     * clicking and us getting here, in which case there's nothing meaningful to promote to.
     */
    public static void promoteToVolume(UUID pieceId, @org.jetbrains.annotations.Nullable BlockSelection.GizmoMode seedMode) {
        var piece = ClientPlacedPieceRegistry.get(pieceId);
        if (piece == null) {
            return;
        }
        var aabb = piece.aabb();
        BlockSelection.setCornersDirect(
            new BlockPos(aabb.minX(), aabb.minY(), aabb.minZ()),
            new BlockPos(aabb.maxX(), aabb.maxY(), aabb.maxZ())
        );
        if (seedMode != null) {
            BlockSelection.setGizmoMode(seedMode);
        }
        SelectionManager.selectSingle(new BlockVolumeSelectable());
    }
}
