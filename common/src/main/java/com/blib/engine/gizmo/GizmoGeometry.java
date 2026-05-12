package com.blib.engine.gizmo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Per-frame geometric snapshot of a gizmo's pose. Captured at render time and read at input time so picking projects
 * handles to screen against the same matrices the user actually saw them through.
 * <p>
 * Vectors are in view space — pose-stack {@code last().pose()} composed with {@link RenderSystem#getModelViewMatrix()}
 * already applies the camera transform, so feeding the combined matrix into a perspective-divide via {@code projection}
 * gets us straight to screen pixels.
 *
 * @param viewPivot  View-space coordinate of the gizmo origin.
 * @param viewX      View-space direction of the gizmo's +X axis (NOT normalized — preserves pose-stack scale so picking
 *                   samples align with rendered geometry).
 * @param viewY      View-space direction of the gizmo's +Y axis.
 * @param viewZ      View-space direction of the gizmo's +Z axis.
 * @param scale      World-space length used for arrow length / ring radius in the renderer. Picking multiplies axis
 *                   vectors by this to recover handle endpoint positions.
 * @param projection Projection matrix in effect when the gizmo was drawn — captured so input handlers that fire while a
 *                   different projection is bound (e.g. GUI projection in chat) project against the matrix the handles
 *                   were rendered through.
 */
public record GizmoGeometry(
    Vector3f viewPivot,
    Vector3f viewX,
    Vector3f viewY,
    Vector3f viewZ,
    float scale,
    Matrix4f projection
) {

    /**
     * Capture a snapshot from the current render state. The pose stack must already be transformed to the gizmo anchor;
     * this method reads it together with {@code RenderSystem.getModelViewMatrix()} to build the local→view matrix and
     * extract the pivot + axis directions.
     */
    public static GizmoGeometry capture(PoseStack poseStack, float scale) {
        return capture(poseStack, scale, RenderSystem.getProjectionMatrix());
    }

    /**
     * Capture using an explicit projection matrix. Use when the projection isn't live on {@link RenderSystem} (e.g. the
     * modeler renders inside an FBO with its own projection that's restored after the FBO pass).
     */
    public static GizmoGeometry capture(PoseStack poseStack, float scale, Matrix4f projection) {
        // In MC 1.21 the camera rotation lives on RenderSystem's model-view stack, not the rendering PoseStack. The
        // pose stack holds local→camera-relative-world (translations only) and the model-view supplies the camera
        // rotation that takes us into view space. Combine the two so a single matrix maps local to view.
        var modelView = RenderSystem.getModelViewMatrix();
        var poseMat = poseStack.last().pose();
        var localToView = new Matrix4f(modelView).mul(poseMat);
        var viewPivot = new Vector3f(localToView.m30(), localToView.m31(), localToView.m32());

        // Don't normalize the axis directions — the rendered handles use pose.pose() × local for vertices, so the
        // radius/length they show on screen reflects the pose's scale. Picking multiplies these by gizmoScale to
        // recover handle endpoints; normalizing here would put picking samples at a different radius from the
        // visible geometry.
        var viewX = transformDirection(localToView, 1, 0, 0);
        var viewY = transformDirection(localToView, 0, 1, 0);
        var viewZ = transformDirection(localToView, 0, 0, 1);

        return new GizmoGeometry(viewPivot, viewX, viewY, viewZ, scale, new Matrix4f(projection));
    }

    /** Axis direction vector for {@code axis} (0=X, 1=Y, 2=Z). */
    public Vector3f axis(int axis) {
        return switch (axis) {
            case 0 -> viewX;
            case 1 -> viewY;
            default -> viewZ;
        };
    }

    private static Vector3f transformDirection(Matrix4f m, float x, float y, float z) {
        var vec = new Vector4f(x, y, z, 0);
        m.transform(vec);
        return new Vector3f(vec.x, vec.y, vec.z);
    }
}
