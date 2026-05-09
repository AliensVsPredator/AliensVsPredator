package com.blib.engine.gizmo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;

/**
 * Draws the interactive translate/rotate gizmo handles in the world. Hooks into {@link BLibGeoBoneItemRenderer}'s
 * pre-render phase at the same anchor as the pivot debug, so the gizmo follows the held item's bone pivot through the
 * user's tuner-applied translation but stays in the pre-rotation frame so axes don't sweep around as the user adds
 * rotation.
 * <p>
 * Render-time also captures a {@link BLibGizmoState.RenderSnapshot} that the mouse-input handler reads to project
 * handles to screen space for picking and to convert mouse drags into world-space deltas. The snapshot is overwritten
 * each frame the targeted item renders, so picking always uses the freshest pose.
 */
public final class BLibGizmoRenderer {

    /** Number of segments used to draw each rotate ring as a line strip. */
    private static final int RING_SEGMENTS = 48;

    private BLibGizmoRenderer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Called from {@link BLibGeoBoneItemRenderer#applyTransforms} when the gizmo is enabled. Renders the handles for
     * the active mode and captures the per-frame snapshot for input picking.
     * <p>
     * The pose stack must be at the gizmo anchor frame: post-{@code transform.apply}'s translation and tuner pivot, but
     * BEFORE the user's rotation/scale (so axes stay aligned to the model's pre-rotation frame). The caller handles
     * push/pop around this; this method assumes it can freely concatenate transforms without leaving the stack in a
     * different state.
     */
    public static void renderAndCapture(
        AzItemRendererPipelineContext itemContext,
        BLibTransform transform,
        ResourceLocation itemId,
        BLibItemTransformMode mode
    ) {
        var gizmoMode = BLibGizmoState.mode();

        if (gizmoMode == BLibGizmoMode.OFF) {
            return;
        }

        // Skip non-tunable-world renders (GUI/hotbar/inventory icons, item-frame, ground entity). The
        // gizmo is for live-tuning the player-attached poses (hand and head), so only those contexts are
        // useful targets — and crucially, the GUI render of the same item runs LATER than the world
        // render in MC's frame, so without this filter the GUI snapshot would clobber the world snapshot
        // every frame and picking would test against the hotbar slot instead of the model in the world.
        if (!isTunableWorldContext(itemContext.getTransformType())) {
            return;
        }

        var poseStack = itemContext.poseStack();
        poseStack.pushPose();

        // Anchor at the user's tuner rotation pivot — same point the existing pivot debug uses. Combined
        // with the bone walk's post-translate, this is also where the bone's authored pivot lands in world
        // space, so the gizmo is drawn exactly on top of the model's rotation/translation anchor.
        poseStack.translate(transform.translation().x, transform.translation().y, transform.translation().z);
        poseStack.translate(transform.pivot().x, transform.pivot().y, transform.pivot().z);

        // In MC 1.21, the camera rotation is on RenderSystem's model-view stack, NOT the rendering
        // PoseStack. The PoseStack here holds local→camera-relative-world (translations only) and the
        // model-view matrix supplies the camera rotation that takes us into view space. To project handle
        // positions to screen we need the full local→view = modelview × pose, so capture both and multiply.
        var modelView = RenderSystem.getModelViewMatrix();
        var poseMat = poseStack.last().pose();
        var localToView = new Matrix4f(modelView).mul(poseMat);
        var viewPivot = new Vector3f(localToView.m30(), localToView.m31(), localToView.m32());

        // The depth-based formula assumes view-space coords (camera at origin) where viewPivot.length() ≈
        // camera distance — that's true for world rendering. In HUD / preview render the pose stack
        // operates in GUI-pixel coords, so viewPivot.length() ≈ on-screen position, not depth, and
        // multiplying it by 0.15 produces wildly oversized handles that project off-screen. Fall back to
        // a fixed pose-stack-local size for preview, leaving the on-screen size to be controlled by the
        // preview's own pose-stack scale + the handle multiplier.
        float scale;

        if (BLibGizmoState.isPreviewRender()) {
            scale = BLibGizmoState.handleScaleMultiplier();
        } else {
            var depth = viewPivot.length();
            scale = Math.max(0.15f, depth * 0.15f) * BLibGizmoState.handleScaleMultiplier();
        }

        var buffer = itemContext.multiBufferSource().getBuffer(RenderType.lines());
        var dragAxis = activeDragAxis();

        switch (gizmoMode) {
            case TRANSLATE -> drawTranslate(poseStack, buffer, scale, dragAxis);
            case ROTATE -> drawRotate(poseStack, buffer, scale, dragAxis);
            case SCALE -> drawScale(poseStack, buffer, scale, dragAxis);
            default -> {
                /* OFF — early-returned above. */
            }
        }

        // Use the same combined local-to-view matrix to compute axis directions, so the picker's projected
        // handle endpoints land where the user actually sees them. Direction vectors (w=0) drop the
        // translation column so only the rotation/scale part of the matrix applies.
        Vector3f viewX = transformDirection(localToView, 1, 0, 0);
        Vector3f viewY = transformDirection(localToView, 0, 1, 0);
        Vector3f viewZ = transformDirection(localToView, 0, 0, 1);

        BLibGizmoState.setLastRender(
            new BLibGizmoState.RenderSnapshot(
                itemId,
                mode,
                itemContext.getTransformType(),
                viewPivot,
                viewX,
                viewY,
                viewZ,
                scale,
                new Matrix4f(RenderSystem.getProjectionMatrix()),
                BLibItemTransformOverrides.isRenderAsWallBlock(),
                BLibGizmoState.isPreviewRender()
            )
        );

        poseStack.popPose();
    }

    /**
     * Clears the per-frame render snapshot. Called once per frame from a tick handler before any rendering runs, so a
     * stale snapshot from a frame where the item wasn't rendered (item swapped out of hand, inventory closed, etc.)
     * doesn't drive picking against a position that's no longer on screen.
     */
    public static void resetFrameSnapshot() {
        BLibGizmoState.setLastRender(null);
    }

    /**
     * Transform a local-space basis vector to view space, preserving the pose-stack matrix's scale. We deliberately do
     * NOT normalize the result: the rendered handles use {@code pose.pose() × local} for their vertices (so the
     * radius/length they show on screen reflects the pose's scale), and the picking code multiplies these basis vectors
     * by the gizmo's scale to recover handle positions. If we normalized, picking would compute samples at a different
     * radius from the visible ring/arrow and the cursor would have to land on invisible geometry to register as a hit —
     * directly proportional to how far the pose scale departs from 1 (e.g., a third-person hand transform with
     * {@code scale: 0.5f} would put picking samples at 2× the visible ring radius, so most clicks miss).
     */
    private static Vector3f transformDirection(Matrix4f m, float x, float y, float z) {
        var vec = new Vector4f(x, y, z, 0);
        m.transform(vec);
        return new Vector3f(vec.x, vec.y, vec.z);
    }

    private static int activeDragAxis() {
        var drag = BLibGizmoState.drag();
        return drag == null ? -1 : drag.axis();
    }

    private static boolean isTunableWorldContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || context == ItemDisplayContext.HEAD
            || context == ItemDisplayContext.FIXED;
    }

    private static void drawTranslate(PoseStack poseStack, VertexConsumer buffer, float scale, int dragAxis) {
        // X axis (red), Y axis (green), Z axis (blue). Brighten alpha when this axis is currently being
        // dragged so the user can see which handle their drag has latched onto.
        drawArrow(poseStack, buffer, scale, 0, 1f, 0.2f, 0.2f, dragAxis == 0 ? 1f : 0.85f);
        drawArrow(poseStack, buffer, scale, 1, 0.2f, 1f, 0.2f, dragAxis == 1 ? 1f : 0.85f);
        drawArrow(poseStack, buffer, scale, 2, 0.2f, 0.4f, 1f, dragAxis == 2 ? 1f : 0.85f);
    }

    private static void drawArrow(PoseStack poseStack, VertexConsumer buffer, float scale, int axis, float r, float g, float b, float a) {
        float ex = axis == 0 ? scale : 0;
        float ey = axis == 1 ? scale : 0;
        float ez = axis == 2 ? scale : 0;

        var pose = poseStack.last();
        var nx = axis == 0 ? 1f : 0f;
        var ny = axis == 1 ? 1f : 0f;
        var nz = axis == 2 ? 1f : 0f;

        // Shaft from origin to tip.
        buffer.addVertex(pose.pose(), 0, 0, 0).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose.pose(), ex, ey, ez).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);

        // Tip cross — a small "+" perpendicular to the axis at the tip, so the user has a target to click.
        var tip = scale;
        var spike = scale * 0.08f;
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

    private static void drawRotate(PoseStack poseStack, VertexConsumer buffer, float scale, int dragAxis) {
        // Rings drawn around each axis. Color matches the axis being rotated AROUND so the user thinks
        // "click the red ring → rotate around X" — same convention as Blender's rotate gizmo.
        drawRing(poseStack, buffer, scale, 0, 1f, 0.2f, 0.2f, dragAxis == 0 ? 1f : 0.75f);
        drawRing(poseStack, buffer, scale, 1, 0.2f, 1f, 0.2f, dragAxis == 1 ? 1f : 0.75f);
        drawRing(poseStack, buffer, scale, 2, 0.2f, 0.4f, 1f, dragAxis == 2 ? 1f : 0.75f);
    }

    private static void drawRing(PoseStack poseStack, VertexConsumer buffer, float radius, int axis, float r, float g, float b, float a) {
        var pose = poseStack.last();
        var nx = axis == 0 ? 1f : 0f;
        var ny = axis == 1 ? 1f : 0f;
        var nz = axis == 2 ? 1f : 0f;

        // Build vertices once, draw connecting line segments.
        Vector3f prev = ringPoint(axis, radius, 0);
        for (int i = 1; i <= RING_SEGMENTS; i++) {
            var t = (float) i / RING_SEGMENTS * (float) (Math.PI * 2);
            var curr = ringPoint(axis, radius, t);
            buffer.addVertex(pose.pose(), prev.x, prev.y, prev.z).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
            buffer.addVertex(pose.pose(), curr.x, curr.y, curr.z).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
            prev = curr;
        }
    }

    private static Vector3f ringPoint(int axis, float radius, float t) {
        var c = (float) Math.cos(t) * radius;
        var s = (float) Math.sin(t) * radius;
        return switch (axis) {
            // Ring lies in the plane perpendicular to its axis. For X ring: YZ plane. For Y ring: XZ plane.
            // For Z ring: XY plane.
            case 0 -> new Vector3f(0, c, s);
            case 1 -> new Vector3f(c, 0, s);
            default -> new Vector3f(c, s, 0);
        };
    }

    /**
     * Single white shaft going +Y with a wireframe cube at the tip. {@link com.blib.api.client.render.v1.BLibTransform}
     * stores scale as a {@code Vector3f} but the API surface (commands, dump output) treats it as a uniform scalar —
     * there's no per-axis scale workflow, so a per-axis gizmo would just be three indistinguishable handles all
     * dragging the same value. One handle is the honest design.
     * <p>
     * +Y is chosen because "drag up to scale up" reads naturally; the user's pose-stack rotation may rotate the line on
     * screen, but the screen-projected drag math handles whatever direction the line ends up pointing.
     */
    private static void drawScale(PoseStack poseStack, VertexConsumer buffer, float scale, int dragAxis) {
        var pose = poseStack.last();
        // Single handle is recorded as axis=0 in the drag state — there's nothing else to disambiguate.
        float a = dragAxis == 0 ? 1f : 0.85f;

        // Shaft.
        buffer.addVertex(pose.pose(), 0, 0, 0).setColor(1f, 1f, 1f, a).setNormal(pose, 0, 1, 0);
        buffer.addVertex(pose.pose(), 0, scale, 0).setColor(1f, 1f, 1f, a).setNormal(pose, 0, 1, 0);

        // Tip cube — small wireframe handle so there's a "thing" the user can target with the cursor.
        float box = scale * 0.12f;
        drawWireBox(buffer, pose, 0, scale, 0, box, 1f, 1f, 1f, a, 0, 1, 0);
    }

    private static void drawWireBox(
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

        // 12 edges of the cube. Picking samples test cursor distance to these segments, so any visible
        // edge is also a click target.
        // Bottom face (y0).
        drawLine(buffer, pose, x0, y0, z0, x1, y0, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z0, x1, y0, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z1, x0, y0, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y0, z1, x0, y0, z0, r, g, b, a, nx, ny, nz);
        // Top face (y1).
        drawLine(buffer, pose, x0, y1, z0, x1, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y1, z0, x1, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y1, z1, x0, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y1, z1, x0, y1, z0, r, g, b, a, nx, ny, nz);
        // Vertical edges.
        drawLine(buffer, pose, x0, y0, z0, x0, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z0, x1, y1, z0, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x1, y0, z1, x1, y1, z1, r, g, b, a, nx, ny, nz);
        drawLine(buffer, pose, x0, y0, z1, x0, y1, z1, r, g, b, a, nx, ny, nz);
    }

    private static void drawLine(
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
