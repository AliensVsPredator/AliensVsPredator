package com.blib.engine.modeler.gizmo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.gizmo.GizmoGeometry;
import com.blib.engine.gizmo.GizmoPrimitives;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerTransforms;

/**
 * Renders the modeler's translate / rotate / resize gizmo at the selected cube's pivot, in the cube's post-rotation
 * local frame so the gizmo follows the cube as the user authors rotation. Captures a {@link GizmoGeometry} into
 * {@link ModelerGizmoState} for input-time picking + drag math.
 * <p>
 * Visuals are emitted via {@link GizmoPrimitives}; gimbal layout matches {@code BLibGizmoRenderer} except the
 * pose-stack frame here is post-rotation (vs. pre-rotation for the item tuner). The frame choice is intentional: the
 * item tuner keeps axes screen-stable so user-applied rotation doesn't drag them around, whereas the modeler user wants
 * the gizmo to follow the cube they're shaping.
 */
@ApiStatus.Internal
public final class ModelerGizmoRenderer {

    private ModelerGizmoRenderer() {}

    public static void render(Matrix4f scenePose, ModelerScene scene, Matrix4f projectionForCapture) {
        var mode = ModelerGizmoState.mode();
        if (mode == ModelerGizmoMode.OFF) {
            ModelerGizmoState.setLastRender(null);
            return;
        }

        var selection = scene.selectedCubeWithOwner();
        if (selection == null) {
            ModelerGizmoState.setLastRender(null);
            return;
        }

        // Walk the bone tree until we find the owner. Same convention as ModelerCubeRenderer so the gizmo lines
        // up with what the user sees.
        var pose = new PoseStack();
        pose.last().pose().mul(scenePose);

        if (!walkToOwner(pose, scene.root, selection.owner())) {
            // Selection owner not in current tree (e.g. scene reloaded after click). Bail.
            ModelerGizmoState.setLastRender(null);
            return;
        }

        // Apply the cube's transform (pivot → rotate → unpivot) so the gizmo's axes follow the cube's authored
        // rotation. After this the pose-stack basis is the cube's post-rotation frame; translating by an anchor in
        // cube-local pre-rotation coords lands the origin at that anchor after rotation around the pivot.
        ModelerTransforms.applyCube(pose, selection.cube());
        var anchor = anchorForMode(selection.cube(), mode);
        pose.translate((float) anchor[0], (float) anchor[1], (float) anchor[2]);

        // Depth-based scale: same formula as BLibGizmoRenderer so the gizmo looks roughly the same on-screen size
        // regardless of camera distance.
        var probe = GizmoGeometry.capture(pose, 1f, projectionForCapture);
        float depth = probe.viewPivot().length();
        float scale = Math.max(0.15f, depth * 0.15f);

        var geometry = GizmoGeometry.capture(pose, scale, projectionForCapture);

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(4.0f);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        switch (mode) {
            case TRANSLATE -> drawTranslate(pose, buffer, scale);
            case ROTATE -> drawRotate(pose, buffer, scale);
            case RESIZE -> drawResize(pose, buffer, scale, selection.cube());
            default -> {
                /* OFF — early-returned above. */
            }
        }

        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }

        ModelerGizmoState.setLastRender(new ModelerGizmoState.RenderSnapshot(geometry, selection.owner(), selection.cube()));
    }

    /** Recursively walks the bone tree, applying transforms onto {@code pose}, until {@code target} is reached. */
    private static boolean walkToOwner(PoseStack pose, ModelerBone bone, ModelerBone target) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

        if (bone == target) {
            // Leave the pose transformed and on top of the stack — caller continues with cube transform.
            return true;
        }

        for (var child : bone.children) {
            if (walkToOwner(pose, child, target)) {
                return true;
            }
        }

        pose.popPose();
        return false;
    }

    private static int activeDragAxis() {
        var drag = ModelerGizmoState.drag();
        return drag == null ? -1 : drag.axis();
    }

    private static int activeDragSign() {
        var drag = ModelerGizmoState.drag();
        return drag == null ? 1 : drag.sign();
    }

    private static void drawTranslate(PoseStack pose, com.mojang.blaze3d.vertex.VertexConsumer buffer, float scale) {
        int dragAxis = activeDragAxis();
        for (int axis = 0; axis < 3; axis++) {
            var color = GizmoPrimitives.axisColor(axis);
            float a = dragAxis == axis ? 1f : 0.85f;
            GizmoPrimitives.drawArrow(pose, buffer, scale, axis, color[0], color[1], color[2], a);
        }
    }

    private static void drawRotate(PoseStack pose, com.mojang.blaze3d.vertex.VertexConsumer buffer, float scale) {
        int dragAxis = activeDragAxis();
        for (int axis = 0; axis < 3; axis++) {
            var color = GizmoPrimitives.axisColor(axis);
            float a = dragAxis == axis ? 1f : 0.75f;
            GizmoPrimitives.drawRing(pose, buffer, scale, axis, color[0], color[1], color[2], a);
        }
    }

    /**
     * Six face arrows, one per ±X/±Y/±Z, anchored at the corresponding cube face center in cube-local pixels. The pose
     * stack at entry sits at the cube's pivot post-rotation; we push to each face center before drawing each arrow so
     * the gizmo origin stays at the pivot but each handle's BASE sits on its face.
     */
    private static void drawResize(PoseStack pose, com.mojang.blaze3d.vertex.VertexConsumer buffer, float scale, ModelerCube cube) {
        int dragAxis = activeDragAxis();
        int dragSign = activeDragSign();

        // Use a shorter arrow length than translate's so the face arrows visually read as different — they sit on
        // the cube surface alongside the user's authored geometry and benefit from being unobtrusive.
        float resizeScale = scale * 0.6f;

        for (int axis = 0; axis < 3; axis++) {
            for (int sign : new int[] { -1, 1 }) {
                var faceCenter = faceCenterLocal(cube, axis, sign);
                var color = GizmoPrimitives.axisColor(axis);
                boolean dragged = dragAxis == axis && dragSign == sign;
                float a = dragged ? 1f : 0.85f;

                pose.pushPose();
                pose.translate((float) faceCenter[0], (float) faceCenter[1], (float) faceCenter[2]);
                GizmoPrimitives.drawArrow(pose, buffer, resizeScale, axis, sign, color[0], color[1], color[2], a);
                pose.popPose();
            }
        }
    }

    /**
     * Pose-stack-local anchor for the gizmo's origin, in cube-local pre-rotation coordinates. TRANSLATE anchors at the
     * cube's visual center so the gizmo tracks the cube as the user moves it; ROTATE and RESIZE anchor at the cube's
     * pivot since rotation is around the pivot in the data model and resize face offsets are pivot-relative.
     */
    static double[] anchorForMode(ModelerCube cube, ModelerGizmoMode mode) {
        return switch (mode) {
            case TRANSLATE -> new double[] {
                cube.origin.x + cube.size.x * 0.5,
                cube.origin.y + cube.size.y * 0.5,
                cube.origin.z + cube.size.z * 0.5
            };
            case ROTATE, RESIZE -> new double[] { cube.pivot.x, cube.pivot.y, cube.pivot.z };
            default -> new double[] { cube.pivot.x, cube.pivot.y, cube.pivot.z };
        };
    }

    /**
     * Face center (in cube-local pixels relative to the cube's pivot) for the {@code ±axis} face. Used to anchor each
     * resize handle on its face. RESIZE anchors the gizmo at the cube pivot, so this offset is face-position minus
     * pivot.
     */
    static double[] faceCenterLocal(ModelerCube cube, int axis, int sign) {
        double cx = cube.origin.x + cube.size.x * 0.5;
        double cy = cube.origin.y + cube.size.y * 0.5;
        double cz = cube.origin.z + cube.size.z * 0.5;
        double fx = cx, fy = cy, fz = cz;
        switch (axis) {
            case 0 -> fx = sign > 0 ? cube.origin.x + cube.size.x : cube.origin.x;
            case 1 -> fy = sign > 0 ? cube.origin.y + cube.size.y : cube.origin.y;
            default -> fz = sign > 0 ? cube.origin.z + cube.size.z : cube.origin.z;
        }
        // Convert face position from cube-local-origin coords to gizmo-pivot-relative coords (subtract pivot).
        return new double[] { fx - cube.pivot.x, fy - cube.pivot.y, fz - cube.pivot.z };
    }
}
