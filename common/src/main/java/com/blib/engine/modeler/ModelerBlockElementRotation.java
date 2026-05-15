package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Vanilla Java block-model element rotation rules. Block elements support one axis only and the angle must be one of
 * {@code -45}, {@code -22.5}, {@code 0}, {@code 22.5}, or {@code 45}; this helper keeps the modeler UI, gizmo, and
 * preview transform using the same constraints.
 */
@ApiStatus.Internal
public final class ModelerBlockElementRotation {

    public static final double[] ALLOWED_ANGLES = { -45.0, -22.5, 0.0, 22.5, 45.0 };

    private static final double EPSILON = 1.0e-4;

    private static final double RESCALE_22_5 = 1.0 / Math.cos(Math.PI / 8.0) - 1.0;

    private static final double RESCALE_45 = Math.sqrt(2.0) - 1.0;

    private ModelerBlockElementRotation() {}

    public record Value(int axis, double angle) {}

    public static boolean isValid(Vec3 rotation) {
        var nonZeroAxes = 0;
        var angle = 0.0;
        for (var axis = 0; axis < 3; axis++) {
            var component = component(rotation, axis);
            if (!isZero(component)) {
                nonZeroAxes++;
                angle = component;
            }
        }
        return nonZeroAxes <= 1 && isAllowedAngle(angle);
    }

    /**
     * Convert an arbitrary Vec3 rotation into a stable UI value. Valid rotations come through unchanged; invalid
     * rotations pick the dominant axis and nearest vanilla-supported angle.
     */
    public static Value view(Vec3 rotation, @Nullable Integer preferredAxis) {
        var axis = dominantAxis(rotation);
        if (axis < 0) {
            axis = preferredAxis != null && preferredAxis >= 0 && preferredAxis < 3 ? preferredAxis : 0;
        }
        return new Value(axis, snapAngle(component(rotation, axis)));
    }

    public static Vec3 toRotation(int axis, double angle) {
        var snapped = snapAngle(angle);
        if (isZero(snapped)) {
            return Vec3.ZERO;
        }
        return switch (axis) {
            case 0 -> new Vec3(snapped, 0.0, 0.0);
            case 1 -> new Vec3(0.0, snapped, 0.0);
            default -> new Vec3(0.0, 0.0, snapped);
        };
    }

    public static double snapAngle(double angle) {
        var best = ALLOWED_ANGLES[0];
        var bestDistance = Math.abs(angle - best);
        for (var i = 1; i < ALLOWED_ANGLES.length; i++) {
            var candidate = ALLOWED_ANGLES[i];
            var distance = Math.abs(angle - candidate);
            if (distance < bestDistance) {
                best = candidate;
                bestDistance = distance;
            }
        }
        return best;
    }

    public static Vec3 rescaleVector(Vec3 rotation, boolean rescale) {
        if (!rescale) {
            return new Vec3(1.0, 1.0, 1.0);
        }
        var value = view(rotation, null);
        var angle = Math.abs(value.angle());
        if (isZero(angle)) {
            return new Vec3(1.0, 1.0, 1.0);
        }
        var factor = 1.0 + (Math.abs(angle - 22.5) < EPSILON ? RESCALE_22_5 : RESCALE_45);
        return switch (value.axis()) {
            case 0 -> new Vec3(1.0, factor, factor);
            case 1 -> new Vec3(factor, 1.0, factor);
            default -> new Vec3(factor, factor, 1.0);
        };
    }

    public static double component(Vec3 rotation, int axis) {
        return switch (axis) {
            case 0 -> rotation.x;
            case 1 -> rotation.y;
            default -> rotation.z;
        };
    }

    private static int dominantAxis(Vec3 rotation) {
        var bestAxis = -1;
        var bestValue = 0.0;
        for (var axis = 0; axis < 3; axis++) {
            var abs = Math.abs(component(rotation, axis));
            if (abs > bestValue) {
                bestAxis = axis;
                bestValue = abs;
            }
        }
        return isZero(bestValue) ? -1 : bestAxis;
    }

    private static boolean isAllowedAngle(double angle) {
        for (var allowed : ALLOWED_ANGLES) {
            if (Math.abs(angle - allowed) < EPSILON) {
                return true;
            }
        }
        return false;
    }

    private static boolean isZero(double value) {
        return Math.abs(value) < EPSILON;
    }
}
