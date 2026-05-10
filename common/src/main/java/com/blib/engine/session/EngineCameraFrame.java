package com.blib.engine.session;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Per-frame snapshot of the matrices and camera position vanilla used to render the world. Captured at
 * {@link net.minecraft.client.renderer.LevelRenderer#renderLevel} HEAD by {@code MixinLevelRenderer_EngineCameraFrame};
 * consumed by the cursor-ray code so picking is pixel-perfect against whatever the user actually sees.
 * <p>
 * Why this exists: reconstructing the projection from {@code mc.options.fov().get()} + screen aspect almost matches
 * vanilla, but vanilla applies a tick-interpolated multiplier ({@code GameRenderer.fov} — sprint, item-use, zoom,
 * fluid) on top of the user FOV setting. That gives a small constant angular error in any reconstructed ray, which
 * manifests as a fixed pixel offset that grows with camera distance ("clicking the corner of a block picks the block
 * behind"). Using vanilla's actual rendered matrices eliminates the error entirely.
 * <p>
 * Matrices are deep-copied on capture because JOML matrices are mutable and vanilla may reuse the same instance across
 * frames; holding the input reference would race with the next frame's mutation.
 * <p>
 * Thread-safety: rendering and input dispatch both run on the render thread in single-player, so no synchronization is
 * needed.
 */
@ApiStatus.Internal
public final class EngineCameraFrame {

    private static @Nullable Matrix4f projection;

    private static @Nullable Matrix4f frustum;

    private static @Nullable Vec3 cameraPosition;

    private EngineCameraFrame() {}

    /**
     * Capture the matrices + camera position from the current render frame. Called per-frame from
     * {@code MixinLevelRenderer_EngineCameraFrame}.
     */
    public static void capture(Vec3 cam, Matrix4f frustumMatrix, Matrix4f projectionMatrix) {
        cameraPosition = cam;
        frustum = new Matrix4f(frustumMatrix);
        projection = new Matrix4f(projectionMatrix);
    }

    /** True if at least one frame has been captured (i.e. the world has rendered at least once). */
    public static boolean hasFrame() {
        return projection != null && frustum != null && cameraPosition != null;
    }

    public static @Nullable Vec3 cameraPosition() {
        return cameraPosition;
    }

    /**
     * Unproject a viewport-relative cursor position to a world-space ray direction. {@code relX, relY} are in
     * {@code [0, 1]}, with {@code (0, 0)} at the top-left of the viewport. Returns {@code null} if no frame has been
     * captured yet, or the matrices are degenerate.
     * <p>
     * Math: build the view-projection matrix vanilla used, invert it, transform two NDC points (one near, one far) at
     * the cursor's NDC coords, divide by w, take the difference. The result is in camera-relative space — the
     * camera-position translation cancels because it's identical for both the near and far point. Normalizing gives a
     * unit world-space direction.
     */
    public static @Nullable Vec3 cursorRayDirection(double relX, double relY) {
        if (projection == null || frustum == null) {
            return null;
        }

        var ndcX = (float) (2.0 * relX - 1.0);
        var ndcY = (float) (1.0 - 2.0 * relY);

        // viewProj = projection * frustum. Mul order matches vanilla: vertex -> frustum -> projection -> NDC.
        var vp = new Matrix4f(projection).mul(frustum);
        var vpInv = new Matrix4f(vp).invert();

        var near = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        vpInv.transform(near);
        var far = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);
        vpInv.transform(far);

        if (Math.abs(near.w()) < 1.0e-6f || Math.abs(far.w()) < 1.0e-6f) {
            return null;
        }
        var nearW = 1.0f / near.w();
        var farW = 1.0f / far.w();
        var dx = far.x() * farW - near.x() * nearW;
        var dy = far.y() * farW - near.y() * nearW;
        var dz = far.z() * farW - near.z() * nearW;

        var lenSq = dx * dx + dy * dy + dz * dz;
        if (lenSq < 1.0e-12) {
            return null;
        }
        var inv = 1.0 / Math.sqrt(lenSq);
        return new Vec3(dx * inv, dy * inv, dz * inv);
    }

    /** Clear captured state. Called when the engine workspace closes. */
    public static void clear() {
        projection = null;
        frustum = null;
        cameraPosition = null;
    }
}
