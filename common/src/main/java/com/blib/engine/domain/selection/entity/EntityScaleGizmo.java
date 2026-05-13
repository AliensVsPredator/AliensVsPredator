package com.blib.engine.domain.selection.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.math.AxisPlaneDrag;
import com.blib.engine.math.CursorCamera;
import com.blib.engine.math.RayAabb;
import com.blib.engine.session.EngineSession;

/**
 * Uniform-scale gizmo for the engine's entity selection — a single Y-axis handle on top of the entity AABB. Drag up to
 * grow, drag down to shrink; the new scale is committed to {@link Attributes#SCALE} on release via
 * {@link com.blib.mod.common.network.packet.C2SSetEntityScalePayload}. Non-uniform scaling isn't possible here because
 * vanilla's SCALE attribute is a single scalar; that's intentional — it matches what the server-side Attribute API
 * actually supports.
 * <p>
 * Drag math: the handle is dragged along its world-Y axis. We project the cursor ray onto an axis-perpendicular plane,
 * read the Y delta, and convert it to a multiplicative scale change. One block of cursor travel = roughly 0.25× change
 * in scale, so a moderate vertical drag covers the full clamped range without feeling twitchy. Server clamps to
 * {@code [0.1, 4.0]}; the client clamps the same so the ghost preview matches what the user will actually get.
 */
@ApiStatus.Internal
public final class EntityScaleGizmo {

    public static final double MIN_SCALE = 0.1;

    public static final double MAX_SCALE = 4.0;

    /** World units of cursor travel per unit of scale. Tuned by feel — feels like 1:1 at moderate camera distance. */
    private static final double SCALE_PER_BLOCK = 0.25;

    private static boolean hovered;

    private static @Nullable DragState drag;

    private EntityScaleGizmo() {}

    public static boolean hovered() {
        return hovered;
    }

    public static void setHovered(boolean h) {
        hovered = h;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    public static @Nullable LivingEntity draggingEntity() {
        return drag == null ? null : drag.entity;
    }

    /** Live ghost scale — what the entity will scale to on drag commit. Returns 1.0 when no drag is active. */
    public static double ghostScale() {
        return drag == null ? 1.0 : drag.ghostScale;
    }

    /**
     * Anchor for the Y handle: top-center of the entity's AABB. Same anchor used for picking + rendering so the visible
     * arrow and the pick box never separate.
     */
    public static Vec3 handleAnchor(LivingEntity entity, double ghostScale) {
        var box = entity.getBoundingBox();
        // Apply the ghost scale visually so the handle moves up/down with the preview AABB during drag — feels more
        // direct than a static-position handle that doesn't follow the preview.
        var cx = (box.minX + box.maxX) * 0.5;
        var cz = (box.minZ + box.maxZ) * 0.5;
        var height = box.maxY - box.minY;
        var top = box.minY + height * ghostScale;
        return new Vec3(cx, top, cz);
    }

    public static void beginDrag(LivingEntity entity, EngineSession session) {
        var camPos = CursorCamera.position(session);
        var anchor = handleAnchor(entity, 1.0);
        var axisDir = new Vec3(0, 1, 0);
        var planeNormal = AxisPlaneDrag.pickPlaneNormal(axisDir, anchor, camPos);

        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        var initialAxisOffset = rayDir == null
            ? 0.0
            : AxisPlaneDrag.projectOntoAxisOrZero(camPos, rayDir, anchor, planeNormal, axisDir);

        var initialScale = readEntityScale(entity);
        drag = new DragState(entity, planeNormal, anchor, axisDir, initialAxisOffset, initialScale, initialScale);
    }

    private static double readEntityScale(LivingEntity entity) {
        var attr = entity.getAttribute(Attributes.SCALE);
        return attr == null ? 1.0 : attr.getValue();
    }

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
        var deltaY = projected - d.initialAxisOffset;
        var newScale = d.initialScale + deltaY * SCALE_PER_BLOCK;
        d.ghostScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScale));
    }

    public static @Nullable DragResult endDrag() {
        var d = drag;
        drag = null;
        if (d == null) {
            return null;
        }
        if (Math.abs(d.ghostScale - d.initialScale) < 1.0e-4) {
            return null;
        }
        var entity = d.entity;
        if (entity == null || !entity.isAlive()) {
            return null;
        }
        return new DragResult(entity, d.ghostScale);
    }

    public static void clear() {
        hovered = false;
        drag = null;
    }

    /**
     * Pick the Y handle under the cursor for {@code entity}. Returns null if the cursor doesn't hit the handle's
     * pickable AABB. Same camera-relative scaling as the block-volume gizmo so the on-screen handle size stays
     * consistent.
     */
    public static @Nullable HandleHit pickUnderCursorWithDistance(EngineSession session, LivingEntity entity) {
        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return null;
        }
        var origin = CursorCamera.position(session);
        var anchor = handleAnchor(entity, ghostScale());
        var scale = BlockSelectionScaleGizmo.scaleForCamera(origin, anchor);

        var len = BlockSelectionScaleGizmo.ARROW_LENGTH * scale;
        var perp = BlockSelectionScaleGizmo.TIP_HALF_WIDTH * scale;
        var t = RayAabb.intersect(
            origin.x,
            origin.y,
            origin.z,
            rayDir.x,
            rayDir.y,
            rayDir.z,
            anchor.x - perp,
            anchor.y,
            anchor.z - perp,
            anchor.x + perp,
            anchor.y + len + perp,
            anchor.z + perp
        );
        return Double.isNaN(t) || t <= 0 ? null : new HandleHit(t);
    }

    public record HandleHit(double t) implements com.blib.engine.tool.gizmo.GizmoHit {}

    public record DragResult(
        LivingEntity entity,
        double newScale
    ) {}

    private static final class DragState {

        final LivingEntity entity;

        final Vec3 planeNormal;

        final Vec3 planePoint;

        final Vec3 axisDir;

        final double initialAxisOffset;

        final double initialScale;

        double ghostScale;

        DragState(
            LivingEntity entity,
            Vec3 planeNormal,
            Vec3 planePoint,
            Vec3 axisDir,
            double initialAxisOffset,
            double initialScale,
            double ghostScale
        ) {
            this.entity = entity;
            this.planeNormal = planeNormal;
            this.planePoint = planePoint;
            this.axisDir = axisDir;
            this.initialAxisOffset = initialAxisOffset;
            this.initialScale = initialScale;
            this.ghostScale = ghostScale;
        }
    }
}
