package com.blib.mod.common.gameplay.history;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.mod.common.gameplay.jigsaw.PlacedPiece;

/**
 * Optional addendum on a {@link BlockRegionEdit} when the gesture also changed the {@link PlacedPiece} registry — piece
 * placement, deletion, or identity-preserving move. {@code addOnRedo} is the piece state to register when re-applying;
 * {@code removeOnRedo} is the UUID to drop. Symmetric on revert: add becomes remove, remove becomes add. Plain block
 * edits leave this {@code null}.
 *
 * @param addOnRedo       Piece to put back into the store on {@code redo()} (and remove on {@code revert()}).
 * @param removeOnRedo    Piece UUID to drop on {@code redo()}; the matching state to re-register on {@code revert()}
 *                        lives in {@link #removedSnapshot}.
 * @param removedSnapshot Snapshot of the piece prior to the gesture so {@code revert()} can resurrect it.
 */
@ApiStatus.Internal
public record PieceLink(
    @Nullable PlacedPiece addOnRedo,
    @Nullable UUID removeOnRedo,
    @Nullable PlacedPiece removedSnapshot
) {

    /** Convenience: piece placement gesture — registers piece on redo, removes it on revert. */
    public static PieceLink placement(PlacedPiece piece) {
        return new PieceLink(piece, null, null);
    }

    /** Convenience: piece deletion gesture — removes piece on redo, resurrects from snapshot on revert. */
    public static PieceLink deletion(PlacedPiece removedSnapshot) {
        return new PieceLink(null, removedSnapshot.id(), removedSnapshot);
    }

    /** Convenience: piece move gesture — replaces old record with new on redo, and inverse on revert. */
    public static PieceLink move(PlacedPiece before, PlacedPiece after) {
        return new PieceLink(after, before.id(), before);
    }
}
