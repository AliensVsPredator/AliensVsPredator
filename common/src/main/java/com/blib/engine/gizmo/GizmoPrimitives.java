package com.blib.engine.gizmo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

/**
 * Stateless line-primitive draw routines for interactive gizmos. Originally lived as private statics inside
 * {@code BLibGizmoRenderer}; extracted here so every gizmo system (item-tuning, modeler, future engine gizmos) shares
 * the same visual style and segment counts.
 * <p>
 * All primitives emit into a {@link RenderType#lines()} buffer using the current {@link PoseStack} pose, so callers
 * control world / view / cube placement by transforming the pose stack before calling. Colors are passed explicitly so
 * dragging / hover states can brighten alpha without duplicating geometry code.
 */
public final class GizmoPrimitives {

    /** Number of segments used to draw each rotate ring as a line strip. */
    public static final int RING_SEGMENTS = 48;

    /** Axis-color RGB triples matching Blender / Maya convention (X red, Y green, Z blue). */
    public static final float[] AXIS_RED = { 1f, 0.2f, 0.2f };

    public static final float[] AXIS_GREEN = { 0.2f, 1f, 0.2f };

    public static final float[] AXIS_BLUE = { 0.2f, 0.4f, 1f };

    private GizmoPrimitives() {
        throw new UnsupportedOperationException();
    }

    /** Color triple for an axis index (0=X, 1=Y, 2=Z). Convenience for handle drawing loops. */
    public static float[] axisColor(int axis) {
        return switch (axis) {
            case 0 -> AXIS_RED;
            case 1 -> AXIS_GREEN;
            default -> AXIS_BLUE;
        };
    }

    /**
     * Axis-aligned arrow extending from the pose-stack origin to {@code length} units along the axis. Shaft is one line
     * and the tip has a small "+" cross perpendicular to the shaft so the user has a fat target.
     * <p>
     * {@code sign} is +1 for positive-axis (the default) or -1 for the opposite direction — used by the modeler's
     * 6-face resize handles to draw the −X / −Y / −Z handles without a per-direction code path.
     */
    public static void drawArrow(
        PoseStack poseStack,
        VertexConsumer buffer,
        float length,
        int axis,
        int sign,
        float r,
        float g,
        float b,
        float a
    ) {
        float reach = length * sign;
        float ex = axis == 0 ? reach : 0;
        float ey = axis == 1 ? reach : 0;
        float ez = axis == 2 ? reach : 0;

        var pose = poseStack.last();
        float nx = axis == 0 ? sign : 0f;
        float ny = axis == 1 ? sign : 0f;
        float nz = axis == 2 ? sign : 0f;

        // Shaft from origin to tip.
        buffer.addVertex(pose.pose(), 0, 0, 0).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose.pose(), ex, ey, ez).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);

        // Tip cross — a small "+" perpendicular to the axis at the tip, so the user has a target to click.
        float tip = reach;
        float spike = length * 0.08f;
        if (axis == 0) {
            drawLine(buffer, pose, tip, -spike, 0, tip, spike, 0, r, g, b, a, nx, ny, nz);
            drawLine(buffer, pose, tip, 0, -spike, tip, 0, spike, r, g, b, a, nx, ny, nz);
        } else if (axis == 1) {
            drawLine(buffer, pose, -spike, tip, 0, spike, tip, 0, r, g, b, a, nx, ny, nz);
            drawLine(buffer, pose, 0, tip, -spike, 0, tip, spike, r, g, b, a, nx, ny, nz);
        } else {
            drawLine(buffer, pose, -spike, 0, tip, spike, 0, tip, r, g, b, a, nx, ny, nz);
            drawLine(buffer, pose, 0, -spike, tip, 0, spike, tip, r, g, b, a, nx, ny, nz);
        }
    }

    /**
     * Positive-axis arrow — shorthand for
     * {@link #drawArrow(PoseStack, VertexConsumer, float, int, int, float, float, float, float)} with {@code sign=+1}.
     */
    public static void drawArrow(PoseStack poseStack, VertexConsumer buffer, float length, int axis, float r, float g, float b, float a) {
        drawArrow(poseStack, buffer, length, axis, 1, r, g, b, a);
    }

    /**
     * Ring around {@code axis} at {@code radius}, drawn as a {@link #RING_SEGMENTS}-segment line strip. The ring lies
     * in the plane perpendicular to {@code axis} (X ring → YZ plane, Y → XZ, Z → XY).
     */
    public static void drawRing(PoseStack poseStack, VertexConsumer buffer, float radius, int axis, float r, float g, float b, float a) {
        var pose = poseStack.last();
        float nx = axis == 0 ? 1f : 0f;
        float ny = axis == 1 ? 1f : 0f;
        float nz = axis == 2 ? 1f : 0f;

        Vector3f prev = ringPoint(axis, radius, 0);
        for (int i = 1; i <= RING_SEGMENTS; i++) {
            float t = (float) i / RING_SEGMENTS * (float) (Math.PI * 2);
            var curr = ringPoint(axis, radius, t);
            buffer.addVertex(pose.pose(), prev.x, prev.y, prev.z).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
            buffer.addVertex(pose.pose(), curr.x, curr.y, curr.z).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
            prev = curr;
        }
    }

    /** Single point on the ring of {@code axis} at angle {@code t} and radius {@code radius}. */
    public static Vector3f ringPoint(int axis, float radius, float t) {
        float c = (float) Math.cos(t) * radius;
        float s = (float) Math.sin(t) * radius;
        return switch (axis) {
            case 0 -> new Vector3f(0, c, s);
            case 1 -> new Vector3f(c, 0, s);
            default -> new Vector3f(c, s, 0);
        };
    }

    /** Twelve-edge wireframe cube centered at {@code (cx, cy, cz)} with half-extent {@code halfSize}. */
    public static void drawWireBox(
        VertexConsumer buffer,
        PoseStack.Pose pose,
        float cx,
        float cy,
        float cz,
        float halfSize,
        float r,
        float g,
        float b,
        float a,
        float nx,
        float ny,
        float nz
    ) {
        float x0 = cx - halfSize, x1 = cx + halfSize;
        float y0 = cy - halfSize, y1 = cy + halfSize;
        float z0 = cz - halfSize, z1 = cz + halfSize;

        // Bottom face.
        drawLine(buffer, pose, x0, y0, z0, x1, y0, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z0, x1, y0, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z1, x0, y0, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y0, z1, x0, y0, z0, r, g, b, a, nx, ny, nz);
        // Top face.
        drawLine(buffer, pose, x0, y1, z0, x1, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y1, z0, x1, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y1, z1, x0, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y1, z1, x0, y1, z0, r, g, b, a, nx, ny, nz);
        // Verticals.
        drawLine(buffer, pose, x0, y0, z0, x0, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z0, x1, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z1, x1, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y0, z1, x0, y1, z1, r, g, b, a, nx, ny, nz);
    }

    /** Two-vertex line segment with color + normal. */
    public static void drawLine(
        VertexConsumer buffer,
        PoseStack.Pose pose,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        float r,
        float g,
        float b,
        float a,
        float nx,
        float ny,
        float nz
    ) {
        buffer.addVertex(pose.pose(), x1, y1, z1).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose.pose(), x2, y2, z2).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
    }
}
