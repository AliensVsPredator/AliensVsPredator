package com.blib.engine.session;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Canonical yaw/pitch → world-space basis vectors, shared by the engine's freecam ({@link EngineNavigation}) and the
 * in-engine modeler's orbital camera. Matches Minecraft's player-yaw convention so engine-mode entry can hand the
 * player's rotation off to the freecam without a snap, and so the modeler doesn't speak a different dialect than the
 * rest of the codebase.
 * <p>
 * Convention: {@code yaw=0, pitch=0} means looking +Z (south); {@code pitch>0} means looking down; positive yaw rotates
 * the look direction CCW when viewed from above (matching {@link net.minecraft.world.entity.Entity#getYRot}).
 * <p>
 * Relations:
 * <ul>
 * <li>{@code forward} is the unit vector the camera looks along.</li>
 * <li>{@code screenRight} is the world-horizontal direction to the right of the camera in screen space.</li>
 * <li>{@code screenUp} is the camera's local up axis, tilted with pitch.</li>
 * </ul>
 * These form an orthonormal frame perpendicular to each other at every yaw / pitch (no singularity at the poles).
 */
@ApiStatus.Internal
public final class EngineCameraBasis {

    private EngineCameraBasis() {}

    public static Vec3 forward(float yaw, float pitch) {
        var yawRad = Math.toRadians(yaw);
        var pitchRad = Math.toRadians(pitch);
        var cosPitch = Math.cos(pitchRad);
        return new Vec3(-Math.sin(yawRad) * cosPitch, -Math.sin(pitchRad), Math.cos(yawRad) * cosPitch);
    }

    public static Vec3 screenRight(float yaw) {
        var yawRad = Math.toRadians(yaw);
        return new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
    }

    public static Vec3 screenUp(float yaw, float pitch) {
        var yawRad = Math.toRadians(yaw);
        var pitchRad = Math.toRadians(pitch);
        var sinPitch = Math.sin(pitchRad);
        var cosPitch = Math.cos(pitchRad);
        return new Vec3(-Math.sin(yawRad) * sinPitch, cosPitch, Math.cos(yawRad) * sinPitch);
    }
}
