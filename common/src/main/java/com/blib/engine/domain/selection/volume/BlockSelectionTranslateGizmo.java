package com.blib.engine.domain.selection.volume;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.math.AxisPlaneDrag;
import com.blib.engine.math.CursorCamera;
import com.blib.engine.math.RayAabb;
import com.blib.engine.session.EngineSession;

/**
 * Axis-aligned translation gizmo for the capture AABB. Three colored arrows (X red, Y green, Z blue) extend from the
 * AABB center along each positive axis. Dragging an arrow translates the entire volume along that axis without resizing
 * — both corners shift by the same delta. Rendered + picked using the same constant-screen-size scaling as
 * {@link BlockSelectionScaleGizmo} so the arrows stay legible at any camera distance.
 * <p>
 * Drag math mirrors the scale gizmo: capture an axis-containing plane whose normal is most camera-aligned, then each
 * frame ray-plane-intersect the cursor ray and project the hit onto the dragged axis. The delta becomes a per-axis
 * world translation applied to both corners. Snap-to-int aligns translations to the block grid.
 * <p>
 * Conceptually independent of the scale gizmo (different axis types, different drag semantics), but they share the
 * camera-distance scaling formula via {@link BlockSelectionScaleGizmo#scaleForCamera}.
 */
@ApiStatus.Internal
public final class BlockSelectionTranslateGizmo {

    /** Length of each arrow's shaft from AABB center to tip, in world units at the reference distance. */
    public static final double ARROW_LENGTH = 2.0;

    /** Half-thickness of the arrow shaft (it's rendered as a thin rectangular prism). */
    public static final double SHAFT_HALF_WIDTH = 0.06;

    /** Half-extent of the cube at the arrow tip — the visual "knob" the user grabs. */
    public static final double TIP_HALF_WIDTH = 0.18;

    /** Pickable arrow length includes shaft + tip; this is the half-extent perpendicular to the axis for picking. */
    public static final double PICK_HALF_WIDTH = TIP_HALF_WIDTH;

    public enum Axis {

        X(Direction.Axis.X, new Vec3(1, 0, 0), new float[] { 0.95f, 0.30f, 0.30f }),
        Y(Direction.Axis.Y, new Vec3(0, 1, 0), new float[] { 0.30f, 0.95f, 0.30f }),
        Z(Direction.Axis.Z, new Vec3(0, 0, 1), new float[] { 0.30f, 0.55f, 0.95f });

        private final Direction.Axis vanillaAxis;

        private final Vec3 direction;

        private final float[] color;

        Axis(Direction.Axis vanillaAxis, Vec3 direction, float[] color) {
            this.vanillaAxis = vanillaAxis;
            this.direction = direction;
            this.color = color;
        }

        public Direction.Axis vanillaAxis() {
            return vanillaAxis;
        }

        public Vec3 direction() {
            return direction;
        }

        public float[] color() {
            return color;
        }
    }

    private static @Nullable Axis hoveredAxis;

    private static @Nullable DragState drag;

    private BlockSelectionTranslateGizmo() {}

    public static @Nullable Axis hoveredAxis() {
        return hoveredAxis;
    }

    public static void setHoveredAxis(@Nullable Axis axis) {
        hoveredAxis = axis;
    }

    public static @Nullable Axis draggingAxis() {
        return drag == null ? null : drag.axis;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    /** AABB center in world space (for arrow origin). */
    public static Vec3 aabbCenter(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Vec3((minX + maxX) / 2.0 + 0.5, (minY + maxY) / 2.0 + 0.5, (minZ + maxZ) / 2.0 + 0.5);
    }

    /**
     * Begin dragging {@code axis}. Snapshots both corners so the translation can be applied as an offset relative to
     * the start state. Drag plane is chosen to be axis-containing and most camera-perpendicular, mirroring the scale
     * gizmo's drag-plane setup.
     */
    public static void beginDrag(Axis axis, EngineSession session) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var cornerAOrig = BlockSelection.cornerA();
        var cornerBOrig = BlockSelection.cornerB();
        if (cornerAOrig == null || cornerBOrig == null) {
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var center = aabbCenter(minX, minY, minZ, maxX, maxY, maxZ);
        var camPos = CursorCamera.position(session);
        var direction = axis.direction();
        var planeNormal = AxisPlaneDrag.pickPlaneNormal(direction, center, camPos);

        // Sample the click cursor's projection onto the axis at drag start, so the first frame's delta is exactly
        // zero rather than the ARROW_LENGTH offset of the arrow tip the user grabbed. Without this, every drag would
        // immediately snap the AABB by ~round(ARROW_LENGTH × scale) blocks before the user has moved the cursor.
        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        var initialAxisOffset = rayDir == null
            ? 0.0
            : AxisPlaneDrag.projectOntoAxisOrZero(camPos, rayDir, center, planeNormal, direction);

        drag = new DragState(axis, planeNormal, center, direction, initialAxisOffset, cornerAOrig, cornerBOrig);
    }

    /**
     * Update the active drag based on the cursor ray. Computes the hit on the drag plane, projects onto the dragged
     * axis, snaps to int, and translates both corners by that delta from their captured start positions.
     */
    public static void updateDrag(EngineSession session, Vec3 cursorRayDir) {
        var d = drag;
        if (d == null) {
            return;
        }
        var camPos = CursorCamera.position(session);
        var projected = AxisPlaneDrag.projectOntoAxis(camPos, cursorRayDir, d.planePoint, d.planeNormal, d.axisDir);
        if (Double.isNaN(projected)) {
            return;
        }
        var deltaAlongAxis = (int) Math.round(projected - d.initialAxisOffset);

        var dx = (int) (d.axisDir.x * deltaAlongAxis);
        var dy = (int) (d.axisDir.y * deltaAlongAxis);
        var dz = (int) (d.axisDir.z * deltaAlongAxis);
        // Apply the offset to the *original* corners and assign directly so the user's A/B labels stay attached to
        // the same physical corners. Using setBounds here would componentwise-min/max-reorder the corners, swapping
        // labels every frame the AABB's existing layout doesn't already match componentwise min/max — visible as
        // diagonal-mirror flicker on sub-block drags.
        BlockSelection.setCornersDirect(d.cornerAOrig.offset(dx, dy, dz), d.cornerBOrig.offset(dx, dy, dz));
    }

    public static void endDrag() {
        drag = null;
    }

    public static void clear() {
        hoveredAxis = null;
        drag = null;
    }

    /**
     * Pick the closest arrow under the cursor. Each arrow is treated as a thin world-space AABB stretching from the
     * AABB center along its axis (length scaled by camera distance to keep on-screen size constant); ray-AABB picks the
     * closest. Returns null if no arrow is hit or the AABB is incomplete.
     */
       /** Pick result that pairs the hit axis with the ray-distance, so callers can compare against other gizmos. */
    public record AxisHit(
        Axis axis,
        double t
    ) implements com.blib.engine.tool.gizmo.GizmoHit {}

    public static @Nullable Axis pickUnderCursor(EngineSession session) {
        var hit = pickUnderCursorWithDistance(session);
        return hit == null ? null : hit.axis();
    }

    public static @Nullable AxisHit pickUnderCursorWithDistance(EngineSession session) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return null;
        }
        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return null;
        }
        var origin = CursorCamera.position(session);

        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var center = aabbCenter(minX, minY, minZ, maxX, maxY, maxZ);
        var scale = BlockSelectionScaleGizmo.scaleForCamera(origin, center);

        Axis closest = null;
        var closestT = Double.POSITIVE_INFINITY;
        for (var axis : Axis.values()) {
            var b = arrowPickBoxRaw(axis, center, scale);
            var t = RayAabb.intersect(
                origin.x,
                origin.y,
                origin.z,
                rayDir.x,
                rayDir.y,
                rayDir.z,
                b[0],
                b[1],
                b[2],
                b[3],
                b[4],
                b[5]
            );
            if (t > 0 && t < closestT) {
                closestT = t;
                closest = axis;
            }
        }
        return closest == null ? null : new AxisHit(closest, closestT);
    }

    /**
     * Pickable AABB for one arrow. Spans the full arrow (shaft + tip) along its axis; perpendicular thickness matches
     * the tip cube so the user can grab anywhere along the arrow's visible footprint.
     */
    public static double[] arrowPickBoxRaw(Axis axis, Vec3 center, double scale) {
        var len = ARROW_LENGTH * scale;
        var perp = PICK_HALF_WIDTH * scale;
        var sx = axis.direction.x;
        var sy = axis.direction.y;
        var sz = axis.direction.z;
        // Arrow extends from center to center + axis*len. Min/max include perpendicular thickness on the other axes.
        var endX = center.x + sx * len;
        var endY = center.y + sy * len;
        var endZ = center.z + sz * len;
        var minX = Math.min(center.x, endX) - (sx == 0 ? perp : 0);
        var minY = Math.min(center.y, endY) - (sy == 0 ? perp : 0);
        var minZ = Math.min(center.z, endZ) - (sz == 0 ? perp : 0);
        var maxX = Math.max(center.x, endX) + (sx == 0 ? perp : 0);
        var maxY = Math.max(center.y, endY) + (sy == 0 ? perp : 0);
        var maxZ = Math.max(center.z, endZ) + (sz == 0 ? perp : 0);
        return new double[] { minX, minY, minZ, maxX, maxY, maxZ };
    }

    private record DragState(
        Axis axis,
        Vec3 planeNormal,
        Vec3 planePoint,
        Vec3 axisDir,
        double initialAxisOffset,
        BlockPos cornerAOrig,
        BlockPos cornerBOrig
    ) {}
}
