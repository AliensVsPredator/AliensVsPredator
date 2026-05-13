package com.blib.engine.render.gizmo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.blib.engine.gizmo.BLibGizmoMode;
import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.gizmo.BLibItemTransformOverrides;
import com.blib.engine.gizmo.GizmoGeometry;
import com.blib.engine.gizmo.GizmoPrimitives;

/**
 * Draws the interactive translate/rotate gizmo handles in the world. Hooks into
 * {@link com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer}'s pre-render phase at the same anchor as the pivot
 * debug, so the gizmo follows the held item's bone pivot through the user's tuner-applied translation but stays in the
 * pre-rotation frame so axes don't sweep around as the user adds rotation.
 * <p>
 * Render-time also captures a {@link BLibGizmoState.RenderSnapshot} that the mouse-input handler reads to project
 * handles to screen space for picking and to convert mouse drags into world-space deltas. The snapshot is overwritten
 * each frame the targeted item renders, so picking always uses the freshest pose. Shared visual primitives live on
 * {@link GizmoPrimitives}; the geometric snapshot is built via {@link GizmoGeometry#capture}.
 */
public final class BLibGizmoRenderer {

    private BLibGizmoRenderer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Called from {@code BLibGeoBoneItemRenderer#applyTransforms} when the gizmo is enabled. Renders the handles for
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
            // We need the view-space depth here to size the gizmo. Capture geometry briefly with a scale of 1 just
            // to read viewPivot length, then re-capture with the chosen scale baked in. Two captures is cheap (a
            // matrix multiply + a couple of vector transforms each) and keeps the math centralized.
            var probe = GizmoGeometry.capture(poseStack, 1f);
            var depth = probe.viewPivot().length();
            scale = Math.max(0.15f, depth * 0.15f) * BLibGizmoState.handleScaleMultiplier();
        }

        var geometry = GizmoGeometry.capture(poseStack, scale);

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

        BLibGizmoState.setLastRender(
            new BLibGizmoState.RenderSnapshot(
                itemId,
                mode,
                itemContext.getTransformType(),
                geometry,
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
        for (int axis = 0; axis < 3; axis++) {
            var color = GizmoPrimitives.axisColor(axis);
            float a = dragAxis == axis ? 1f : 0.85f;
            GizmoPrimitives.drawArrow(poseStack, buffer, scale, axis, color[0], color[1], color[2], a);
        }
    }

    private static void drawRotate(PoseStack poseStack, VertexConsumer buffer, float scale, int dragAxis) {
        // Rings drawn around each axis. Color matches the axis being rotated AROUND so the user thinks
        // "click the red ring → rotate around X" — same convention as Blender's rotate gizmo.
        for (int axis = 0; axis < 3; axis++) {
            var color = GizmoPrimitives.axisColor(axis);
            float a = dragAxis == axis ? 1f : 0.75f;
            GizmoPrimitives.drawRing(poseStack, buffer, scale, axis, color[0], color[1], color[2], a);
        }
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
        float a = dragAxis == 0 ? 1f : 0.85f;

        // Shaft.
        GizmoPrimitives.drawLine(buffer, pose, 0, 0, 0, 0, scale, 0, 1f, 1f, 1f, a, 0, 1, 0);

        // Tip cube — small wireframe handle so there's a "thing" the user can target with the cursor.
        float box = scale * 0.12f;
        GizmoPrimitives.drawWireBox(buffer, pose, 0, scale, 0, box, 1f, 1f, 1f, a, 0, 1, 0);
    }
}
