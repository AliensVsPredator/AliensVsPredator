package com.blib.api.client.render.v1.item;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Draws the interactive translate/rotate gizmo handles in the world. Hooks into
 * {@link BLibGeoBoneItemRenderer}'s pre-render phase at the same anchor as the pivot debug, so the gizmo
 * follows the held item's bone pivot through the user's tuner-applied translation but stays in the
 * pre-rotation frame so axes don't sweep around as the user adds rotation.
 * <p>
 * Render-time also captures a {@link BLibGizmoState.RenderSnapshot} that the mouse-input handler reads to
 * project handles to screen space for picking and to convert mouse drags into world-space deltas. The
 * snapshot is overwritten each frame the targeted item renders, so picking always uses the freshest pose.
 */
public final class BLibGizmoRenderer {

    /** Number of segments used to draw each rotate ring as a line strip. */
    private static final int RING_SEGMENTS = 48;

    private BLibGizmoRenderer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Called from {@link BLibGeoBoneItemRenderer#applyTransforms} when the gizmo is enabled. Renders the
     * handles for the active mode and captures the per-frame snapshot for input picking.
     * <p>
     * The pose stack must be at the gizmo anchor frame: post-{@code transform.apply}'s translation and
     * tuner pivot, but BEFORE the user's rotation/scale (so axes stay aligned to the model's pre-rotation
     * frame). The caller handles push/pop around this; this method assumes it can freely concatenate
     * transforms without leaving the stack in a different state.
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
        var depth = viewPivot.length();
        var scale = Math.max(0.15f, depth * 0.15f);

        var buffer = itemContext.multiBufferSource().getBuffer(RenderType.lines());
        var dragAxis = activeDragAxis();

        switch (gizmoMode) {
            case TRANSLATE -> drawTranslate(poseStack, buffer, scale, dragAxis);
            case ROTATE -> drawRotate(poseStack, buffer, scale, dragAxis);
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

        BLibGizmoState.setLastRender(new BLibGizmoState.RenderSnapshot(
            itemId,
            mode,
            itemContext.getTransformType(),
            viewPivot,
            viewX,
            viewY,
            viewZ,
            scale,
            new Matrix4f(RenderSystem.getProjectionMatrix()),
            BLibItemTransformOverrides.isRenderAsWallBlock()
        ));

        poseStack.popPose();
    }

    /**
     * Clears the per-frame render snapshot. Called once per frame from a tick handler before any rendering
     * runs, so a stale snapshot from a frame where the item wasn't rendered (item swapped out of hand,
     * inventory closed, etc.) doesn't drive picking against a position that's no longer on screen.
     */
    public static void resetFrameSnapshot() {
        BLibGizmoState.setLastRender(null);
    }

    /**
     * Project a unit direction in local space to a unit direction in view space. The pose-stack matrix's
     * upper-left 3x3 is the local-to-view linear part (rotation/scale), so we transform the direction by
     * dropping the translation column.
     */
    private static Vector3f transformDirection(Matrix4f m, float x, float y, float z) {
        var vec = new Vector4f(x, y, z, 0);
        m.transform(vec);
        return new Vector3f(vec.x, vec.y, vec.z).normalize();
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

    private static void drawLine(
        VertexConsumer buffer, PoseStack.Pose pose,
        float x1, float y1, float z1, float x2, float y2, float z2,
        float r, float g, float b, float a,
        float nx, float ny, float nz
    ) {
        buffer.addVertex(pose.pose(), x1, y1, z1).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose.pose(), x2, y2, z2).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
    }
}
