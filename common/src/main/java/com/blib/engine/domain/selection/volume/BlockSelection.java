package com.blib.engine.domain.selection.volume;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import com.blib.internal.common.capture.CaptureMode;
import com.blib.mod.common.network.packet.S2CMoveSelectionResultPayload;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;

/**
 * Client-side singleton holding the engine workspace's block-volume selection: the two corner block positions, the
 * active gizmo mode (translate / scale / move-blocks), which corner is being picked next (if any), the capture-output
 * mode (general / jigsaw) used by the Capture operation, and the cached list of captures the server has reported for
 * the active project.
 * <p>
 * The selection itself is the underlying primitive; <em>operations</em> on it (Capture, Move Blocks, …) live on top — a
 * single AABB is shared across all of them so users keep their volume across operation switches. Mirrors the
 * relationship between {@code JigsawPieceSelection} (per-tool transient state) and {@code ProjectSession}
 * (per-engine-session cached server data) — both responsibilities are small enough to live in one class.
 * <p>
 * Cleared on engine workspace close. Single-player only — no per-player partitioning needed.
 */
@ApiStatus.Internal
public final class BlockSelection {

    public enum PickingState {
        NONE,
        A,
        B
    }

    /**
     * Active manipulation tool. Mirrors the Blender / MagicaVoxel convention of one active edit tool at a time (driven
     * via the workspace toolbar + {@code T} / {@code S} / {@code M} hotkeys). {@link #SCALE_VOLUME} resizes the AABB by
     * dragging face arrows; {@link #TRANSLATE_VOLUME} moves the AABB without affecting blocks; {@link #MOVE_BLOCKS}
     * lifts the blocks within the AABB and translates them to a new world location (Paint.NET floating-selection
     * semantics — implemented in Phase 2).
     */
    public enum GizmoMode {
        TRANSLATE_VOLUME,
        SCALE_VOLUME,
        MOVE_BLOCKS
    }

    private static @Nullable BlockPos cornerA;

    private static @Nullable BlockPos cornerB;

    private static PickingState picking = PickingState.NONE;

    private static CaptureMode mode = CaptureMode.GENERAL;

    /** Active edit tool; defaults to {@link GizmoMode#SCALE_VOLUME} since users typically resize after corner-pick. */
    private static GizmoMode gizmoMode = GizmoMode.SCALE_VOLUME;

    /** Captures list cached from {@code S2CCaptureListPayload}. Empty until the panel requests one. */
    private static List<String> captures = List.of();

    /**
     * Most-recent CAPTURE op result the server has pushed back. The Capture Panel polls + consumes this each frame so
     * the "Capturing…" spinner gets replaced with the actual outcome (success summary or error message). Polled rather
     * than callback-routed because the panel renders every frame anyway and the polling cost is one null-check.
     */
    private static @Nullable S2CProjectOpResultPayload pendingCaptureResult;

    /**
     * Active Move Blocks drag offset (delta from origin AABB to ghost destination), or {@code null} when no drag is in
     * progress. While non-null, the ghost renderer paints a destination preview at {@code aabb + offset}; release
     * commits via {@link com.blib.mod.common.network.packet.C2SMoveSelectionPayload} and the listener clears this on
     * the server's success/failure reply.
     */
    private static @Nullable BlockPos moveOffset;

    /**
     * Whether the active Move Blocks drag was started with Alt held. Captured at drag-begin (latched, not live-sampled)
     * so a stray Alt release mid-drag can't flip "move" into "copy"; mirrors the existing MMB camera-gesture pattern.
     */
    private static boolean moveCopyMode;

    /**
     * Most-recent Move Blocks server reply, drained by the panel for status display and by the listener for the AABB
     * shift.
     */
    private static @Nullable S2CMoveSelectionResultPayload pendingMoveResult;

    private BlockSelection() {}

    public static @Nullable BlockPos cornerA() {
        return cornerA;
    }

    public static @Nullable BlockPos cornerB() {
        return cornerB;
    }

    public static PickingState picking() {
        return picking;
    }

    public static CaptureMode mode() {
        return mode;
    }

    public static void setMode(CaptureMode newMode) {
        mode = newMode == null ? CaptureMode.GENERAL : newMode;
    }

    public static GizmoMode gizmoMode() {
        return gizmoMode;
    }

    public static void setGizmoMode(GizmoMode newMode) {
        gizmoMode = newMode == null ? GizmoMode.SCALE_VOLUME : newMode;
    }

    /** Begin picking the named corner. Cancels any in-progress pick on the other corner. */
    public static void beginPick(PickingState which) {
        if (which == PickingState.NONE) {
            picking = PickingState.NONE;
            return;
        }
        picking = which;
    }

    public static void cancelPick() {
        picking = PickingState.NONE;
    }

    /** Called by the viewport when the user clicks while picking. Stores the corner and exits picking state. */
    public static void onBlockClicked(BlockPos pos) {
        switch (picking) {
            case A -> cornerA = pos;
            case B -> cornerB = pos;
            case NONE -> { /* not picking — ignore */ }
        }
        picking = PickingState.NONE;
    }

    /**
     * Replace both corners with the componentwise min/max of the supplied positions. Used when the user genuinely wants
     * to (re-)assign the AABB bounds without preserving which corner is "A" vs "B" — e.g. corner picks (where each
     * click defines a fresh A or B) or paste shifts (where the destination volume reassigns both corners). Note that
     * this <em>swaps corner labels</em> if the user's prior A/B labelling didn't already match componentwise min/max;
     * for translate / move operations that should preserve labels, use {@link #setCornersDirect} or
     * {@link #translateCorners} instead.
     */
    public static void setBounds(BlockPos min, BlockPos max) {
        cornerA = new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
        cornerB = new BlockPos(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()));
    }

    /**
     * Assign {@code a} to {@link #cornerA} and {@code b} to {@link #cornerB} without any reordering. Used by gizmo drag
     * handlers that need to preserve the user's A/B labelling — translating or scaling shouldn't swap which corner is
     * which, even on sub-block (delta=0) drag frames where the AABB bounds technically don't change.
     */
    public static void setCornersDirect(BlockPos a, BlockPos b) {
        cornerA = a;
        cornerB = b;
    }

    /**
     * Translate both corners by {@code (dx, dy, dz)} in place. No-op for null corners. Identity-preserving — A stays A,
     * B stays B; their relative positions in the AABB are preserved since both shift by the same offset.
     */
    public static void translateCorners(int dx, int dy, int dz) {
        if (cornerA != null) {
            cornerA = cornerA.offset(dx, dy, dz);
        }
        if (cornerB != null) {
            cornerB = cornerB.offset(dx, dy, dz);
        }
    }

    /** Bounding box from the two corners. Empty if either corner is unset. */
    public static Optional<AABB> aabb() {
        if (cornerA == null || cornerB == null) {
            return Optional.empty();
        }
        var minX = Math.min(cornerA.getX(), cornerB.getX());
        var minY = Math.min(cornerA.getY(), cornerB.getY());
        var minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        var maxX = Math.max(cornerA.getX(), cornerB.getX()) + 1;
        var maxY = Math.max(cornerA.getY(), cornerB.getY()) + 1;
        var maxZ = Math.max(cornerA.getZ(), cornerB.getZ()) + 1;
        return Optional.of(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static List<String> captures() {
        return captures;
    }

    public static void setCaptures(List<String> newCaptures) {
        captures = newCaptures == null ? List.of() : List.copyOf(newCaptures);
    }

    public static void setPendingCaptureResult(S2CProjectOpResultPayload result) {
        pendingCaptureResult = result;
    }

    /** Returns the latest CAPTURE op result and clears it. Returns null if no result has arrived since last poll. */
    public static @Nullable S2CProjectOpResultPayload consumePendingCaptureResult() {
        var r = pendingCaptureResult;
        pendingCaptureResult = null;
        return r;
    }

    public static @Nullable BlockPos moveOffset() {
        return moveOffset;
    }

    public static void setMoveOffset(@Nullable BlockPos offset) {
        moveOffset = offset;
    }

    public static boolean moveCopyMode() {
        return moveCopyMode;
    }

    public static void setMoveCopyMode(boolean copy) {
        moveCopyMode = copy;
    }

    public static void setPendingMoveResult(S2CMoveSelectionResultPayload result) {
        pendingMoveResult = result;
    }

    public static @Nullable S2CMoveSelectionResultPayload consumePendingMoveResult() {
        var r = pendingMoveResult;
        pendingMoveResult = null;
        return r;
    }

    public static void clear() {
        cornerA = null;
        cornerB = null;
        picking = PickingState.NONE;
        mode = CaptureMode.GENERAL;
        gizmoMode = GizmoMode.SCALE_VOLUME;
        captures = List.of();
        pendingCaptureResult = null;
        moveOffset = null;
        moveCopyMode = false;
        pendingMoveResult = null;
    }

    /**
     * Drop the spatial state that lets a volume render and own input — corners, the in-progress corner pick, and any
     * live Move Blocks gesture. Used when a non-volume target (entity, jigsaw, generic block) becomes the active
     * selection: keeping the corners around lets the wireframe, gizmos, and RMB-on-AABB context menu coexist with the
     * single-thing inspector, which contradicts "one selection at a time". User-preference fields ({@link #mode},
     * {@link #gizmoMode}) and project-level caches ({@link #captures}, {@link #pendingCaptureResult}) survive so a
     * subsequent re-marquee picks up where the user left off.
     */
    public static void clearVolume() {
        cornerA = null;
        cornerB = null;
        picking = PickingState.NONE;
        moveOffset = null;
        moveCopyMode = false;
        pendingMoveResult = null;
    }
}
