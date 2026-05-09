package com.blib.engine.session;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * Mutable per-activation state for {@link EngineMode}. Holds the freecam transform (current and previous-tick for
 * partial-tick interpolation), navigation-mode state, and accumulated mouse input.
 * <p>
 * All fields are written from the client thread (input handlers and the tick callback) and read from the camera mixin
 * during render. Single-threaded by design — Minecraft client work is.
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

    private double pendingMouseDx;

    private double pendingMouseDy;

    /** Mouse wheel delta accumulated by {@code MouseHandler.onScroll}; consumed during tick. */
    private double pendingScrollDy;

    /** Previous-tick mouse-button state for edge detection. */
    private boolean prevLeftDown;

    private boolean prevRightDown;

    /**
     * Selected entity, held weakly so a despawn / unload doesn't pin the entity in memory through us. Reads check
     * {@code isAlive()} and clear stale refs.
     */
    private @Nullable WeakReference<LivingEntity> selectedEntity;

    /**
     * Total mouse-pixel distance accumulated since the current LMB press. Used to disambiguate click (under threshold)
     * from drag (over threshold) so clicks can mean "select" while drags mean "orbit".
     */
    private double lmbDragDistance;

    /**
     * Set true once the current LMB press has crossed the drag threshold. Latches until release. Orbit math runs only
     * while this is true; before then the press is still a "potential click".
     */
    private boolean orbitDragActive;

    /**
     * Event-buffered LMB press / release. Mouse events fire on the client thread between tick boundaries; a fast click
     * can press and release entirely within one tick window, so polling {@code isLeftPressed} from the tick handler
     * would miss it. The mouse mixin sets these flags the moment {@code MouseHandler.onPress} fires; the tick handler
     * consumes them.
     */
    private boolean lmbPressPending;

    private boolean lmbReleasePending;

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

    public void addMouseDelta(double dx, double dy) {
        pendingMouseDx += dx;
        pendingMouseDy += dy;
    }

    public double consumeMouseDx() {
        var v = pendingMouseDx;
        pendingMouseDx = 0;
        return v;
    }

    public double consumeMouseDy() {
        var v = pendingMouseDy;
        pendingMouseDy = 0;
        return v;
    }

    public void addScroll(double dy) {
        pendingScrollDy += dy;
    }

    public double consumeScrollDy() {
        var v = pendingScrollDy;
        pendingScrollDy = 0;
        return v;
    }

    public boolean prevLeftDown() {
        return prevLeftDown;
    }

    public void setPrevLeftDown(boolean v) {
        this.prevLeftDown = v;
    }

    public boolean prevRightDown() {
        return prevRightDown;
    }

    public void setPrevRightDown(boolean v) {
        this.prevRightDown = v;
    }

    public @Nullable LivingEntity selectedEntity() {
        if (selectedEntity == null) {
            return null;
        }

        var entity = selectedEntity.get();

        if (entity == null || !entity.isAlive()) {
            selectedEntity = null;
            return null;
        }

        return entity;
    }

    public void setSelectedEntity(@Nullable LivingEntity entity) {
        this.selectedEntity = entity == null ? null : new WeakReference<>(entity);
    }

    public double lmbDragDistance() {
        return lmbDragDistance;
    }

    public void resetLmbDragDistance() {
        this.lmbDragDistance = 0;
    }

    public void addLmbDragDistance(double v) {
        this.lmbDragDistance += v;
    }

    public boolean orbitDragActive() {
        return orbitDragActive;
    }

    public void setOrbitDragActive(boolean v) {
        this.orbitDragActive = v;
    }

    public void notifyLmbPressed() {
        this.lmbPressPending = true;
    }

    public void notifyLmbReleased() {
        this.lmbReleasePending = true;
    }

    public boolean consumeLmbPressEvent() {
        var v = lmbPressPending;
        lmbPressPending = false;
        return v;
    }

    public boolean consumeLmbReleaseEvent() {
        var v = lmbReleasePending;
        lmbReleasePending = false;
        return v;
    }
}
