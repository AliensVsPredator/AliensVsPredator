package com.blib.api.client.render.v1.item;

import com.blib.api.client.render.v1.BLibTransform;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Picking and drag math for the interactive transform gizmo. Stateless — drag state lives on
 * {@link BLibGizmoState}, this class just walks it forward in response to mouse events.
 * <p>
 * All gizmo handles are tested in screen space against the cursor: handle world (view) positions are
 * projected through the snapshot's projection matrix, then compared to the cursor in window pixels. This
 * avoids the need for a 3D ray-vs-geometry test and works regardless of how the gizmo's axes are oriented
 * in view space.
 * <p>
 * Drag deltas:
 * <ul>
 *   <li>TRANSLATE: cursor's pixel motion is projected onto the screen-space direction of the dragged
 *       axis. The world-space delta is recovered from the ratio of "world-length the axis arrow
 *       represents" to "screen pixels the arrow projects to."</li>
 *   <li>ROTATE: cursor's screen-space angle around the gizmo origin is tracked from drag-start; the angle
 *       delta becomes the rotation around the chosen axis. Sign is flipped when the axis points toward
 *       the viewer so that a CW cursor sweep matches the conventional "positive rotation rotates +X
 *       toward +Y around +Z" right-hand rule.</li>
 * </ul>
 */
public final class BLibGizmoInput {

    /** Maximum cursor-to-arrow-handle distance, in window pixels, that counts as a hit. */
    private static final float TRANSLATE_PICK_THRESHOLD_PX = 16f;

    /**
     * Maximum cursor-to-rotate-ring distance. Larger than the translate threshold because rings render as
     * thin (1-pixel) line strips and are visually harder to land on than the translate arrows, which have
     * the shaft + a "+" tip that gives a fatter target. Tune up if rings still feel finicky to click; tune
     * down if accidental ring picks during translate-axis clicks become an issue (translate handle picks
     * still take priority since they're tested first when in TRANSLATE mode).
     */
    private static final float ROTATE_PICK_THRESHOLD_PX = 24f;

    /**
     * Number of segments used to test cursor-to-ring distance. Higher = better picking accuracy at click
     * time but more work per click. Bumped from 32 to 96 — rings projected to screen often render as
     * tilted ellipses, where a sparse sampling can leave gaps between segments large enough that the
     * cursor falls between samples and picking misses.
     */
    private static final int RING_PICK_SEGMENTS = 96;

    /**
     * Toggleable trace logging for diagnosing why a click isn't landing on a handle. Off by default — flip
     * via {@link #setTraceEnabled} from a debug command. Routes to slf4j so the chat doesn't get spammed,
     * inspect via {@code logs/latest.log}.
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
     * Try to start a drag at the given cursor position. Returns true if a handle was hit and a drag has
     * been started — the caller (typically a mouse-event mixin) should cancel the underlying event so it
     * doesn't propagate to the chat screen or world. Returns false when no handle is under the cursor or
     * the gizmo isn't currently captured for a target.
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

        trace("tryStartDrag: cursor=({},{}) screen={}x{} item={} ctx={} pickedAxis={} (chose from {} snapshots)",
            cursorX, cursorY, w, h, snapshot.itemId(), snapshot.displayContext(), axis, snapshots.size());

        if (axis < 0) {
            return false;
        }

        // Read the appropriate slot at drag-start: wall-fixed when the snapshot was captured during a
        // wall-block render, regular per-context override otherwise. The drag math below mirrors this on
        // the write side, so wall and floor poses stay in their own slots.
        var current = snapshot.wall()
            ? BLibItemTransformOverrides.getEffectiveWallFixed(snapshot.itemId(), snapshot.mode())
            : BLibItemTransformOverrides.getEffective(snapshot.itemId(), snapshot.mode(), snapshot.displayContext());

        BLibGizmoState.setDrag(new BLibGizmoState.DragState(
            axis,
            BLibGizmoState.mode(),
            snapshot.itemId(),
            snapshot.mode(),
            snapshot.displayContext(),
            current,
            cursorX, cursorY,
            snapshot
        ));

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
        // getScreenWidth/Height (NOT getWidth/Height) — cursor xpos()/ypos() come from GLFW in window
        // (screen) coordinates, which on HDPI displays differ from framebuffer pixels by a factor of 2+.
        // Use the same coord space throughout so picking math compares apples to apples.
        int w = window.getScreenWidth();
        int h = window.getScreenHeight();

        switch (drag.mode()) {
            case TRANSLATE -> applyTranslate(drag, cursorX, cursorY, w, h);
            case ROTATE -> applyRotate(drag, cursorX, cursorY, w, h);
            default -> {
                /* OFF — no drag math to apply. */
            }
        }
    }

    /**
     * Pick whichever snapshot in {@code snapshots} has its projected origin closest to the cursor in
     * screen pixels, within a generous threshold. The threshold is wide enough that if a snapshot's
     * gizmo is visible on screen, clicks anywhere near its rings will pick THAT snapshot (rather than
     * picking a different snapshot whose origin happens to project somewhere else off-screen).
     * <p>
     * Returns null if no snapshot's projected origin is within reach of the cursor — typically means
     * either no gizmo is on screen at the click point, or all rendered gizmos are far from where the
     * cursor was when the click fired.
     */
    private static @org.jetbrains.annotations.Nullable BLibGizmoState.RenderSnapshot pickClosestSnapshot(
        java.util.List<BLibGizmoState.RenderSnapshot> snapshots, double cursorX, double cursorY, int w, int h
    ) {
        // Scale picks the snapshot's threshold based on its gizmo's screen size — bigger gizmos can be
        // matched from further away. A factor of 2x the gizmo's screen radius (rings are at radius =
        // gizmo scale projected; cursor anywhere within 2x that radius from origin counts).
        BLibGizmoState.RenderSnapshot best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (var s : snapshots) {
            var origin = projectToScreen(s.viewPivot(), s.projection(), w, h);
            if (origin == null) continue;

            double dx = cursorX - origin.x;
            double dy = cursorY - origin.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Match radius: the gizmo's projected ring radius plus picking slack. Project (viewPivot +
            // scale * +X) to get a known reference point, distance from origin estimates the ring's
            // screen-pixel radius. Threshold is 2× that radius, so cursors near or just-outside any
            // ring still associate with the right snapshot.
            var tipView = new org.joml.Vector3f(s.viewPivot()).fma(s.scale(), s.viewX());
            var tip = projectToScreen(tipView, s.projection(), w, h);
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
        var origin = projectToScreen(s.viewPivot(), s.projection(), w, h);

        if (origin == null) {
            trace("pickHandle: gizmo origin behind camera or w<=0 — viewPivot={}", s.viewPivot());
            return -1;
        }

        trace("pickHandle: origin screen=({},{}) cursor=({},{}) -> distance to origin = {} px",
            origin.x, origin.y, cursorX, cursorY,
            Math.hypot(cursorX - origin.x, cursorY - origin.y));

        boolean isTranslate = BLibGizmoState.mode() == BLibGizmoMode.TRANSLATE;
        float threshold = isTranslate ? TRANSLATE_PICK_THRESHOLD_PX : ROTATE_PICK_THRESHOLD_PX;

        int best = -1;
        float bestDist = threshold;
        float[] perAxis = new float[3];

        for (int axis = 0; axis < 3; axis++) {
            float dist = isTranslate
                ? distanceToTranslateHandle(s, axis, origin, cursorX, cursorY, w, h)
                : distanceToRotateHandle(s, axis, cursorX, cursorY, w, h);

            perAxis[axis] = dist;

            if (dist < bestDist) {
                bestDist = dist;
                best = axis;
            }
        }

        trace("pickHandle: per-axis distances X={}, Y={}, Z={} threshold={} -> best axis = {}",
            perAxis[0], perAxis[1], perAxis[2], threshold, best);
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

    private static Vector2f projectToScreen(Vector3f viewPos, Matrix4f projection, int w, int h) {
        var clip = new Vector4f(viewPos.x, viewPos.y, viewPos.z, 1f);
        projection.transform(clip);

        // Only reject when clip.w is too close to zero to divide. Don't sign-restrict — MC's
        // perspective + modelview composition can put visible content at clip.w of either sign depending
        // on convention quirks, and the picking samples and rendered handles go through the same matrices
        // either way, so the perspective divide produces consistent screen coords for both regardless of
        // the sign. (An earlier `clip.w <= 0.001` check was clipping out perfectly-visible gizmos in
        // third-person hand renders where the gizmo origin's view.z came out positive.)
        if (Math.abs(clip.w) < 1e-6f) {
            return null;
        }

        return new Vector2f(
            (clip.x / clip.w + 1f) * 0.5f * w,
            (1f - clip.y / clip.w) * 0.5f * h
        );
    }

    private static Vector3f axisVec(BLibGizmoState.RenderSnapshot s, int axis) {
        return switch (axis) {
            case 0 -> s.viewX();
            case 1 -> s.viewY();
            default -> s.viewZ();
        };
    }

    private static float distanceToTranslateHandle(
        BLibGizmoState.RenderSnapshot s, int axis, Vector2f origin,
        double cursorX, double cursorY, int w, int h
    ) {
        var tipView = new Vector3f(s.viewPivot()).fma(s.scale(), axisVec(s, axis));
        var tip = projectToScreen(tipView, s.projection(), w, h);

        if (tip == null) {
            return Float.POSITIVE_INFINITY;
        }

        return distancePointToSegment((float) cursorX, (float) cursorY, origin, tip);
    }

    /**
     * Test cursor distance to a ring by sampling N points around the ring, projecting each to screen, and
     * walking the resulting polyline to find the nearest segment. Cheap enough — RING_PICK_SEGMENTS=32 is
     * 32 projections + 32 point-to-segment distance tests, dominated by the matrix multiply, which runs
     * once per click.
     */
    private static float distanceToRotateHandle(
        BLibGizmoState.RenderSnapshot s, int axis,
        double cursorX, double cursorY, int w, int h
    ) {
        float best = Float.POSITIVE_INFINITY;
        Vector2f prev = null;

        for (int i = 0; i <= RING_PICK_SEGMENTS; i++) {
            float t = (float) i / RING_PICK_SEGMENTS * (float) (Math.PI * 2);
            var local = ringLocal(axis, s.scale(), t);
            var viewPt = new Vector3f(s.viewPivot())
                .fma(local.x, s.viewX())
                .fma(local.y, s.viewY())
                .fma(local.z, s.viewZ());
            var screen = projectToScreen(viewPt, s.projection(), w, h);

            if (screen == null) {
                prev = null;
                continue;
            }

            if (prev != null) {
                float d = distancePointToSegment((float) cursorX, (float) cursorY, prev, screen);
                if (d < best) best = d;
            }

            prev = screen;
        }

        return best;
    }

    private static Vector3f ringLocal(int axis, float radius, float t) {
        var c = (float) Math.cos(t) * radius;
        var s = (float) Math.sin(t) * radius;
        return switch (axis) {
            case 0 -> new Vector3f(0, c, s);
            case 1 -> new Vector3f(c, 0, s);
            default -> new Vector3f(c, s, 0);
        };
    }

    private static float distancePointToSegment(float px, float py, Vector2f a, Vector2f b) {
        float abx = b.x - a.x;
        float aby = b.y - a.y;
        float apx = px - a.x;
        float apy = py - a.y;
        float ab2 = abx * abx + aby * aby;

        if (ab2 < 1e-6f) {
            return (float) Math.hypot(apx, apy);
        }

        float t = Math.clamp((apx * abx + apy * aby) / ab2, 0f, 1f);
        float cx = a.x + t * abx;
        float cy = a.y + t * aby;
        return (float) Math.hypot(px - cx, py - cy);
    }

    private static void applyTranslate(BLibGizmoState.DragState drag, double cursorX, double cursorY, int w, int h) {
        var s = drag.startSnapshot();
        var origin = projectToScreen(s.viewPivot(), s.projection(), w, h);

        if (origin == null) {
            return;
        }

        var axisView = axisVec(s, drag.axis());
        var tipView = new Vector3f(s.viewPivot()).fma(s.scale(), axisView);
        var tip = projectToScreen(tipView, s.projection(), w, h);

        if (tip == null) {
            return;
        }

        // The screen-projected axis: vector from origin to tip in screen pixels. Drag motion projected onto
        // this gives signed pixel distance along the axis. The arrow is `s.scale()` world units long and
        // takes |axisScreen| pixels on screen, so world-units-per-pixel along the axis = scale / |axisScreen|.
        float axisScreenX = tip.x - origin.x;
        float axisScreenY = tip.y - origin.y;
        float axisScreenLen2 = axisScreenX * axisScreenX + axisScreenY * axisScreenY;

        if (axisScreenLen2 < 1f) {
            // Arrow projects to less than 1 pixel — axis is nearly parallel to view direction. Drag would
            // need ~infinite-pixel sensitivity to read; bail rather than divide by tiny.
            return;
        }

        float axisScreenLen = (float) Math.sqrt(axisScreenLen2);
        float dx = (float) (cursorX - drag.startCursorX());
        float dy = (float) (cursorY - drag.startCursorY());
        float pixelsAlongAxis = (dx * axisScreenX + dy * axisScreenY) / axisScreenLen;
        float worldDelta = pixelsAlongAxis * s.scale() / axisScreenLen;

        var startTrans = drag.startTransform().translation();
        var newTrans = new Vector3f(startTrans);

        switch (drag.axis()) {
            case 0 -> newTrans.x += worldDelta;
            case 1 -> newTrans.y += worldDelta;
            default -> newTrans.z += worldDelta;
        }

        writeOverride(drag, new BLibTransform(newTrans, drag.startTransform().rotation(), drag.startTransform().scale(), drag.startTransform().pivot()));
    }

    private static void applyRotate(BLibGizmoState.DragState drag, double cursorX, double cursorY, int w, int h) {
        var s = drag.startSnapshot();
        var origin = projectToScreen(s.viewPivot(), s.projection(), w, h);

        if (origin == null) {
            return;
        }

        // Tangent-projection drag math. The naive `atan2(now) - atan2(start)` approach feels jerky because
        // a fixed pixel motion produces wildly different angular changes depending on which side of the
        // gizmo the cursor is on (radial motion contributes 0; tangential motion contributes proportional
        // to 1/radius). Instead we lock the tangent direction at drag-start and project all subsequent
        // cursor motion onto that one direction — so 1 pixel of cursor motion along the tangent ALWAYS
        // produces the same angular change, regardless of where on the ring the click landed.
        double startDx = drag.startCursorX() - origin.x;
        double startDy = drag.startCursorY() - origin.y;
        double startRadius = Math.hypot(startDx, startDy);

        if (startRadius < 1) {
            // Click was effectively at the gizmo center — there's no well-defined tangent direction. Skip
            // this drag step rather than producing junk values.
            return;
        }

        // Tangent at drag-start, normalized: 90° CCW rotation of the radial direction.
        double tangentX = -startDy / startRadius;
        double tangentY = startDx / startRadius;

        // Drag delta in pixels from drag-start cursor to current cursor.
        double dx = cursorX - drag.startCursorX();
        double dy = cursorY - drag.startCursorY();

        // Pixel motion along tangent direction (signed). Equivalent to arc length on the start-radius ring.
        double arcPx = dx * tangentX + dy * tangentY;

        // arc / radius = angle in radians. This is the small-angle approximation; for typical tuner drags
        // (a few dozen pixels) the error is negligible. For full-circle sweeps the user would need to
        // release and re-click, which matches Blender's rotation gizmo feel.
        double deltaRad = arcPx / startRadius;

        // Right-hand rule sign: when the rotation axis points toward the viewer (axisView.z > 0 in OpenGL
        // right-handed view space), positive axis rotation maps to visual CCW, which our screen-CCW-positive
        // tangent computes as positive. Flip when the axis points away so a CW cursor sweep still reads as
        // positive rotation around an axis pointing into the screen.
        var axisView = axisVec(s, drag.axis());
        float sign = axisView.z > 0 ? -1f : 1f;
        float deltaDegrees = (float) Math.toDegrees(deltaRad) * sign;

        var startRot = drag.startTransform().rotation();
        var newRot = new Vector3f(startRot);

        switch (drag.axis()) {
            case 0 -> newRot.x += deltaDegrees;
            case 1 -> newRot.y += deltaDegrees;
            default -> newRot.z += deltaDegrees;
        }

        writeOverride(drag, new BLibTransform(drag.startTransform().translation(), newRot, drag.startTransform().scale(), drag.startTransform().pivot()));
    }

    /**
     * Routes the drag's updated transform into the right override slot. Wall-block snapshots write to
     * the wall-fixed slot (no display context — wall-fixed isn't context-keyed); everything else writes
     * to the regular per-context override map.
     */
    private static void writeOverride(BLibGizmoState.DragState drag, BLibTransform updated) {
        if (drag.startSnapshot().wall()) {
            BLibItemTransformOverrides.setWallFixed(drag.itemId(), drag.transformMode(), updated);
        } else {
            BLibItemTransformOverrides.set(drag.itemId(), drag.transformMode(), drag.displayContext(), updated);
        }
    }
}
