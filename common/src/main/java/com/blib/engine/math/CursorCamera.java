package com.blib.engine.math;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.session.EngineCameraFrame;
import com.blib.engine.session.EngineSession;

/**
 * Resolves the camera position used by gizmo picking and drag math. Always prefers the
 * {@link EngineCameraFrame#cameraPosition() captured frame} when available because vanilla applies a tick-interpolated
 * FOV multiplier on top of the user FOV — using the session's interpolated camera produces a small constant pixel
 * offset that grows with distance. Falls back to {@link EngineSession#cameraPosition()} on the very first frame before
 * the level renderer has captured anything.
 * <p>
 * Prior to this utility every gizmo class repeated the same fallback boilerplate inline.
 */
@ApiStatus.Internal
public final class CursorCamera {

    private CursorCamera() {}

    public static Vec3 position(EngineSession session) {
        var captured = EngineCameraFrame.cameraPosition();
        return captured != null ? captured : session.cameraPosition();
    }
}
