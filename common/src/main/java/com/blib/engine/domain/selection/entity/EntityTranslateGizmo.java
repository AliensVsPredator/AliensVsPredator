package com.blib.engine.domain.selection.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo;
import com.blib.engine.input.ActiveKeybindings;
import com.blib.engine.input.Keybindings;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.math.AxisPlaneDrag;
import com.blib.engine.math.CursorCamera;
import com.blib.engine.math.RayAabb;
import com.blib.engine.session.EngineSession;
import com.blib.engine.tool.gizmo.GizmoHit;

/**
 * Translation gizmo for the engine's entity selection — three axis arrows extending from the entity AABB center.
 * Mirrors {@link BlockSelectionTranslateGizmo}'s ray-plane drag math but commits via the ghost-then-commit pattern: the
 * <em>ghost offset</em> is updated live during drag (consumed by the renderer for a translucent preview AABB), and the
 * server is told the final position once via a {@link com.blib.mod.common.network.packet.C2STranslateEntityPayload} on
 * release. The actual entity stays at its server-side pose throughout the drag.
 * <p>
 * Translation is free-continuous (sub-block precision). Holding Shift mid-drag rounds the per-axis delta to integer
 * blocks, matching the snap-to-grid muscle memory from the block-volume gizmo (which is always integer-snapped because
 * block-volume corners are integer block positions).
 */
@ApiStatus.Internal
public final class EntityTranslateGizmo {

    private static @Nullable BlockSelectionTranslateGizmo.Axis hoveredAxis;

    private static @Nullable DragState drag;

    private EntityTranslateGizmo() {}

    public static @Nullable BlockSelectionTranslateGizmo.Axis hoveredAxis() {
        return hoveredAxis;
    }

    public static void setHoveredAxis(@Nullable BlockSelectionTranslateGizmo.Axis axis) {
        hoveredAxis = axis;
    }

    public static @Nullable BlockSelectionTranslateGizmo.Axis draggingAxis() {
        return drag == null ? null : drag.axis;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    /** Live entity being dragged, or null when no drag is in progress. The renderer reads this each frame. */
    public static @Nullable LivingEntity draggingEntity() {
        return drag == null ? null : drag.entity;
    }

    /** Live ghost offset from the entity's true position, or {@link Vec3#ZERO} when no drag is active. */
    public static Vec3 ghostOffset() {
        return drag == null ? Vec3.ZERO : drag.ghostOffset;
    }

    /**
     * AABB center of the entity in world space. Anchor for the arrow origins so the gizmo stays visually centered on
     * the entity regardless of its hitbox shape.
     */
    public static Vec3 entityCenter(LivingEntity entity, Vec3 ghostOffset) {
        var box = entity.getBoundingBox().move(ghostOffset);
        return new Vec3((box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5);
    }

    public static void beginDrag(LivingEntity entity, BlockSelectionTranslateGizmo.Axis axis, EngineSession session) {
        var center = entityCenter(entity, Vec3.ZERO);
        var camPos = CursorCamera.position(session);
        var direction = axis.direction();
        var planeNormal = AxisPlaneDrag.pickPlaneNormal(direction, center, camPos);

        var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
        var initialAxisOffset = rayDir == null
            ? 0.0
            : AxisPlaneDrag.projectOntoAxisOrZero(camPos, rayDir, center, planeNormal, direction);

        drag = new DragState(entity, axis, planeNormal, center, direction, initialAxisOffset, entity.position(), Vec3.ZERO);
    }

    /**
     * Update the ghost offset from the cursor ray. Free continuous unless Shift is held, in which case the per-axis
     * delta rounds to integer blocks.
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
        var rawDelta = projected - d.initialAxisOffset;
        var snapped = ActiveKeybindings.isModifierHeld(Keybindings.GIZMO_SNAP_INT) ? (double) Math.round(rawDelta) : rawDelta;

        d.ghostOffset = new Vec3(d.axisDir.x * snapped, d.axisDir.y * snapped, d.axisDir.z * snapped);
    }

    public static @Nullable DragResult endDrag() {
        var d = drag;
        drag = null;
        if (d == null) {
            return null;
        }
        if (d.ghostOffset.lengthSqr() < 1.0e-9) {
            // Click without drag — no-op rather than a degenerate teleport packet.
            return null;
        }
        var entity = d.entity;
        if (entity == null || !entity.isAlive()) {
            return null;
        }
        var finalPos = d.startPos.add(d.ghostOffset);
        return new DragResult(entity, finalPos.x, finalPos.y, finalPos.z);
    }

    public static void clear() {
        hoveredAxis = null;
        drag = null;
    }

    /**
     * Pick the closest arrow under the cursor for {@code entity}'s gizmo origin. Returns null if no arrow is hit.
     * Reuses {@link BlockSelectionTranslateGizmo#arrowPickBoxRaw} so the visible arrows and pick boxes stay in
     * lock-step with the block-volume implementation.
     */
    public static @Nullable AxisHit pickUnderCursorWithDistance(EngineSession session, LivingEntity entity) {
        var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return null;
        }
        var origin = CursorCamera.position(session);
        var center = entityCenter(entity, ghostOffset());
        var scale = BlockSelectionScaleGizmo.scaleForCamera(origin, center);

        BlockSelectionTranslateGizmo.Axis closest = null;
        var closestT = Double.POSITIVE_INFINITY;
        for (var axis : BlockSelectionTranslateGizmo.Axis.values()) {
            var b = BlockSelectionTranslateGizmo.arrowPickBoxRaw(axis, center, scale);
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

    public record AxisHit(
        BlockSelectionTranslateGizmo.Axis axis,
        double t
    ) implements GizmoHit {}

    public record DragResult(
        LivingEntity entity,
        double finalX,
        double finalY,
        double finalZ
    ) {}

    private static final class DragState {

        final LivingEntity entity;

        final BlockSelectionTranslateGizmo.Axis axis;

        final Vec3 planeNormal;

        final Vec3 planePoint;

        final Vec3 axisDir;

        final double initialAxisOffset;

        final Vec3 startPos;

        Vec3 ghostOffset;

        DragState(
            LivingEntity entity,
            BlockSelectionTranslateGizmo.Axis axis,
            Vec3 planeNormal,
            Vec3 planePoint,
            Vec3 axisDir,
            double initialAxisOffset,
            Vec3 startPos,
            Vec3 ghostOffset
        ) {
            this.entity = entity;
            this.axis = axis;
            this.planeNormal = planeNormal;
            this.planePoint = planePoint;
            this.axisDir = axisDir;
            this.initialAxisOffset = initialAxisOffset;
            this.startPos = startPos;
            this.ghostOffset = ghostOffset;
        }
    }
}
