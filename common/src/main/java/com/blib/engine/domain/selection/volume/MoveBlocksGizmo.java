package com.blib.engine.domain.selection.volume;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.math.AxisPlaneDrag;
import com.blib.engine.math.CursorCamera;
import com.blib.engine.math.RayAabb;
import com.blib.engine.session.EngineSession;
import com.blib.engine.tool.gizmo.GizmoHit;

/**
 * Translation gizmo for the <em>blocks within</em> the selection AABB (Move Blocks tool). Visually identical to
 * {@link BlockSelectionTranslateGizmo} — three axis-colored arrows from AABB center — but instead of moving the
 * selection's corners, dragging updates {@link BlockSelection#setMoveOffset} so a ghost preview can render the future
 * position. The world is unchanged until {@link #endDrag} fires the network commit; on success the listener shifts the
 * AABB to follow the moved blocks (Photoshop's "marquee follows the dropped pixels" pattern).
 * <p>
 * Drag-begin captures Alt-state (latched, never live-sampled) so a stray Alt release mid-drag can't switch a "move"
 * into a "copy". Mirrors the MMB camera-gesture pattern.
 * <p>
 * Picking, drag-plane math, and arrow geometry are duplicated from {@link BlockSelectionTranslateGizmo} rather than
 * factored into a shared helper — only two callers, the diff is intentional (different drag effect), and the duplicate
 * keeps each class single-purpose. If a third axis-arrow gizmo lands later we can consolidate then.
 */
@ApiStatus.Internal
public final class MoveBlocksGizmo {

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

    private MoveBlocksGizmo() {}

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

    /** AABB center in world space — same anchor as the translate gizmo. */
    public static Vec3 aabbCenter(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Vec3((minX + maxX) / 2.0 + 0.5, (minY + maxY) / 2.0 + 0.5, (minZ + maxZ) / 2.0 + 0.5);
    }

    /**
     * Begin dragging {@code axis}. Captures Alt-state for copy-vs-cut semantics, resets the move offset to zero so the
     * ghost starts coincident with the source AABB, and snapshots the drag plane the same way the translate gizmo does.
     */
    public static void beginDrag(Axis axis, EngineSession session, boolean copy) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
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

        // Sample the click cursor's projection onto the axis so the first frame's delta is zero rather than
        // ARROW_LENGTH × scale. See {@code BlockSelectionTranslateGizmo} for the same fix and rationale.
        var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
        var initialAxisOffset = rayDir == null
            ? 0.0
            : AxisPlaneDrag.projectOntoAxisOrZero(camPos, rayDir, center, planeNormal, direction);

        BlockSelection.setMoveCopyMode(copy);
        BlockSelection.setMoveOffset(BlockPos.ZERO);
        drag = new DragState(axis, planeNormal, center, direction, initialAxisOffset, copy);
    }

    /**
     * Update the live offset based on the cursor ray. Snaps to integer block deltas so the ghost stays grid-aligned.
     * Doesn't touch the world or the AABB — only {@link BlockSelection#setMoveOffset}.
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
        BlockSelection.setMoveOffset(new BlockPos(dx, dy, dz));
    }

    /**
     * End the drag. Returns the captured offset + copy flag if the drag produced a non-zero delta, else {@code null}.
     * On non-null result the caller fires {@code C2SMoveSelectionPayload}; on null the caller does nothing (the user
     * dragged back to zero, equivalent to a cancel). The offset is <em>not</em> cleared here — the ghost stays visible
     * until the server's reply lands so the user has continuous visual feedback.
     */
    public static @Nullable DragResult endDrag() {
        var d = drag;
        drag = null;
        if (d == null) {
            return null;
        }
        var offset = BlockSelection.moveOffset();
        if (offset == null || (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0)) {
            BlockSelection.setMoveOffset(null);
            return null;
        }
        return new DragResult(offset, d.copyMode);
    }

    public static void clear() {
        hoveredAxis = null;
        drag = null;
    }

    public record DragResult(
        BlockPos offset,
        boolean copy
    ) {}

    public record AxisHit(
        Axis axis,
        double t
    ) implements GizmoHit {}

    public static @Nullable Axis pickUnderCursor(EngineSession session) {
        var hit = pickUnderCursorWithDistance(session);
        return hit == null ? null : hit.axis();
    }

    public static @Nullable AxisHit pickUnderCursorWithDistance(EngineSession session) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return null;
        }
        var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
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

    public static double[] arrowPickBoxRaw(Axis axis, Vec3 center, double scale) {
        var len = ARROW_LENGTH * scale;
        var perp = PICK_HALF_WIDTH * scale;
        var sx = axis.direction.x;
        var sy = axis.direction.y;
        var sz = axis.direction.z;
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
        boolean copyMode
    ) {}
}
