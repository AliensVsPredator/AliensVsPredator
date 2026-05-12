package com.blib.engine.gizmo;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Stateless picking and drag-math helpers for gizmo systems. All math operates on a {@link GizmoGeometry} snapshot plus
 * cursor coordinates in screen pixels — no item-specific or modeler-specific state. Originally inlined in
 * {@code BLibGizmoInput}; extracted so the modeler can reuse the same projection / distance / delta routines.
 * <p>
 * Coordinate convention: cursor coordinates and the {@code w / h} dimensions are in the SAME screen-space (whether
 * window pixels for the world gizmo or panel-relative pixels for the modeler is the caller's choice). Projection
 * results are returned in that same space, so as long as the caller is consistent the picking math is unit-agnostic.
 */
public final class GizmoMath {

    private GizmoMath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Project a view-space point through {@code projection} to screen-space pixel coordinates. Returns null when the
     * point is too close to the camera plane to perform a stable perspective divide ({@code |clip.w| < 1e-6}).
     */
    public static @Nullable Vector2f projectToScreen(Vector3f viewPos, Matrix4f projection, int w, int h) {
        var clip = new Vector4f(viewPos.x, viewPos.y, viewPos.z, 1f);
        projection.transform(clip);

        // Only reject when clip.w is too close to zero to divide. Don't sign-restrict — perspective + modelview
        // composition can put visible content at clip.w of either sign depending on convention quirks, and the
        // picking samples and rendered handles go through the same matrices either way.
        if (Math.abs(clip.w) < 1e-6f) {
            return null;
        }

        return new Vector2f(
            (clip.x / clip.w + 1f) * 0.5f * w,
            (1f - clip.y / clip.w) * 0.5f * h
        );
    }

    /** Distance from point {@code (px, py)} to the segment {@code a → b}, clamped at the endpoints. */
    public static float distancePointToSegment(float px, float py, Vector2f a, Vector2f b) {
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

    /**
     * Distance from cursor to a positive- or negative-axis arrow handle anchored at the gizmo pivot. {@code sign} is +1
     * for the positive-axis handle (X / Y / Z arrows), -1 for the negative-axis handle. Returns
     * {@link Float#POSITIVE_INFINITY} when the handle's endpoint or origin can't be projected.
     */
    public static float distanceToAxisHandle(GizmoGeometry geom, int axis, int sign, double cursorX, double cursorY, int w, int h) {
        return distanceToOffsetAxisHandle(geom, 0, 0, 0, axis, sign, cursorX, cursorY, w, h);
    }

    /**
     * Distance from cursor to an axis-handle arrow anchored at a local-pose offset from the gizmo pivot. Used by the
     * modeler's six face-resize handles, which sit on the cube's face centers (offset from the cube pivot) rather than
     * the pivot itself. {@code (offsetX, offsetY, offsetZ)} is in the same pose-stack-local units as the geometry's
     * axis vectors — i.e. cube-local pixels for the modeler.
     */
    public static float distanceToOffsetAxisHandle(
        GizmoGeometry geom,
        float offsetX,
        float offsetY,
        float offsetZ,
        int axis,
        int sign,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        var baseView = localToView(geom, offsetX, offsetY, offsetZ);
        var base = projectToScreen(baseView, geom.projection(), w, h);
        if (base == null) {
            return Float.POSITIVE_INFINITY;
        }

        var axisView = geom.axis(axis);
        var tipView = new Vector3f(baseView).fma(geom.scale() * sign, axisView);
        var tip = projectToScreen(tipView, geom.projection(), w, h);
        if (tip == null) {
            return Float.POSITIVE_INFINITY;
        }

        return distancePointToSegment((float) cursorX, (float) cursorY, base, tip);
    }

    /**
     * Transform a local-pose offset to view space using the geometry's captured basis vectors. Local units are
     * pose-stack-local (e.g. cube-local pixels for the modeler).
     */
    public static Vector3f localToView(GizmoGeometry geom, float offsetX, float offsetY, float offsetZ) {
        return new Vector3f(geom.viewPivot())
            .fma(offsetX, geom.viewX())
            .fma(offsetY, geom.viewY())
            .fma(offsetZ, geom.viewZ());
    }

    /**
     * Distance from cursor to a rotate-ring handle. Samples {@code segments} points around the ring, projects each to
     * screen, and walks the resulting polyline for the nearest segment. More segments improve picking accuracy on
     * highly-tilted rings (where adjacent samples can be many pixels apart) at a linear cost.
     */
    public static float distanceToRotateHandle(GizmoGeometry geom, int axis, double cursorX, double cursorY, int w, int h, int segments) {
        float best = Float.POSITIVE_INFINITY;
        Vector2f prev = null;

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments * (float) (Math.PI * 2);
            var local = GizmoPrimitives.ringPoint(axis, geom.scale(), t);
            var viewPt = new Vector3f(geom.viewPivot())
                .fma(local.x, geom.viewX())
                .fma(local.y, geom.viewY())
                .fma(local.z, geom.viewZ());
            var screen = projectToScreen(viewPt, geom.projection(), w, h);

            if (screen == null) {
                prev = null;
                continue;
            }

            if (prev != null) {
                float d = distancePointToSegment((float) cursorX, (float) cursorY, prev, screen);
                if (d < best) {
                    best = d;
                }
            }

            prev = screen;
        }

        return best;
    }

    /**
     * Compute the world-units delta along the {@code (axis × sign)} direction from the drag-start cursor to the current
     * cursor, for a handle anchored at the gizmo pivot. The math projects pixel cursor motion onto the screen-projected
     * axis vector, then converts pixels-along-axis to world units using {@code scale / |axisScreen|}.
     * <p>
     * Returns 0 when the axis projects to fewer than 1 pixel on screen (handle is nearly parallel to view direction and
     * the world-units-per-pixel conversion would blow up).
     */
    public static float axisDelta(
        GizmoGeometry startGeom,
        int axis,
        int sign,
        double startCursorX,
        double startCursorY,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        return offsetAxisDelta(startGeom, 0, 0, 0, axis, sign, startCursorX, startCursorY, cursorX, cursorY, w, h);
    }

    /**
     * Same as {@link #axisDelta} but for a handle anchored at a local-pose offset from the pivot. The axis-screen
     * direction is computed from the offset base — which matters for perspective foreshortening on handles that are
     * deep into the scene (modeler's RESIZE face handles on a rotated cube).
     */
    public static float offsetAxisDelta(
        GizmoGeometry startGeom,
        float offsetX,
        float offsetY,
        float offsetZ,
        int axis,
        int sign,
        double startCursorX,
        double startCursorY,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        var baseView = localToView(startGeom, offsetX, offsetY, offsetZ);
        var origin = projectToScreen(baseView, startGeom.projection(), w, h);
        if (origin == null) {
            return 0f;
        }

        var axisView = startGeom.axis(axis);
        var tipView = new Vector3f(baseView).fma(startGeom.scale() * sign, axisView);
        var tip = projectToScreen(tipView, startGeom.projection(), w, h);
        if (tip == null) {
            return 0f;
        }

        float axisScreenX = tip.x - origin.x;
        float axisScreenY = tip.y - origin.y;
        float axisScreenLen2 = axisScreenX * axisScreenX + axisScreenY * axisScreenY;

        if (axisScreenLen2 < 1f) {
            return 0f;
        }

        float axisScreenLen = (float) Math.sqrt(axisScreenLen2);
        float dx = (float) (cursorX - startCursorX);
        float dy = (float) (cursorY - startCursorY);
        float pixelsAlongAxis = (dx * axisScreenX + dy * axisScreenY) / axisScreenLen;
        // Returned value is along the (axis × sign) direction — caller multiplies by axis × sign if they need the
        // signed axis-space delta. sign = +1 returns positive-axis delta; sign = -1 returns delta along negative
        // axis (i.e. dragging "outward" from a MIN face).
        return pixelsAlongAxis * startGeom.scale() / axisScreenLen;
    }

    /**
     * Dimensionless scale delta for a single-axis uniform-scale handle (BLib item gizmo). Same projection math as
     * {@link #axisDelta} but doesn't multiply by {@code scale} on the way out — useful when the underlying value is
     * itself a scale multiplier where pixels-along-axis / handle-length-in-pixels reads naturally as the delta.
     */
    public static float scaleUniformDelta(
        GizmoGeometry startGeom,
        int axis,
        double startCursorX,
        double startCursorY,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        var origin = projectToScreen(startGeom.viewPivot(), startGeom.projection(), w, h);
        if (origin == null) {
            return 0f;
        }

        var axisView = startGeom.axis(axis);
        var tipView = new Vector3f(startGeom.viewPivot()).fma(startGeom.scale(), axisView);
        var tip = projectToScreen(tipView, startGeom.projection(), w, h);
        if (tip == null) {
            return 0f;
        }

        float axisScreenX = tip.x - origin.x;
        float axisScreenY = tip.y - origin.y;
        float axisScreenLen2 = axisScreenX * axisScreenX + axisScreenY * axisScreenY;

        if (axisScreenLen2 < 1f) {
            return 0f;
        }

        float axisScreenLen = (float) Math.sqrt(axisScreenLen2);
        float dx = (float) (cursorX - startCursorX);
        float dy = (float) (cursorY - startCursorY);
        float pixelsAlongAxis = (dx * axisScreenX + dy * axisScreenY) / axisScreenLen;
        return pixelsAlongAxis / axisScreenLen;
    }

    /**
     * Rotation delta in degrees around {@code axis}. Tangent-projection method: lock the tangent direction at
     * drag-start, project all subsequent cursor motion onto it, and convert arc-length to angle via
     * {@code arc / radius}. Sign-flips when the rotation axis points toward the viewer so a CW cursor sweep produces a
     * positive rotation around a +Z-into-screen axis (right-hand rule).
     * <p>
     * Returns 0 when the click landed effectively at the gizmo origin (no well-defined tangent direction).
     */
    public static float rotateDegreesDelta(
        GizmoGeometry startGeom,
        int axis,
        double startCursorX,
        double startCursorY,
        double cursorX,
        double cursorY,
        int w,
        int h
    ) {
        var origin = projectToScreen(startGeom.viewPivot(), startGeom.projection(), w, h);
        if (origin == null) {
            return 0f;
        }

        double startDx = startCursorX - origin.x;
        double startDy = startCursorY - origin.y;
        double startRadius = Math.hypot(startDx, startDy);

        if (startRadius < 1) {
            return 0f;
        }

        double tangentX = -startDy / startRadius;
        double tangentY = startDx / startRadius;

        double dx = cursorX - startCursorX;
        double dy = cursorY - startCursorY;
        double arcPx = dx * tangentX + dy * tangentY;
        double deltaRad = arcPx / startRadius;

        // Right-hand rule: flip when the axis points toward the viewer (axisView.z > 0 in OpenGL right-handed view
        // space) so a CW cursor sweep produces a positive rotation around an axis pointing into the screen.
        var axisView = startGeom.axis(axis);
        float sign = axisView.z > 0 ? -1f : 1f;
        return (float) Math.toDegrees(deltaRad) * sign;
    }
}
