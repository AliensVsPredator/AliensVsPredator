package com.blib.engine.gizmo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;

/**
 * Process-wide state for the interactive transform-tuning gizmo. Holds:
 * <ul>
 * <li>The active {@link BLibGizmoMode} (off / translate / rotate).</li>
 * <li>The {@code (item, mode, displayContext)} triple the gizmo is currently editing — captured the last time the
 * targeted item was rendered, so a single LMB-press can resolve which override slot to update without the command
 * needing explicit args.</li>
 * <li>Per-frame render snapshot — the world-space pivot position and the pose-stack matrix at gizmo render time. The
 * mouse-input handler reads these to project handle positions to screen for picking and to convert mouse drags into
 * world-space deltas.</li>
 * <li>The active drag, if any — which axis is being dragged, the override value at drag start, and the cursor's
 * drag-start ground-truth (so deltas read off cumulative motion rather than per-frame jitter).</li>
 * </ul>
 * All fields are {@code volatile} or write-on-render-thread / read-on-input-thread; the design relies on Minecraft's
 * render and input both running on the client thread, so per-frame writes don't race with per-frame reads.
 */
public final class BLibGizmoState {

    /** Active gizmo mode. {@code OFF} means render nothing and ignore clicks. */
    private static volatile BLibGizmoMode mode = BLibGizmoMode.OFF;

    /**
     * Snapshot of the most recent render of a tunable item that matched the gizmo target (if any). Reset to null
     * between frames; written from the render thread inside {@link BLibGeoBoneItemRenderer}'s pre-render hook, read
     * from the mouse-input thread for picking.
     */
    @Nullable
    private static volatile RenderSnapshot lastRender = null;

    /**
     * All snapshots captured in the most recent frame's gizmo renders. When multiple tunable items are rendered in the
     * same frame (e.g., a row of placed queen-head blocks all in view), each render adds to this list. At click time
     * the input handler picks whichever entry has its projected origin nearest the cursor — that's the gizmo the user
     * is actually trying to click on. Entries older than {@link #SNAPSHOT_MAX_AGE_NANOS} are dropped on add, so the
     * list naturally trims to "this frame's captures" without needing an explicit per-frame clear hook.
     */
    private static final List<TimedSnapshot> recentRenders = new CopyOnWriteArrayList<>();

    /**
     * Snapshots older than this on add are dropped. Roughly two frames at 60fps — long enough to span a frame's worth
     * of multi-item captures, short enough that clicks always pick from the most recent rendering, not stale data from
     * when the camera was elsewhere.
     */
    private static final long SNAPSHOT_MAX_AGE_NANOS = 33_000_000L;

    /** Active drag state, or null when no axis is currently grabbed. */
    @Nullable
    private static volatile DragState drag = null;

    /**
     * True while the HUD-corner preview render is in flight. Read by {@link BLibGeoBoneItemRenderer}'s pre-render hook
     * to suppress the user's translation component (so the preview stays anchored to its corner regardless of how much
     * the user has translated the actual first-person item) and by {@link BLibGizmoRenderer} to boost the gizmo's
     * screen size against the preview's larger pose-stack scale. Volatile because the flag is set/cleared on the render
     * thread but read from input handlers that may run on another thread; in practice both are the client thread, but
     * the cost of volatile is negligible compared to the cost of a torn read.
     */
    private static volatile boolean PREVIEW_RENDER = false;

    /**
     * Scale multiplier applied to the gizmo's handle size at render time. Defaults to 1× for normal world renders; the
     * preview render sets it to a larger value so the gizmos visually dominate the preview (the whole point of the
     * preview is that the actual gizmos are too small to interact with at the normal world scale at the screen edges).
     * Reset to 1× as soon as the preview render finishes.
     */
    private static volatile float HANDLE_SCALE_MULTIPLIER = 1f;

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

        if (snapshot != null) {
            // Trim stale entries (older than two frames at 60fps) and append the new one. Picker reads
            // from this list rather than `lastRender` so multi-item frames pick the right gizmo.
            long now = System.nanoTime();
            recentRenders.removeIf(t -> now - t.timestamp() > SNAPSHOT_MAX_AGE_NANOS);
            recentRenders.add(new TimedSnapshot(snapshot, now));
        }
    }

    /**
     * All snapshots captured in the last ~two frames. Picker iterates these to find whichever gizmo the cursor is
     * closest to — handles the case where multiple tunable items are visible at once and the "most-recently-rendered"
     * one isn't the one the user is clicking on.
     */
    public static List<RenderSnapshot> recentRenders() {
        long now = System.nanoTime();
        var result = new ArrayList<RenderSnapshot>();

        for (var t : recentRenders) {
            if (now - t.timestamp() <= SNAPSHOT_MAX_AGE_NANOS) {
                result.add(t.snapshot());
            }
        }

        return result;
    }

    private record TimedSnapshot(
        RenderSnapshot snapshot,
        long timestamp
    ) {}

    public static @Nullable DragState drag() {
        return drag;
    }

    public static void setDrag(@Nullable DragState newDrag) {
        drag = newDrag;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    public static boolean isPreviewRender() {
        return PREVIEW_RENDER;
    }

    public static void setPreviewRender(boolean enabled) {
        PREVIEW_RENDER = enabled;
    }

    public static float handleScaleMultiplier() {
        return HANDLE_SCALE_MULTIPLIER;
    }

    public static void setHandleScaleMultiplier(float multiplier) {
        HANDLE_SCALE_MULTIPLIER = multiplier;
    }

    /**
     * Per-frame snapshot of where the gizmo was drawn. Composes a shared {@link GizmoGeometry} (view-space pivot + axis
     * directions + scale + projection) with item-specific context (id, transform mode, display context, wall/preview
     * flags). Delegate accessors keep call sites that read {@code viewPivot()} / {@code projection()} unchanged.
     *
     * @param itemId         Item being tuned (the held tunable item that was rendered this frame).
     * @param mode           Tuner mode — {@code BLOCKING} if the player was using-item this frame, else {@code IDLE}.
     * @param displayContext The display context this render used (e.g., {@code THIRD_PERSON_RIGHT_HAND}).
     * @param geometry       Shared geometric snapshot — viewPivot, axis directions, scale, projection.
     * @param wall           True when this snapshot was captured during a wall-block render (the
     *                       {@code RENDER_AS_WALL_BLOCK} flag was on). Drag input writes to the wall-fixed override
     *                       slot instead of the regular {@code FIXED} slot when this is true, so wall and floor poses
     *                       tune independently.
     * @param preview        True when this snapshot was captured by the HUD-corner preview render. The picker prefers
     *                       preview snapshots over world snapshots when both are present, since the preview is the
     *                       surface the user is actually clicking on.
     */
    public record RenderSnapshot(
        ResourceLocation itemId,
        BLibItemTransformMode mode,
        ItemDisplayContext displayContext,
        GizmoGeometry geometry,
        boolean wall,
        boolean preview
    ) {

        public Vector3f viewPivot() {
            return geometry.viewPivot();
        }

        public Vector3f viewX() {
            return geometry.viewX();
        }

        public Vector3f viewY() {
            return geometry.viewY();
        }

        public Vector3f viewZ() {
            return geometry.viewZ();
        }

        public float scale() {
            return geometry.scale();
        }

        public Matrix4f projection() {
            return geometry.projection();
        }
    }

    /**
     * Active drag — populated on LMB press over a handle, cleared on LMB release. Holds enough to compute the per-frame
     * delta against drag-start without integrating per-frame drift.
     *
     * @param axis           Which axis is being dragged (0=X, 1=Y, 2=Z).
     * @param mode           Snapshot of the gizmo mode at drag-start (TRANSLATE or ROTATE — never OFF).
     * @param itemId         Item being edited.
     * @param transformMode  IDLE or BLOCKING; the drag updates this slot in the override map.
     * @param displayContext Display context being edited.
     * @param startTransform The full transform value at drag-start, used as the base for delta application so
     *                       cumulative numerical drift can't compound.
     * @param startCursorX   Cursor X (in window pixels) at drag-start.
     * @param startCursorY   Cursor Y (in window pixels) at drag-start.
     * @param startSnapshot  The render snapshot at drag-start. Drag math reads view-space pivot, axis directions, and
     *                       projection from here so a per-frame change to where the gizmo would render this frame
     *                       doesn't perturb the drag.
     */
    public record DragState(
        int axis,
        BLibGizmoMode mode,
        ResourceLocation itemId,
        BLibItemTransformMode transformMode,
        ItemDisplayContext displayContext,
        BLibTransform startTransform,
        double startCursorX,
        double startCursorY,
        RenderSnapshot startSnapshot
    ) {}
}
