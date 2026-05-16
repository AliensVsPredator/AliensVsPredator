package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Ray-vs-cube picker for the modeler viewport. Walks the bone tree, builds each cube's world-space transform, inverts
 * it to bring the world-space cursor ray into cube-local coordinates, and runs a standard slab-method AABB intersect
 * against the cube's inflated bounds. Closest hit wins.
 * <p>
 * The transform stack mirrors {@code ModelerCubeRenderer} exactly so picking lines up with what's drawn.
 */
@ApiStatus.Internal
public final class ModelerPicker {

    /** A single hit candidate. {@code t} is the world-space ray parameter (distance along the unit-direction ray). */
    public record Hit(
        ModelerBone owner,
        ModelerCube cube,
        ModelerCube.Face face,
        Vec3 localPoint,
        double t
    ) {}

    private static final float EPSILON = 1e-8f;

    private ModelerPicker() {}

    public static @Nullable Hit pick(ModelerScene scene, Vec3 rayOrigin, Vec3 rayDir) {
        var state = new State(
            new Vector3f((float) rayOrigin.x, (float) rayOrigin.y, (float) rayOrigin.z),
            new Vector3f((float) rayDir.x, (float) rayDir.y, (float) rayDir.z)
        );
        walk(scene.root, new Matrix4f(), state);
        return state.toHit();
    }

    private static void walk(ModelerBone bone, Matrix4f parentToWorld, State state) {
        var boneToWorld = new Matrix4f(parentToWorld);
        ModelerTransforms.applyBone(boneToWorld, bone);

        for (var cube : bone.cubes) {
            var cubeToWorld = new Matrix4f(boneToWorld);
            ModelerTransforms.applyCube(cubeToWorld, cube);
            var worldToCube = new Matrix4f(cubeToWorld).invert();

            // Transform the ray into cube-local space. The ray parameter t is preserved across linear transforms
            // (localPoint = worldToCube * worldPoint, both using the same t), so we can compare t values across
            // cubes directly without rescaling.
            var localOrigin = worldToCube.transformPosition(new Vector3f(state.worldOrigin));
            var localDir = worldToCube.transformDirection(new Vector3f(state.worldDir));

            var hit = intersectAabb(localOrigin, localDir, cube);
            if (hit != null && hit.t() > 0 && hit.t() < state.bestT) {
                state.bestT = hit.t();
                state.bestBone = bone;
                state.bestCube = cube;
                state.bestFace = hit.face();
                var point = new Vector3f(localOrigin).add(new Vector3f(localDir).mul((float) hit.t()));
                state.bestLocalPoint = new Vec3(point.x, point.y, point.z);
            }
        }

        for (var child : bone.children) {
            walk(child, boneToWorld, state);
        }
    }

    /** Slab-method ray-AABB intersect. Returns the entry t + face, or the exit face if the ray starts inside. */
    private static @Nullable AabbHit intersectAabb(Vector3f origin, Vector3f dir, ModelerCube cube) {
        var inflate = (float) cube.inflate;
        var minX = (float) cube.origin.x - inflate;
        var minY = (float) cube.origin.y - inflate;
        var minZ = (float) cube.origin.z - inflate;
        var maxX = minX + (float) cube.size.x + 2 * inflate;
        var maxY = minY + (float) cube.size.y + 2 * inflate;
        var maxZ = minZ + (float) cube.size.z + 2 * inflate;

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;
        ModelerCube.Face enterFace = ModelerCube.Face.NORTH;
        ModelerCube.Face exitFace = ModelerCube.Face.SOUTH;

        // X slab.
        if (Math.abs(dir.x) < EPSILON) {
            if (origin.x < minX || origin.x > maxX)
                return null;
        } else {
            var t1 = (minX - origin.x) / dir.x;
            var t2 = (maxX - origin.x) / dir.x;
            var nearFace = ModelerCube.Face.WEST;
            var farFace = ModelerCube.Face.EAST;
            if (t1 > t2) {
                var tmp = t1;
                t1 = t2;
                t2 = tmp;
                nearFace = ModelerCube.Face.EAST;
                farFace = ModelerCube.Face.WEST;
            }
            if (t1 > tMin) {
                tMin = t1;
                enterFace = nearFace;
            }
            if (t2 < tMax) {
                tMax = t2;
                exitFace = farFace;
            }
            if (tMin > tMax)
                return null;
        }
        // Y slab.
        if (Math.abs(dir.y) < EPSILON) {
            if (origin.y < minY || origin.y > maxY)
                return null;
        } else {
            var t1 = (minY - origin.y) / dir.y;
            var t2 = (maxY - origin.y) / dir.y;
            var nearFace = ModelerCube.Face.DOWN;
            var farFace = ModelerCube.Face.UP;
            if (t1 > t2) {
                var tmp = t1;
                t1 = t2;
                t2 = tmp;
                nearFace = ModelerCube.Face.UP;
                farFace = ModelerCube.Face.DOWN;
            }
            if (t1 > tMin) {
                tMin = t1;
                enterFace = nearFace;
            }
            if (t2 < tMax) {
                tMax = t2;
                exitFace = farFace;
            }
            if (tMin > tMax)
                return null;
        }
        // Z slab.
        if (Math.abs(dir.z) < EPSILON) {
            if (origin.z < minZ || origin.z > maxZ)
                return null;
        } else {
            var t1 = (minZ - origin.z) / dir.z;
            var t2 = (maxZ - origin.z) / dir.z;
            var nearFace = ModelerCube.Face.NORTH;
            var farFace = ModelerCube.Face.SOUTH;
            if (t1 > t2) {
                var tmp = t1;
                t1 = t2;
                t2 = tmp;
                nearFace = ModelerCube.Face.SOUTH;
                farFace = ModelerCube.Face.NORTH;
            }
            if (t1 > tMin) {
                tMin = t1;
                enterFace = nearFace;
            }
            if (t2 < tMax) {
                tMax = t2;
                exitFace = farFace;
            }
            if (tMin > tMax)
                return null;
        }

        if (tMax < 0)
            return null;
        return tMin > 0 ? new AabbHit(tMin, enterFace) : new AabbHit(tMax, exitFace);
    }

    private record AabbHit(
        double t,
        ModelerCube.Face face
    ) {}

    private static final class State {

        final Vector3f worldOrigin;

        final Vector3f worldDir;

        double bestT = Double.POSITIVE_INFINITY;

        ModelerBone bestBone;

        ModelerCube bestCube;

        ModelerCube.Face bestFace;

        Vec3 bestLocalPoint;

        State(Vector3f origin, Vector3f dir) {
            this.worldOrigin = origin;
            this.worldDir = dir;
        }

        @Nullable
        Hit toHit() {
            return bestCube == null || bestFace == null || bestLocalPoint == null
                ? null
                : new Hit(bestBone, bestCube, bestFace, bestLocalPoint, bestT);
        }
    }
}
