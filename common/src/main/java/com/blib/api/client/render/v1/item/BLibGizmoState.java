package com.blib.api.client.render.v1.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Process-wide state for the interactive transform-tuning gizmo. Holds:
 * <ul>
 *   <li>The active {@link BLibGizmoMode} (off / translate / rotate).</li>
 *   <li>The {@code (item, mode, displayContext)} triple the gizmo is currently editing — captured the last
 *       time the targeted item was rendered, so a single LMB-press can resolve which override slot to
 *       update without the command needing explicit args.</li>
 *   <li>Per-frame render snapshot — the world-space pivot position and the pose-stack matrix at gizmo
 *       render time. The mouse-input handler reads these to project handle positions to screen for picking
 *       and to convert mouse drags into world-space deltas.</li>
 *   <li>The active drag, if any — which axis is being dragged, the override value at drag start, and the
 *       cursor's drag-start ground-truth (so deltas read off cumulative motion rather than per-frame
 *       jitter).</li>
 * </ul>
 * All fields are {@code volatile} or write-on-render-thread / read-on-input-thread; the design relies on
 * Minecraft's render and input both running on the client thread, so per-frame writes don't race with
 * per-frame reads.
 */
public final class BLibGizmoState {

    /** Active gizmo mode. {@code OFF} means render nothing and ignore clicks. */
    private static volatile BLibGizmoMode mode = BLibGizmoMode.OFF;

    /**
     * Snapshot of the most recent render of a tunable item that matched the gizmo target (if any). Reset to
     * null between frames; written from the render thread inside {@link BLibGeoBoneItemRenderer}'s pre-render
     * hook, read from the mouse-input thread for picking.
     */
    @Nullable
    private static volatile RenderSnapshot lastRender = null;

    /** Active drag state, or null when no axis is currently grabbed. */
    @Nullable
    private static volatile DragState drag = null;

    private BLibGizmoState() {
        throw new UnsupportedOperationException();
    }

    public static BLibGizmoMode mode() {
        return mode;
    }

    public static void setMode(BLibGizmoMode newMode) {
        mode = newMode;

        // Cancel any in-flight drag when the user toggles modes — the drag state is mode-specific (axis
        // direction interpretation differs between translate and rotate), and continuing it across a mode
        // change would apply the wrong delta.
        if (newMode == BLibGizmoMode.OFF) {
            drag = null;
        }
    }

    public static @Nullable RenderSnapshot lastRender() {
        return lastRender;
    }

    public static void setLastRender(@Nullable RenderSnapshot snapshot) {
        lastRender = snapshot;
    }

    public static @Nullable DragState drag() {
        return drag;
    }

    public static void setDrag(@Nullable DragState newDrag) {
        drag = newDrag;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    /**
     * Per-frame snapshot of where the gizmo was drawn. Vectors are in view space (camera transform already
     * applied — Minecraft puts the camera onto the pose stack at the start of the frame, so when our
     * renderer reads pose-stack matrix it's already a local-to-view transform). View space is enough for
     * picking: project view positions to clip via the captured projection matrix, then to screen for
     * cursor-distance comparisons.
     *
     * @param itemId           Item being tuned (the held tunable item that was rendered this frame).
     * @param mode             Tuner mode — {@code BLOCKING} if the player was using-item this frame, else
     *                         {@code IDLE}.
     * @param displayContext   The display context this render used (e.g., {@code THIRD_PERSON_RIGHT_HAND}).
     * @param viewPivot        View-space coordinate of the gizmo origin (= where the bone pivot lands, in
     *                         pre-rotation pose frame, plus the user's tuner pivot offset).
     * @param viewX            View-space direction of the +X axis at the gizmo origin (unit vector).
     * @param viewY            View-space direction of the +Y axis at the gizmo origin (unit vector).
     * @param viewZ            View-space direction of the +Z axis at the gizmo origin (unit vector).
     * @param scale            World-space length to draw each axis arrow / ring radius. Picked so the gizmo
     *                         looks roughly the same on-screen size regardless of how far the item is from
     *                         the camera (using camera distance as a hint).
     * @param projection       The projection matrix in effect when the gizmo was drawn — captured here
     *                         instead of read live so that input handlers running while a different
     *                         projection is bound (e.g., GUI projection while chat is open) project
     *                         against the matrix the handles were rendered through.
     * @param wall             True when this snapshot was captured during a wall-block render (the
     *                         {@code RENDER_AS_WALL_BLOCK} flag was on). Drag input writes to the
     *                         wall-fixed override slot instead of the regular {@code FIXED} slot when
     *                         this is true, so wall and floor poses tune independently.
     */
    public record RenderSnapshot(
        ResourceLocation itemId,
        BLibItemTransformMode mode,
        ItemDisplayContext displayContext,
        Vector3f viewPivot,
        Vector3f viewX,
        Vector3f viewY,
        Vector3f viewZ,
        float scale,
        Matrix4f projection,
        boolean wall
    ) {}

    /**
     * Active drag — populated on LMB press over a handle, cleared on LMB release. Holds enough to compute
     * the per-frame delta against drag-start without integrating per-frame drift.
     *
     * @param axis             Which axis is being dragged (0=X, 1=Y, 2=Z).
     * @param mode             Snapshot of the gizmo mode at drag-start (TRANSLATE or ROTATE — never OFF).
     * @param itemId           Item being edited.
     * @param transformMode    IDLE or BLOCKING; the drag updates this slot in the override map.
     * @param displayContext   Display context being edited.
     * @param startTransform   The full transform value at drag-start, used as the base for delta application
     *                         so cumulative numerical drift can't compound.
     * @param startCursorX     Cursor X (in window pixels) at drag-start.
     * @param startCursorY     Cursor Y (in window pixels) at drag-start.
     * @param startSnapshot    The render snapshot at drag-start. Drag math reads view-space pivot, axis
     *                         directions, and projection from here so a per-frame change to where the
     *                         gizmo would render this frame doesn't perturb the drag.
     */
    public record DragState(
        int axis,
        BLibGizmoMode mode,
        ResourceLocation itemId,
        BLibItemTransformMode transformMode,
        ItemDisplayContext displayContext,
        com.blib.api.client.render.v1.BLibTransform startTransform,
        double startCursorX,
        double startCursorY,
        RenderSnapshot startSnapshot
    ) {}
}
