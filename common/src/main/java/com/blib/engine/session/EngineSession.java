package com.blib.engine.session;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mutable per-activation state for {@link EngineMode}. Holds the freecam transform (current and previous-tick pose for
 * partial-tick interpolation), the active {@link NavigationMode}, and the orbit-camera pivot.
 * <p>
 * Single-threaded by design — written from the client thread (input handlers and the tick callback) and read from the
 * camera mixin during render.
 */
@ApiStatus.Internal
public final class EngineSession {

    private double prevX, prevY, prevZ;

    private float prevYaw, prevPitch;

    private double cameraX, cameraY, cameraZ;

    /** Yaw in degrees, mirroring {@code Entity#getYRot} (0 = south, +90 = west). */
    private float yaw;

    /** Pitch in degrees, mirroring {@code Entity#getXRot} (positive = looking down). */
    private float pitch;

    private NavigationMode mode = NavigationMode.FLY;

    /** Pivot point used by orbit/zoom math. Set on LMB-press in orbit mode; carries through pan/zoom. */
    private Vec3 pivot;

    EngineSession(double x, double y, double z, float yaw, float pitch) {
        this.prevX = this.cameraX = x;
        this.prevY = this.cameraY = y;
        this.prevZ = this.cameraZ = z;
        this.prevYaw = this.yaw = yaw;
        this.prevPitch = this.pitch = pitch;
        this.pivot = new Vec3(x, y, z);
    }

    /**
     * Snapshot the current pose into the previous-tick fields. Called once per client tick before applying that tick's
     * input, so the camera mixin can lerp between prev and current by partial-tick — giving smooth motion at render
     * rates above the tick rate.
     */
    public void snapshotForTick() {
        prevX = cameraX;
        prevY = cameraY;
        prevZ = cameraZ;
        prevYaw = yaw;
        prevPitch = pitch;
    }

    public Vec3 cameraPosition() {
        return new Vec3(cameraX, cameraY, cameraZ);
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public NavigationMode mode() {
        return mode;
    }

    public void setMode(NavigationMode mode) {
        this.mode = mode;
    }

    public Vec3 pivot() {
        return pivot;
    }

    public void setPivot(Vec3 pivot) {
        this.pivot = pivot;
    }

    /** Linearly interpolated render position. */
    public Vec3 interpolatedPosition(float partialTick) {
        return new Vec3(
            Mth.lerp(partialTick, prevX, cameraX),
            Mth.lerp(partialTick, prevY, cameraY),
            Mth.lerp(partialTick, prevZ, cameraZ)
        );
    }

    public float interpolatedYaw(float partialTick) {
        return Mth.rotLerp(partialTick, prevYaw, yaw);
    }

    public float interpolatedPitch(float partialTick) {
        return Mth.lerp(partialTick, prevPitch, pitch);
    }

    public void translate(double dx, double dy, double dz) {
        cameraX += dx;
        cameraY += dy;
        cameraZ += dz;
    }

    public void setPosition(double x, double y, double z) {
        cameraX = x;
        cameraY = y;
        cameraZ = z;
    }

    public void rotate(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch = Math.max(-89.9f, Math.min(89.9f, pitch + deltaPitch));
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = Math.max(-89.9f, Math.min(89.9f, pitch));
    }
}
