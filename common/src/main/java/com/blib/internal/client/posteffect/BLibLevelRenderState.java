package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

/**
 * Per-frame snapshot of camera matrices captured at the start of {@code LevelRenderer.renderLevel}, exposed to the
 * post-effect framework so post shaders can reconstruct world-relative-to-camera positions from depth.
 * <p>
 * Vanilla MC's {@code RenderSystem} does not expose the inverse view matrix, only the current modelview which mutates
 * as render passes push their own transforms. We grab {@code frustumMatrix} (the pure camera view) at the top of the
 * level pass and freeze it so downstream consumers see a stable value across the frame.
 * <p>
 * Also tracks a monotonic frame counter for double-buffer-style temporal effects (currently unused but matches the
 * JCL/Iris {@code frameCounter} convention).
 */
@ApiStatus.Internal
public final class BLibLevelRenderState {

    private static final Matrix4f viewMatrix = new Matrix4f();

    private static final Matrix4f viewMatrixInverse = new Matrix4f();

    private static int frameCounter;

    private static boolean haveView;

    private BLibLevelRenderState() {
        throw new UnsupportedOperationException();
    }

    public static void captureViewMatrix(Matrix4f frustumMatrix) {
        viewMatrix.set(frustumMatrix);
        viewMatrixInverse.set(frustumMatrix).invert();
        haveView = true;
        frameCounter++;
    }

    public static boolean isCaptured() {
        return haveView;
    }

    public static Matrix4f viewMatrix() {
        return viewMatrix;
    }

    public static Matrix4f viewMatrixInverse() {
        return viewMatrixInverse;
    }

    public static int frameCounter() {
        return frameCounter;
    }
}
