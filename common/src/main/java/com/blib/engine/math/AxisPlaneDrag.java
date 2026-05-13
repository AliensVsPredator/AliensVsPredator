package com.blib.engine.math;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Drag math shared by every axis-handle gizmo: pick a drag plane that contains a given axis, project the cursor ray
 * onto that plane, and return the projection along the axis. Replaces the 20-line {@code computeInitialAxisOffset}
 * helper that was duplicated across all five domain gizmos.
 * <p>
 * The "drag plane" is the unique plane that contains the drag axis and whose normal lies on the camera-to-pivot ray.
 * That normal has the largest meaningful projection onto cursor motion, so dragging feels responsive at any camera
 * angle.
 */
@ApiStatus.Internal
public final class AxisPlaneDrag {

    private AxisPlaneDrag() {}

    /**
     * Pick a unit-length plane normal for an axis-handle drag. {@code axisDir} is the axis being dragged along (must be
     * unit length); {@code anchor} is a point on the plane (usually the gizmo origin or face center); {@code camPos} is
     * the camera position used to choose the orientation of the plane.
     */
    public static Vec3 pickPlaneNormal(Vec3 axisDir, Vec3 anchor, Vec3 camPos) {
        var camToAnchor = anchor.subtract(camPos);
        var inPlane = camToAnchor.subtract(axisDir.scale(camToAnchor.dot(axisDir)));
        if (inPlane.lengthSqr() < 1.0e-6) {
            inPlane = axisDir.cross(new Vec3(1, 0, 0));
            if (inPlane.lengthSqr() < 1.0e-6) {
                inPlane = axisDir.cross(new Vec3(0, 0, 1));
            }
        }
        return inPlane.normalize();
    }

    /**
     * Project the cursor ray onto the drag plane and return the projection along the drag axis. Returns
     * {@link Double#NaN} when the ray is parallel to the plane or hits behind the camera.
     */
    public static double projectOntoAxis(
        Vec3 rayOrigin,
        Vec3 rayDir,
        Vec3 planePoint,
        Vec3 planeNormal,
        Vec3 axisDir
    ) {
        var denom = rayDir.dot(planeNormal);
        if (Math.abs(denom) < 1.0e-6) {
            return Double.NaN;
        }
        var t = planePoint.subtract(rayOrigin).dot(planeNormal) / denom;
        if (t <= 0) {
            return Double.NaN;
        }
        var hit = rayOrigin.add(rayDir.scale(t));
        return hit.subtract(planePoint).dot(axisDir);
    }

    /**
     * Convenience: projection along the axis, treating NaN as zero. Useful when the caller's contract is "fall back to
     * no drag" rather than "skip the update".
     */
    public static double projectOntoAxisOrZero(
        Vec3 rayOrigin,
        Vec3 rayDir,
        Vec3 planePoint,
        Vec3 planeNormal,
        Vec3 axisDir
    ) {
        var v = projectOntoAxis(rayOrigin, rayDir, planePoint, planeNormal, axisDir);
        return Double.isNaN(v) ? 0.0 : v;
    }
}
