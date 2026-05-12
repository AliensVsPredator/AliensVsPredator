package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.blib.engine.session.EngineCameraBasis;

/**
 * Orbital camera for the modeler viewport. State is yaw/pitch/distance + a focus point that the camera always looks at.
 * Yaw/pitch follow {@link EngineCameraBasis}'s convention (matching Minecraft's player yaw: yaw=0 looks +Z south,
 * positive yaw rotates CCW from above) — same as the live-world {@code ViewportPanel}'s freecam, so any future shared
 * helper (gizmos, axis labels, world-space picking) doesn't have to translate between two dialects.
 * <p>
 * {@link #unprojectCursor} mirrors {@code EngineCameraFrame.ray()} so click-to-pick uses the actually-rendered
 * matrices.
 */
@ApiStatus.Internal
public final class ModelerCamera {

    /**
     * Default yaw + pitch chosen so the camera sits NW-elevated looking SE-down at the origin on first open. NW (camera
     * at {@code (-X, +Y, -Z)}) gives a familiar Blockbench-style isometric angle; yaw=315° points the look direction
     * toward SE (equal +X and +Z components) so the floor and seed cube are both clearly visible.
     */
    public float yaw = 315f;

    public float pitch = 30f;

    public float distance = 64f;

    public Vec3 focusPoint = new Vec3(0, 8, 0);

    /** Vertical field of view in degrees. */
    public float fovDegrees = 60f;

    public Matrix4f viewMatrix() {
        var pos = position();
        var center = new Vector3f((float) focusPoint.x, (float) focusPoint.y, (float) focusPoint.z);
        return new Matrix4f().lookAt(pos, center, up());
    }

    public Matrix4f projectionMatrix(float aspect) {
        return new Matrix4f().perspective((float) Math.toRadians(fovDegrees), aspect, 0.1f, 4096f);
    }

    /** Camera position in world space: focus minus forward × distance. */
    public Vector3f position() {
        var fwd = EngineCameraBasis.forward(yaw, pitch);
        return new Vector3f(
            (float) (focusPoint.x - fwd.x * distance),
            (float) (focusPoint.y - fwd.y * distance),
            (float) (focusPoint.z - fwd.z * distance)
        );
    }

    /**
     * Camera-up vector derived from yaw + pitch via the shared basis — stays perpendicular to forward at all pitches.
     */
    public Vector3f up() {
        var u = EngineCameraBasis.screenUp(yaw, pitch);
        return new Vector3f((float) u.x, (float) u.y, (float) u.z);
    }

    /**
     * Convert a cursor in viewport-relative [0,1] coords (top-left origin) into a world-space direction vector. Origin
     * of the ray is the camera position ({@link #position()}). Aspect must match the rectangle the viewport renders to.
     */
    public Vec3 unprojectCursor(float relX, float relY, float aspect) {
        var ndcX = 2.0f * relX - 1.0f;
        var ndcY = 1.0f - 2.0f * relY;
        var vp = new Matrix4f(projectionMatrix(aspect)).mul(viewMatrix());
        var inv = new Matrix4f(vp).invert();
        var nearPt = new Vector4f(ndcX, ndcY, -1f, 1f).mul(inv);
        var farPt = new Vector4f(ndcX, ndcY, 1f, 1f).mul(inv);
        nearPt.div(nearPt.w);
        farPt.div(farPt.w);
        var dir = new Vector3f(farPt.x - nearPt.x, farPt.y - nearPt.y, farPt.z - nearPt.z).normalize();
        return new Vec3(dir.x, dir.y, dir.z);
    }

    public void clampPitch() {
        if (pitch > 89f)
            pitch = 89f;
        if (pitch < -89f)
            pitch = -89f;
    }

    public void clampDistance() {
        if (distance < 1f)
            distance = 1f;
        if (distance > 1024f)
            distance = 1024f;
    }
}
