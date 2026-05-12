package com.blib.engine.gizmo;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.blib.api.client.render.v1.BLibTransform;

/**
 * Picking and drag math for the interactive transform gizmo. Stateless — drag state lives on {@link BLibGizmoState},
 * this class just walks it forward in response to mouse events. All math (projection, distance, deltas) lives in
 * {@link GizmoMath} so the modeler / future gizmo systems share the same routines.
 * <p>
 * All gizmo handles are tested in screen space against the cursor: handle world (view) positions are projected through
 * the snapshot's projection matrix, then compared to the cursor in window pixels. This avoids the need for a 3D
 * ray-vs-geometry test and works regardless of how the gizmo's axes are oriented in view space.
 */
public final class BLibGizmoInput {

    /** Maximum cursor-to-arrow-handle distance, in window pixels, that counts as a hit. */
    private static final float TRANSLATE_PICK_THRESHOLD_PX = 16f;

    /**
     * Maximum cursor-to-rotate-ring distance. Larger than the translate threshold because rings render as thin
     * (1-pixel) line strips and are visually harder to land on than the translate arrows, which have the shaft + a "+"
     * tip that gives a fatter target. Tune up if rings still feel finicky to click; tune down if accidental ring picks
     * during translate-axis clicks become an issue (translate handle picks still take priority since they're tested
     * first when in TRANSLATE mode).
     */
    private static final float ROTATE_PICK_THRESHOLD_PX = 24f;

    /**
     * Maximum cursor-to-scale-handle distance. Same as TRANSLATE because the scale handle is rendered as a shaft with a
     * tip cube, giving a similarly fat target.
     */
    private static final float SCALE_PICK_THRESHOLD_PX = 16f;

    /**
     * Number of segments used to test cursor-to-ring distance. Higher = better picking accuracy at click time but more
     * work per click. Bumped from 32 to 96 — rings projected to screen often render as tilted ellipses, where a sparse
     * sampling can leave gaps between segments large enough that the cursor falls between samples and picking misses.
     */
    private static final int RING_PICK_SEGMENTS = 96;

    /**
     * Toggleable trace logging for diagnosing why a click isn't landing on a handle. Off by default — flip via
     * {@link #setTraceEnabled} from a debug command. Routes to slf4j so the chat doesn't get spammed, inspect via
     * {@code logs/latest.log}.
     */
    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile boolean traceEnabled = false;

    private BLibGizmoInput() {
        throw new UnsupportedOperationException();
    }

    public static void setTraceEnabled(boolean enabled) {
        traceEnabled = enabled;
    }

    public static boolean isTraceEnabled() {
        return traceEnabled;
    }

    /**
     * Try to start a drag at the given cursor position. Returns true if a handle was hit and a drag has been started —
     * the caller (typically a mouse-event mixin) should cancel the underlying event so it doesn't propagate to the chat
     * screen or world. Returns false when no handle is under the cursor or the gizmo isn't currently captured for a
     * target.
     */
    public static boolean tryStartDrag(double cursorX, double cursorY) {
        if (BLibGizmoState.mode() == BLibGizmoMode.OFF) {
            trace("tryStartDrag: gizmo mode OFF, ignoring click");
            return false;
        }

        var window = Minecraft.getInstance().getWindow();
        // getScreenWidth/Height (NOT getWidth/Height) — cursor xpos()/ypos() come from GLFW in window
        // (screen) coordinates, which on HDPI displays differ from framebuffer pixels by a factor of 2+.
        // Use the same coord space throughout so picking math compares apples to apples.
        int w = window.getScreenWidth();
        int h = window.getScreenHeight();

        // Multi-item frames: when several tunable items render in the same frame (e.g., a row of placed
        // queen-head blocks all in view), each render produces its own snapshot. The cursor is on at most
        // one of them — pick whichever snapshot's projected origin is closest to the cursor. Without this,
        // the picker uses the most-recently-rendered snapshot which might be off-screen / behind a wall /
        // not the one the user is clicking on.
        var snapshots = BLibGizmoState.recentRenders();

        if (snapshots.isEmpty()) {
            var legacy = BLibGizmoState.lastRender();
            if (legacy == null) {
                trace("tryStartDrag: no render snapshot — item not rendered this frame, picking impossible");
                return false;
            }
            snapshots = java.util.List.of(legacy);
        }

        var snapshot = pickClosestSnapshot(snapshots, cursorX, cursorY, w, h);

        if (snapshot == null) {
            trace("tryStartDrag: cursor not near any of {} rendered gizmo origins", snapshots.size());
            return false;
        }

        int axis = pickHandle(snapshot, cursorX, cursorY, w, h);

        trace(
            "tryStartDrag: cursor=({},{}) screen={}x{} item={} ctx={} pickedAxis={} (chose from {} snapshots)",
            cursorX,
            cursorY,
            w,
            h,
            snapshot.itemId(),
            snapshot.displayContext(),
            axis,
            snapshots.size()
        );

        if (axis < 0) {
            return false;
        }

        // Read the appropriate slot at drag-start: wall-fixed when the snapshot was captured during a
        // wall-block render, regular per-context override otherwise. The drag math below mirrors this on
        // the write side, so wall and floor poses stay in their own slots.
        var current = snapshot.wall()
            ? BLibItemTransformOverrides.getEffectiveWallFixed(snapshot.itemId(), snapshot.mode())
            : BLibItemTransformOverrides.getEffective(snapshot.itemId(), snapshot.mode(), snapshot.displayContext());

        BLibGizmoState.setDrag(
            new BLibGizmoState.DragState(
                axis,
                BLibGizmoState.mode(),
                snapshot.itemId(),
                snapshot.mode(),
                snapshot.displayContext(),
                current,
                cursorX,
                cursorY,
                snapshot
            )
        );

        trace("tryStartDrag: drag started on axis {} mode {}", axis, BLibGizmoState.mode());
        return true;
    }

    /**
     * End any active drag. Idempotent — safe to call when nothing is being dragged.
     */
    public static void endDrag() {
        BLibGizmoState.setDrag(null);
    }

    /**
     * Update the active drag with a fresh cursor position. No-op if no drag is in progress.
     */
    public static void updateDrag(double cursorX, double cursorY) {
        var drag = BLibGizmoState.drag();

        if (drag == null) {
            return;
        }

        var window = Minecraft.getInstance().getWindow();
        int w = window.getScreenWidth();
        int h = window.getScreenHeight();

        switch (drag.mode()) {
            case TRANSLATE -> applyTranslate(drag, cursorX, cursorY, w, h);
            case ROTATE -> applyRotate(drag, cursorX, cursorY, w, h);
            case SCALE -> applyScale(drag, cursorX, cursorY, w, h);
            default -> {
                /* OFF — no drag math to apply. */
            }
        }
    }

    /**
     * Pick whichever snapshot in {@code snapshots} has its projected origin closest to the cursor in screen pixels,
     * within a generous threshold. The threshold is wide enough that if a snapshot's gizmo is visible on screen, clicks
     * anywhere near its rings will pick THAT snapshot (rather than picking a different snapshot whose origin happens to
     * project somewhere else off-screen).
     * <p>
     * Returns null if no snapshot's projected origin is within reach of the cursor — typically means either no gizmo is
     * on screen at the click point, or all rendered gizmos are far from where the cursor was when the click fired.
     */
    private static @org.jetbrains.annotations.Nullable BLibGizmoState.RenderSnapshot pickClosestSnapshot(
        java.util.List<BLibGizmoState.RenderSnapshot> snapshots,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        // Preview snapshots take priority over world snapshots when both match. The whole point of the
        // preview is that the user is interacting with it instead of the (off-screen-edge) world gizmo;
        // if any preview is in range, we MUST pick it, otherwise the click would resolve to the world
        // gizmo behind the preview and the preview's oversized handles become decorative.
        var previews = snapshots.stream().filter(BLibGizmoState.RenderSnapshot::preview).toList();
        var pool = previews.isEmpty() ? snapshots : previews;

        // Scale picks the snapshot's threshold based on its gizmo's screen size — bigger gizmos can be
        // matched from further away. A factor of 2x the gizmo's screen radius (rings are at radius =
        // gizmo scale projected; cursor anywhere within 2x that radius from origin counts).
        BLibGizmoState.RenderSnapshot best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (var s : pool) {
            var origin = GizmoMath.projectToScreen(s.viewPivot(), s.projection(), w, h);
            if (origin == null)
                continue;

            double dx = cursorX - origin.x;
            double dy = cursorY - origin.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Match radius: the gizmo's projected ring radius plus picking slack. Project (viewPivot +
            // scale * +X) to get a known reference point, distance from origin estimates the ring's
            // screen-pixel radius. Threshold is 2× that radius, so cursors near or just-outside any
            // ring still associate with the right snapshot.
            var tipView = new Vector3f(s.viewPivot()).fma(s.scale(), s.viewX());
            var tip = GizmoMath.projectToScreen(tipView, s.projection(), w, h);
            float radiusPx = tip != null ? (float) Math.hypot(tip.x - origin.x, tip.y - origin.y) : 64f;
            float matchRadius = Math.max(64f, radiusPx * 2f);

            if (dist < matchRadius && dist < bestDist) {
                bestDist = dist;
                best = s;
            }
        }

        return best;
    }

    private static int pickHandle(BLibGizmoState.RenderSnapshot s, double cursorX, double cursorY, int w, int h) {
        var origin = GizmoMath.projectToScreen(s.viewPivot(), s.projection(), w, h);

        if (origin == null) {
            trace("pickHandle: gizmo origin behind camera or w<=0 — viewPivot={}", s.viewPivot());
            return -1;
        }

        trace(
            "pickHandle: origin screen=({},{}) cursor=({},{}) -> distance to origin = {} px",
            origin.x,
            origin.y,
            cursorX,
            cursorY,
            Math.hypot(cursorX - origin.x, cursorY - origin.y)
        );

        var mode = BLibGizmoState.mode();

        // SCALE has only one handle (the +Y line/cube), so the loop-over-axes approach doesn't apply —
        // handle it as a special case. Returning axis=0 is just a placeholder; the drag math reads the
        // single Y-axis direction directly off the snapshot.
        if (mode == BLibGizmoMode.SCALE) {
            float dist = GizmoMath.distanceToAxisHandle(s.geometry(), 1, 1, cursorX, cursorY, w, h);
            int picked = dist < SCALE_PICK_THRESHOLD_PX ? 0 : -1;
            trace("pickHandle: SCALE handle distance = {} px threshold={} -> picked = {}", dist, SCALE_PICK_THRESHOLD_PX, picked);
            return picked;
        }

        float threshold = switch (mode) {
            case TRANSLATE -> TRANSLATE_PICK_THRESHOLD_PX;
            case ROTATE -> ROTATE_PICK_THRESHOLD_PX;
            default -> 0f;
        };

        int best = -1;
        float bestDist = threshold;
        float[] perAxis = new float[3];

        for (int axis = 0; axis < 3; axis++) {
            float dist = switch (mode) {
                case TRANSLATE -> GizmoMath.distanceToAxisHandle(s.geometry(), axis, 1, cursorX, cursorY, w, h);
                case ROTATE -> GizmoMath.distanceToRotateHandle(s.geometry(), axis, cursorX, cursorY, w, h, RING_PICK_SEGMENTS);
                default -> Float.POSITIVE_INFINITY;
            };

            perAxis[axis] = dist;

            if (dist < bestDist) {
                bestDist = dist;
                best = axis;
            }
        }

        trace(
            "pickHandle: per-axis distances X={}, Y={}, Z={} threshold={} -> best axis = {}",
            perAxis[0],
            perAxis[1],
            perAxis[2],
            threshold,
            best
        );

        return best;
    }

    private static void trace(String fmt, Object... args) {
        if (BLibGizmoState.mode() == BLibGizmoMode.OFF && !traceEnabled) {
            return;
        }

        LOGGER.info("[BLibGizmo] " + fmt, args);

        // Chat surfacing is gated on the explicit trace toggle: chat messages while a drag is in flight
        // can physically obscure the gizmo, breaking interaction. Default behavior is silent-to-chat,
        // log-file only.
        if (!traceEnabled) {
            return;
        }

        var mc = Minecraft.getInstance();

        if (mc.player != null) {
            var formatted = MessageFormatter.arrayFormat(fmt, args).getMessage();
            mc.player.sendSystemMessage(Component.literal("[gizmo] " + formatted));
        }
    }

    private static void applyTranslate(BLibGizmoState.DragState drag, double cursorX, double cursorY, int w, int h) {
        var s = drag.startSnapshot();
        float worldDelta = GizmoMath.axisDelta(
            s.geometry(),
            drag.axis(),
            1,
            drag.startCursorX(),
            drag.startCursorY(),
            cursorX,
            cursorY,
            w,
            h
        );

        if (worldDelta == 0f) {
            return;
        }

        var startTrans = drag.startTransform().translation();
        var newTrans = new Vector3f(startTrans);

        switch (drag.axis()) {
            case 0 -> newTrans.x += worldDelta;
            case 1 -> newTrans.y += worldDelta;
            default -> newTrans.z += worldDelta;
        }

        writeOverride(
            drag,
            new BLibTransform(newTrans, drag.startTransform().rotation(), drag.startTransform().scale(), drag.startTransform().pivot())
        );
    }

    private static void applyRotate(BLibGizmoState.DragState drag, double cursorX, double cursorY, int w, int h) {
        var s = drag.startSnapshot();
        float deltaDegrees = GizmoMath.rotateDegreesDelta(
            s.geometry(),
            drag.axis(),
            drag.startCursorX(),
            drag.startCursorY(),
            cursorX,
            cursorY,
            w,
            h
        );

        if (deltaDegrees == 0f) {
            return;
        }

        var startRot = drag.startTransform().rotation();
        var newRot = new Vector3f(startRot);

        switch (drag.axis()) {
            case 0 -> newRot.x += deltaDegrees;
            case 1 -> newRot.y += deltaDegrees;
            default -> newRot.z += deltaDegrees;
        }

        writeOverride(
            drag,
            new BLibTransform(drag.startTransform().translation(), newRot, drag.startTransform().scale(), drag.startTransform().pivot())
        );
    }

    /**
     * Single-handle uniform-scale drag. Same projection-onto-axis math as {@link #applyTranslate}, but the axis is
     * hard-coded to viewY (matching {@link BLibGizmoRenderer}'s +Y line) and the result is an additive uniform delta on
     * all three scale components rather than a per-axis translation delta. Sensitivity: drag the full handle length
     * along the line direction → +1.0 scale.
     */
    private static void applyScale(BLibGizmoState.DragState drag, double cursorX, double cursorY, int w, int h) {
        var s = drag.startSnapshot();
        float delta = GizmoMath.scaleUniformDelta(s.geometry(), 1, drag.startCursorX(), drag.startCursorY(), cursorX, cursorY, w, h);

        if (delta == 0f) {
            return;
        }

        var startScale = drag.startTransform().scale();
        var newScale = new Vector3f(startScale.x + delta, startScale.y + delta, startScale.z + delta);

        writeOverride(
            drag,
            new BLibTransform(
                drag.startTransform().translation(),
                drag.startTransform().rotation(),
                newScale,
                drag.startTransform().pivot()
            )
        );
    }

    /**
     * Routes the drag's updated transform into the right override slot. Wall-block snapshots write to the wall-fixed
     * slot (no display context — wall-fixed isn't context-keyed); everything else writes to the regular per-context
     * override map.
     */
    private static void writeOverride(BLibGizmoState.DragState drag, BLibTransform updated) {
        if (drag.startSnapshot().wall()) {
            BLibItemTransformOverrides.setWallFixed(drag.itemId(), drag.transformMode(), updated);
        } else {
            BLibItemTransformOverrides.set(drag.itemId(), drag.transformMode(), drag.displayContext(), updated);
        }
    }
}
