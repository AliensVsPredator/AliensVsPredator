package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Vanilla Java block-model element rotation rules. Stored block elements still support one axis only and a residual
 * angle in {@code [-45, 45]}; larger authored rotations are represented by baking quarter-turns into element bounds
 * and keeping only the vanilla-safe residual angle.
 */
@ApiStatus.Internal
public final class ModelerBlockElementRotation {

    public static final double[] ALLOWED_ANGLES = { -45.0, -22.5, 0.0, 22.5, 45.0 };

    private static final double EPSILON = 1.0e-4;

    private static final double RESCALE_22_5 = 1.0 / Math.cos(Math.PI / 8.0) - 1.0;

    private static final double RESCALE_45 = Math.sqrt(2.0) - 1.0;

    private static final int[] FACE_UV_BASE_INDICES = { 1, 0, 3, 2 };

    private ModelerBlockElementRotation() {}

    public record Value(int axis, double angle) {}

    public record Normalized(double snappedAngle, int quarterTurns, double residualAngle) {}

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

    public static double snapToStep(double angle) {
        return clean(Math.rint(angle / 22.5) * 22.5);
    }

    public static Normalized normalize(double angle) {
        var snapped = snapToStep(angle);
        var residual = snapped;
        var quarterTurns = 0;
        while (residual > 45.0 + EPSILON) {
            residual -= 90.0;
            quarterTurns++;
        }
        while (residual < -45.0 - EPSILON) {
            residual += 90.0;
            quarterTurns--;
        }
        return new Normalized(snapped, quarterTurns, clean(residual));
    }

    public static void applyBakedRotation(
        ModelerCube cube,
        Vec3 baseOrigin,
        Vec3 baseSize,
        Vec3 basePivot,
        boolean baseHasPerFaceUv,
        Map<ModelerCube.Face, ModelerCube.FaceUv> baseFaceUvs,
        int axis,
        double angle
    ) {
        var sourceFaceUvs = Map.copyOf(baseFaceUvs);
        var normalized = normalize(angle);
        var quarterTurns = normalized.quarterTurns();
        var origin = baseOrigin;
        var size = baseSize;

        if (Math.floorMod(quarterTurns, 4) != 0) {
            var baked = bakeBounds(baseOrigin, baseSize, basePivot, axis, quarterTurns);
            origin = baked.origin();
            size = baked.size();
        }

        cube.origin = origin;
        cube.size = size;
        cube.pivot = basePivot;
        cube.rotation = toRotation(axis, normalized.residualAngle());
        cube.hasPerFaceUv = baseHasPerFaceUv;
        cube.faceUvs.clear();
        cube.faceUvs.putAll(remapFaceUvs(baseOrigin, baseSize, basePivot, origin, size, sourceFaceUvs, axis, quarterTurns));
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

    private static Bounds bakeBounds(Vec3 origin, Vec3 size, Vec3 pivot, int axis, int quarterTurns) {
        var corners = corners(origin, size);
        var minX = Double.POSITIVE_INFINITY;
        var minY = Double.POSITIVE_INFINITY;
        var minZ = Double.POSITIVE_INFINITY;
        var maxX = Double.NEGATIVE_INFINITY;
        var maxY = Double.NEGATIVE_INFINITY;
        var maxZ = Double.NEGATIVE_INFINITY;
        for (var corner : corners) {
            var rotated = rotatePoint(corner, pivot, axis, quarterTurns);
            minX = Math.min(minX, rotated.x);
            minY = Math.min(minY, rotated.y);
            minZ = Math.min(minZ, rotated.z);
            maxX = Math.max(maxX, rotated.x);
            maxY = Math.max(maxY, rotated.y);
            maxZ = Math.max(maxZ, rotated.z);
        }
        var bakedOrigin = new Vec3(clean(minX), clean(minY), clean(minZ));
        var bakedSize = new Vec3(clean(maxX - minX), clean(maxY - minY), clean(maxZ - minZ));
        return new Bounds(bakedOrigin, bakedSize);
    }

    private static Map<ModelerCube.Face, ModelerCube.FaceUv> remapFaceUvs(
        Vec3 baseOrigin,
        Vec3 baseSize,
        Vec3 basePivot,
        Vec3 bakedOrigin,
        Vec3 bakedSize,
        Map<ModelerCube.Face, ModelerCube.FaceUv> baseFaceUvs,
        int axis,
        int quarterTurns
    ) {
        var remapped = new EnumMap<ModelerCube.Face, ModelerCube.FaceUv>(ModelerCube.Face.class);
        for (var entry : baseFaceUvs.entrySet()) {
            var oldFace = entry.getKey();
            var newFace = rotateFace(oldFace, axis, quarterTurns);
            var uv = entry.getValue();
            var rotation = remapUvRotation(
                oldFace,
                newFace,
                uv.rotation(),
                baseOrigin,
                baseSize,
                basePivot,
                bakedOrigin,
                bakedSize,
                axis,
                quarterTurns
            );
            remapped.put(newFace, new ModelerCube.FaceUv(uv.u(), uv.v(), uv.width(), uv.height(), rotation, uv.textureSource()));
        }
        return remapped;
    }

    private static int remapUvRotation(
        ModelerCube.Face oldFace,
        ModelerCube.Face newFace,
        int oldRotation,
        Vec3 baseOrigin,
        Vec3 baseSize,
        Vec3 basePivot,
        Vec3 bakedOrigin,
        Vec3 bakedSize,
        int axis,
        int quarterTurns
    ) {
        var oldVertices = faceVertices(oldFace, baseOrigin, baseSize);
        var newVertices = faceVertices(newFace, bakedOrigin, bakedSize);
        for (var oldIndex = 0; oldIndex < oldVertices.length; oldIndex++) {
            var transformed = rotatePoint(oldVertices[oldIndex], basePivot, axis, quarterTurns);
            var newIndex = matchingVertex(transformed, newVertices);
            if (newIndex >= 0) {
                var oldSteps = Math.floorMod(oldRotation / 90, 4);
                var newSteps = Math.floorMod(FACE_UV_BASE_INDICES[oldIndex] + oldSteps - FACE_UV_BASE_INDICES[newIndex], 4);
                return newSteps * 90;
            }
        }
        return Math.floorMod(oldRotation, 360);
    }

    private static int matchingVertex(Vec3 target, Vec3[] candidates) {
        for (var i = 0; i < candidates.length; i++) {
            if (samePoint(target, candidates[i])) {
                return i;
            }
        }
        return -1;
    }

    private static boolean samePoint(Vec3 a, Vec3 b) {
        return Math.abs(a.x - b.x) < EPSILON && Math.abs(a.y - b.y) < EPSILON && Math.abs(a.z - b.z) < EPSILON;
    }

    private static Vec3[] corners(Vec3 origin, Vec3 size) {
        var x0 = origin.x;
        var y0 = origin.y;
        var z0 = origin.z;
        var x1 = origin.x + size.x;
        var y1 = origin.y + size.y;
        var z1 = origin.z + size.z;
        return new Vec3[] {
            new Vec3(x0, y0, z0),
            new Vec3(x0, y0, z1),
            new Vec3(x0, y1, z0),
            new Vec3(x0, y1, z1),
            new Vec3(x1, y0, z0),
            new Vec3(x1, y0, z1),
            new Vec3(x1, y1, z0),
            new Vec3(x1, y1, z1)
        };
    }

    private static Vec3[] faceVertices(ModelerCube.Face face, Vec3 origin, Vec3 size) {
        var x0 = origin.x;
        var y0 = origin.y;
        var z0 = origin.z;
        var x1 = origin.x + size.x;
        var y1 = origin.y + size.y;
        var z1 = origin.z + size.z;
        return switch (face) {
            case EAST -> new Vec3[] { new Vec3(x1, y0, z0), new Vec3(x1, y1, z0), new Vec3(x1, y1, z1), new Vec3(x1, y0, z1) };
            case WEST -> new Vec3[] { new Vec3(x0, y0, z1), new Vec3(x0, y1, z1), new Vec3(x0, y1, z0), new Vec3(x0, y0, z0) };
            case UP -> new Vec3[] { new Vec3(x0, y1, z0), new Vec3(x0, y1, z1), new Vec3(x1, y1, z1), new Vec3(x1, y1, z0) };
            case DOWN -> new Vec3[] { new Vec3(x0, y0, z1), new Vec3(x0, y0, z0), new Vec3(x1, y0, z0), new Vec3(x1, y0, z1) };
            case SOUTH -> new Vec3[] { new Vec3(x1, y0, z1), new Vec3(x1, y1, z1), new Vec3(x0, y1, z1), new Vec3(x0, y0, z1) };
            case NORTH -> new Vec3[] { new Vec3(x0, y0, z0), new Vec3(x0, y1, z0), new Vec3(x1, y1, z0), new Vec3(x1, y0, z0) };
        };
    }

    private static ModelerCube.Face rotateFace(ModelerCube.Face face, int axis, int quarterTurns) {
        var normal = faceNormal(face);
        var rotated = rotateVector(normal[0], normal[1], normal[2], axis, quarterTurns);
        if (rotated[0] > 0) return ModelerCube.Face.EAST;
        if (rotated[0] < 0) return ModelerCube.Face.WEST;
        if (rotated[1] > 0) return ModelerCube.Face.UP;
        if (rotated[1] < 0) return ModelerCube.Face.DOWN;
        if (rotated[2] > 0) return ModelerCube.Face.SOUTH;
        return ModelerCube.Face.NORTH;
    }

    private static int[] faceNormal(ModelerCube.Face face) {
        return switch (face) {
            case EAST -> new int[] { 1, 0, 0 };
            case WEST -> new int[] { -1, 0, 0 };
            case UP -> new int[] { 0, 1, 0 };
            case DOWN -> new int[] { 0, -1, 0 };
            case SOUTH -> new int[] { 0, 0, 1 };
            case NORTH -> new int[] { 0, 0, -1 };
        };
    }

    private static Vec3 rotatePoint(Vec3 point, Vec3 pivot, int axis, int quarterTurns) {
        var rotated = rotateVector(point.x - pivot.x, point.y - pivot.y, point.z - pivot.z, axis, quarterTurns);
        return new Vec3(clean(rotated[0] + pivot.x), clean(rotated[1] + pivot.y), clean(rotated[2] + pivot.z));
    }

    private static double[] rotateVector(double x, double y, double z, int axis, int quarterTurns) {
        var turns = Math.floorMod(quarterTurns, 4);
        var rx = x;
        var ry = y;
        var rz = z;
        for (var i = 0; i < turns; i++) {
            var nextX = rx;
            var nextY = ry;
            var nextZ = rz;
            switch (axis) {
                case 0 -> {
                    nextY = -rz;
                    nextZ = ry;
                }
                case 1 -> {
                    nextX = rz;
                    nextZ = -rx;
                }
                default -> {
                    nextX = -ry;
                    nextY = rx;
                }
            }
            rx = nextX;
            ry = nextY;
            rz = nextZ;
        }
        return new double[] { clean(rx), clean(ry), clean(rz) };
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

    private static double clean(double value) {
        if (Math.abs(value) < EPSILON) {
            return 0.0;
        }
        return Math.rint(value * 1_000_000.0) / 1_000_000.0;
    }

    private record Bounds(Vec3 origin, Vec3 size) {}
}
