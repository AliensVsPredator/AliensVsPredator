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
     * and the tip is a square-base pyramid pointing outward — reads as an arrow from any viewing angle.
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

        // Tip-pyramid geometry: apex at full reach, base square inset along the axis by tipLen. Proportions live on
        // TIP_LEN_RATIO / TIP_HALF_W_RATIO so the outlined-pyramid and filled-pyramid paths stay visually identical.
        float tipLen = length * TIP_LEN_RATIO;
        float tipHalfW = length * TIP_HALF_W_RATIO;
        float baseAlong = reach - tipLen * sign;
        float bx = axis == 0 ? baseAlong : 0;
        float by = axis == 1 ? baseAlong : 0;
        float bz = axis == 2 ? baseAlong : 0;

        // Base-square corner offsets relative to the base center, in the plane perpendicular to the axis. Cyclic
        // order so consecutive entries form a base-square edge.
        float[][] cornerOffsets;
        if (axis == 0) {
            cornerOffsets = new float[][] {
                { 0, +tipHalfW, +tipHalfW },
                { 0, +tipHalfW, -tipHalfW },
                { 0, -tipHalfW, -tipHalfW },
                { 0, -tipHalfW, +tipHalfW }
            };
        } else if (axis == 1) {
            cornerOffsets = new float[][] {
                { +tipHalfW, 0, +tipHalfW },
                { +tipHalfW, 0, -tipHalfW },
                { -tipHalfW, 0, -tipHalfW },
                { -tipHalfW, 0, +tipHalfW }
            };
        } else {
            cornerOffsets = new float[][] {
                { +tipHalfW, +tipHalfW, 0 },
                { +tipHalfW, -tipHalfW, 0 },
                { -tipHalfW, -tipHalfW, 0 },
                { -tipHalfW, +tipHalfW, 0 }
            };
        }

        // Shaft from origin to the base of the tip pyramid.
        drawLine(buffer, pose, 0, 0, 0, bx, by, bz, r, g, b, a, nx, ny, nz);

        // For each base corner: one line to the apex (pyramid silhouette) + one base-square edge to the next corner.
        // 4 + 4 = 8 lines per arrowhead.
        for (int i = 0; i < 4; i++) {
            var c1 = cornerOffsets[i];
            drawLine(buffer, pose, bx + c1[0], by + c1[1], bz + c1[2], ex, ey, ez, r, g, b, a, nx, ny, nz);
            var c2 = cornerOffsets[(i + 1) % 4];
            drawLine(
                buffer,
                pose,
                bx + c1[0],
                by + c1[1],
                bz + c1[2],
                bx + c2[0],
                by + c2[1],
                bz + c2[2],
                r,
                g,
                b,
                a,
                nx,
                ny,
                nz
            );
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
     * Just the shaft — line from the pose-stack origin to the base of where {@link #drawArrowTipFilled} will place its
     * pyramid. Use with a {@code LINES}-topology buffer. Pair with {@link #drawArrowTipFilled} for a filled-arrow look
     * (two render passes: lines for the shaft, quads for the tip).
     */
    public static void drawArrowShaft(
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
        float baseAlong = reach - length * TIP_LEN_RATIO * sign;
        float bx = axis == 0 ? baseAlong : 0;
        float by = axis == 1 ? baseAlong : 0;
        float bz = axis == 2 ? baseAlong : 0;

        var pose = poseStack.last();
        float nx = axis == 0 ? sign : 0f;
        float ny = axis == 1 ? sign : 0f;
        float nz = axis == 2 ? sign : 0f;

        drawLine(buffer, pose, 0, 0, 0, bx, by, bz, r, g, b, a, nx, ny, nz);
    }

    /**
     * Positive-axis shaft — shorthand for
     * {@link #drawArrowShaft(PoseStack, VertexConsumer, float, int, int, float, float, float, float)} with
     * {@code sign=+1}.
     */
    public static void drawArrowShaft(
        PoseStack poseStack,
        VertexConsumer buffer,
        float length,
        int axis,
        float r,
        float g,
        float b,
        float a
    ) {
        drawArrowShaft(poseStack, buffer, length, axis, 1, r, g, b, a);
    }

    /**
     * Filled square-base pyramid sitting at the end of an axis arrow, apex at {@code length * sign} along the axis,
     * base inset by {@link #TIP_LEN_RATIO} * length. Use with a {@code QUADS}-topology buffer in
     * {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#POSITION_COLOR}. Emits 5 quads: 4 side triangles encoded as
     * degenerate quads (apex repeated), plus the base square. Caller is responsible for shader / blend / depth state.
     */
    public static void drawArrowTipFilled(
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
        float tipLen = length * TIP_LEN_RATIO;
        float tipHalfW = length * TIP_HALF_W_RATIO;
        float baseAlong = reach - tipLen * sign;

        float ax = axis == 0 ? reach : 0;
        float ay = axis == 1 ? reach : 0;
        float az = axis == 2 ? reach : 0;
        float bx = axis == 0 ? baseAlong : 0;
        float by = axis == 1 ? baseAlong : 0;
        float bz = axis == 2 ? baseAlong : 0;

        // Base-square corner offsets relative to the base center, in the plane perpendicular to the axis. Listed in
        // cyclic order so consecutive entries (and the wrap from [3] back to [0]) form base-square edges.
        float[][] cornerOffsets;
        if (axis == 0) {
            cornerOffsets = new float[][] {
                { 0, +tipHalfW, +tipHalfW },
                { 0, +tipHalfW, -tipHalfW },
                { 0, -tipHalfW, -tipHalfW },
                { 0, -tipHalfW, +tipHalfW }
            };
        } else if (axis == 1) {
            cornerOffsets = new float[][] {
                { +tipHalfW, 0, +tipHalfW },
                { +tipHalfW, 0, -tipHalfW },
                { -tipHalfW, 0, -tipHalfW },
                { -tipHalfW, 0, +tipHalfW }
            };
        } else {
            cornerOffsets = new float[][] {
                { +tipHalfW, +tipHalfW, 0 },
                { +tipHalfW, -tipHalfW, 0 },
                { -tipHalfW, -tipHalfW, 0 },
                { -tipHalfW, +tipHalfW, 0 }
            };
        }

        var matrix = poseStack.last().pose();

        // 4 side faces — each a triangle (c[i], c[i+1], apex) encoded as a degenerate quad with the apex repeated.
        for (int i = 0; i < 4; i++) {
            var c1 = cornerOffsets[i];
            var c2 = cornerOffsets[(i + 1) % 4];
            buffer.addVertex(matrix, bx + c1[0], by + c1[1], bz + c1[2]).setColor(r, g, b, a);
            buffer.addVertex(matrix, bx + c2[0], by + c2[1], bz + c2[2]).setColor(r, g, b, a);
            buffer.addVertex(matrix, ax, ay, az).setColor(r, g, b, a);
            buffer.addVertex(matrix, ax, ay, az).setColor(r, g, b, a);
        }
        // Base quad — closes the pyramid from behind so the back face is also rendered (depth-test is typically off
        // for gizmos, so the camera can see through to it during certain rotations).
        for (var c : cornerOffsets) {
            buffer.addVertex(matrix, bx + c[0], by + c[1], bz + c[2]).setColor(r, g, b, a);
        }
    }

    /**
     * Positive-axis filled tip — shorthand for
     * {@link #drawArrowTipFilled(PoseStack, VertexConsumer, float, int, int, float, float, float, float)} with
     * {@code sign=+1}.
     */
    public static void drawArrowTipFilled(
        PoseStack poseStack,
        VertexConsumer buffer,
        float length,
        int axis,
        float r,
        float g,
        float b,
        float a
    ) {
        drawArrowTipFilled(poseStack, buffer, length, axis, 1, r, g, b, a);
    }

    /**
     * Filled box tip for scale / resize handles. Occupies the same tip zone as {@link #drawArrowTipFilled} but with
     * constant perpendicular extent end-to-end (no taper) so it reads as a grab handle rather than a directional arrow.
     * Use with a {@code QUADS}-topology buffer in {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#POSITION_COLOR}.
     * Caller is responsible for shader / blend / depth state.
     */
    public static void drawArrowTipCubeFilled(
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
        float tipLen = length * TIP_LEN_RATIO;
        float tipHalfW = length * TIP_HALF_W_RATIO;
        float baseAlong = reach - tipLen * sign;

        // Box bounding-box, axis-aligned in the pose-stack frame. min/max handle the negative-sign case automatically.
        float minX = axis == 0 ? Math.min(reach, baseAlong) : -tipHalfW;
        float maxX = axis == 0 ? Math.max(reach, baseAlong) : +tipHalfW;
        float minY = axis == 1 ? Math.min(reach, baseAlong) : -tipHalfW;
        float maxY = axis == 1 ? Math.max(reach, baseAlong) : +tipHalfW;
        float minZ = axis == 2 ? Math.min(reach, baseAlong) : -tipHalfW;
        float maxZ = axis == 2 ? Math.max(reach, baseAlong) : +tipHalfW;

        var matrix = poseStack.last().pose();
        emitBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
    }

    /**
     * Positive-axis filled cube tip — shorthand for
     * {@link #drawArrowTipCubeFilled(PoseStack, VertexConsumer, float, int, int, float, float, float, float)} with
     * {@code sign=+1}.
     */
    public static void drawArrowTipCubeFilled(
        PoseStack poseStack,
        VertexConsumer buffer,
        float length,
        int axis,
        float r,
        float g,
        float b,
        float a
    ) {
        drawArrowTipCubeFilled(poseStack, buffer, length, axis, 1, r, g, b, a);
    }

    /** Emit the six axis-aligned quad faces of an AABB into a {@code QUADS / POSITION_COLOR} buffer. */
    private static void emitBoxQuads(
        VertexConsumer buffer,
        org.joml.Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ,
        float r,
        float g,
        float b,
        float a
    ) {
        // -Z
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        // +Z
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        // -X
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        // +X
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        // -Y
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        // +Y
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
    }

    /**
     * Fraction of arrow length occupied by the tip pyramid (apex to base). Shared by {@link #drawArrow},
     * {@link #drawArrowShaft}, and {@link #drawArrowTipFilled} so the shaft + tip geometry lines up across the two-pass
     * filled-arrow path.
     */
    private static final float TIP_LEN_RATIO = 0.18f;

    /** Half-width of the tip pyramid's base square as a fraction of arrow length — see {@link #TIP_LEN_RATIO}. */
    private static final float TIP_HALF_W_RATIO = 0.05f;

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
