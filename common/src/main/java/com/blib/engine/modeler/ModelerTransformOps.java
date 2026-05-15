package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;

/**
 * Whole-model transformations driven by the modeler viewport's <em>Transform</em> menu: 90° rotations around any world
 * axis, mirroring across any axis plane, and centering the model along one or two axes. Every operation pushes a
 * {@link ModelerAction} onto the history so a single Ctrl+Z reverts the entire gesture.
 * <p>
 * Scope: cube selections operate on only those cubes; otherwise transforms operate on the entire scene tree rooted at
 * {@link ModelerScene#root}. Bone-local transforms are kept intact (no recursion into world-space recomputation), so
 * the result is approximate for models with deeply rotated nested bones — fine for v1 because most authored entity
 * models keep bone hierarchies axis-aligned in their rest pose.
 * <p>
 * Implementation choices:
 * <ul>
 * <li><b>Rotate</b> mutates {@link ModelerScene#root}'s authored rotation field only — visually rotates everything
 * downstream without baking into per-cube data. Pressing the same direction 4× wraps back to identity via the
 * {@link #normalizeDegrees} helper.</li>
 * <li><b>Flip</b> bakes into every cube and bone in the tree: cube origin / size mirrors, pivots negate, rotations
 * around the two axes perpendicular to the flip axis negate (the third stays — that axis's rotation isn't affected by a
 * mirror through its perpendicular plane), and cube box-UV mirror state toggles to preserve texture orientation under
 * the reflected geometry.</li>
 * <li><b>Center</b> mutates {@link ModelerScene#root}'s authored position. Bounds are computed in root-local coords by
 * walking every cube — same approximation as the wholesale flip.</li>
 * </ul>
 */
@ApiStatus.Internal
public final class ModelerTransformOps {

    /** Selects which world axis a transform operates against. */
    public enum Axis {
        X,
        Y,
        Z
    }

    private ModelerTransformOps() {}

    /**
     * Rotate selected cubes, or the entire model when no cubes are selected. Whole-model rotation is stored as a delta
     * to the root bone's authored rotation Euler so subsequent edits stack additively without baking the rotation into
     * per-cube data.
     */
    public static void rotate(Axis axis, int degrees) {
        var scene = ModelerScene.get();
        var selected = selectedCubes(scene);
        if (!selected.isEmpty()) {
            rotateSelectedCubes(selected, axis, degrees);
            return;
        }

        var root = scene.root;
        var before = ModelerAction.BoneMemento.of(root);
        root.rotation = switch (axis) {
            case X -> new Vec3(normalizeDegrees(root.rotation.x + degrees), root.rotation.y, root.rotation.z);
            case Y -> new Vec3(root.rotation.x, normalizeDegrees(root.rotation.y + degrees), root.rotation.z);
            case Z -> new Vec3(root.rotation.x, root.rotation.y, normalizeDegrees(root.rotation.z + degrees));
        };
        var after = ModelerAction.BoneMemento.of(root);
        if (!after.differsFrom(before)) {
            return;
        }
        var sign = degrees > 0 ? "+" : "";
        var description = "Rotate " + sign + degrees + "° around " + axis.name();
        ModelerActionHistory
            .push(new ModelerAction.BoneMementoAction("transform_rotate", description, System.currentTimeMillis(), root, before, after));
    }

    /**
     * Mirror the entire model across the plane perpendicular to {@code axis} (so "flip X" reflects through the YZ
     * plane). Cube spans stay positive — the {@link ModelerCube#origin} shifts by {@code -size} on the flipped axis so
     * the cube ends up at its mirror location with its original dimensions.
     */
    public static void flip(Axis axis) {
        var selected = selectedCubes(ModelerScene.get());
        if (!selected.isEmpty()) {
            flipSelectedCubes(selected, axis);
            return;
        }

        var children = new ArrayList<ModelerAction>();
        flipRecursive(ModelerScene.get().root, axis, children);
        if (children.isEmpty()) {
            return;
        }
        var description = "Flip across " + axis.name() + " axis";
        ModelerActionHistory
            .push(new ModelerAction.CompositeAction("transform_flip", description, System.currentTimeMillis(), children));
    }

    /**
     * Translate the model so its world-space bounds center sits at 0 on the selected axis (the other two axes stay
     * put). No-op when the model has no cubes — there's no bounds to center.
     */
    public static void center(Axis axis) {
        var scene = ModelerScene.get();
        var selected = selectedCubes(scene);
        if (!selected.isEmpty()) {
            centerSelectedCubes(selected, axis);
            return;
        }

        var bounds = computeBounds(scene.root);
        if (bounds == null) {
            return;
        }
        var center = bounds.center();
        var root = scene.root;
        var before = ModelerAction.BoneMemento.of(root);
        root.position = switch (axis) {
            case X -> new Vec3(root.position.x - center.x, root.position.y, root.position.z);
            case Y -> new Vec3(root.position.x, root.position.y - center.y, root.position.z);
            case Z -> new Vec3(root.position.x, root.position.y, root.position.z - center.z);
        };
        var after = ModelerAction.BoneMemento.of(root);
        if (!after.differsFrom(before)) {
            return;
        }
        var description = "Center along " + axis.name();
        ModelerActionHistory
            .push(new ModelerAction.BoneMementoAction("transform_center", description, System.currentTimeMillis(), root, before, after));
    }

    /**
     * Center along X and Z simultaneously while leaving Y alone — for entity models that should stay grounded on their
     * feet while their lateral footprint centers on the world origin. Common enough to deserve a dedicated menu entry
     * alongside the per-axis options.
     */
    public static void centerLateral() {
        var scene = ModelerScene.get();
        var selected = selectedCubes(scene);
        if (!selected.isEmpty()) {
            centerSelectedCubesLateral(selected);
            return;
        }

        var bounds = computeBounds(scene.root);
        if (bounds == null) {
            return;
        }
        var center = bounds.center();
        var root = scene.root;
        var before = ModelerAction.BoneMemento.of(root);
        root.position = new Vec3(root.position.x - center.x, root.position.y, root.position.z - center.z);
        var after = ModelerAction.BoneMemento.of(root);
        if (!after.differsFrom(before)) {
            return;
        }
        ModelerActionHistory
            .push(
                new ModelerAction.BoneMementoAction(
                    "transform_center_lateral",
                    "Center laterally (X + Z)",
                    System.currentTimeMillis(),
                    root,
                    before,
                    after
                )
            );
    }

    public static void toggleSelectedMirrorUv() {
        var selected = selectedCubes(ModelerScene.get());
        if (selected.isEmpty()) {
            return;
        }

        var actions = new ArrayList<ModelerAction>();
        for (var selectedCube : selected) {
            var cube = selectedCube.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            cube.mirrorUv = !cube.mirrorUv;
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions
                    .add(
                        new ModelerAction.CubeMementoAction(
                            "transform_mirror_uv",
                            "Mirror UV " + cube.name,
                            System.currentTimeMillis(),
                            cube,
                            before,
                            after
                        )
                    );
            }
        }
        pushCubeActions(actions, "Mirror UV for selected cubes");
    }

    private static void rotateSelectedCubes(List<SelectedCube> selected, Axis axis, int degrees) {
        var actions = new ArrayList<ModelerAction>();
        for (var selectedCube : selected) {
            var cube = selectedCube.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            cube.rotation = switch (axis) {
                case X -> new Vec3(normalizeDegrees(cube.rotation.x + degrees), cube.rotation.y, cube.rotation.z);
                case Y -> new Vec3(cube.rotation.x, normalizeDegrees(cube.rotation.y + degrees), cube.rotation.z);
                case Z -> new Vec3(cube.rotation.x, cube.rotation.y, normalizeDegrees(cube.rotation.z + degrees));
            };
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions
                    .add(
                        new ModelerAction.CubeMementoAction(
                            "transform_rotate_cube",
                            "Rotate cube " + cube.name,
                            System.currentTimeMillis(),
                            cube,
                            before,
                            after
                        )
                    );
            }
        }
        var sign = degrees > 0 ? "+" : "";
        pushCubeActions(actions, "Rotate " + sign + degrees + "° around " + axis.name());
    }

    private static void flipSelectedCubes(List<SelectedCube> selected, Axis axis) {
        var actions = new ArrayList<ModelerAction>();
        for (var selectedCube : selected) {
            var cube = selectedCube.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            flipCube(cube, axis);
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions
                    .add(
                        new ModelerAction.CubeMementoAction(
                            "transform_flip_cube",
                            "Flip cube " + cube.name,
                            System.currentTimeMillis(),
                            cube,
                            before,
                            after
                        )
                    );
            }
        }
        pushCubeActions(actions, "Flip across " + axis.name() + " axis");
    }

    private static void centerSelectedCubes(List<SelectedCube> selected, Axis axis) {
        var bounds = computeBounds(selected);
        if (bounds == null) {
            return;
        }
        var center = bounds.center();
        var delta = switch (axis) {
            case X -> new Vec3(-center.x, 0, 0);
            case Y -> new Vec3(0, -center.y, 0);
            case Z -> new Vec3(0, 0, -center.z);
        };
        moveSelectedCubes(selected, delta, "Center selected cubes on " + axis.name());
    }

    private static void centerSelectedCubesLateral(List<SelectedCube> selected) {
        var bounds = computeBounds(selected);
        if (bounds == null) {
            return;
        }
        var center = bounds.center();
        moveSelectedCubes(selected, new Vec3(-center.x, 0, -center.z), "Center selected cubes laterally (X + Z)");
    }

    private static void moveSelectedCubes(List<SelectedCube> selected, Vec3 delta, String description) {
        var actions = new ArrayList<ModelerAction>();
        for (var selectedCube : selected) {
            var cube = selectedCube.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            cube.origin = cube.origin.add(delta);
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions
                    .add(
                        new ModelerAction.CubeMementoAction(
                            "transform_center_cube",
                            "Center cube " + cube.name,
                            System.currentTimeMillis(),
                            cube,
                            before,
                            after
                        )
                    );
            }
        }
        pushCubeActions(actions, description);
    }

    private static void pushCubeActions(List<ModelerAction> actions, String description) {
        if (actions.isEmpty()) {
            return;
        }
        if (actions.size() == 1) {
            ModelerActionHistory.push(actions.get(0));
            return;
        }
        ModelerActionHistory
            .push(new ModelerAction.CompositeAction("transform_cubes", description, System.currentTimeMillis(), List.copyOf(actions)));
    }

    /**
     * Apply the flip to a bone subtree, accumulating one memento action per mutated bone or cube into {@code out}. The
     * composite action wrapping {@code out} round-trips the entire gesture under one undo step.
     */
    private static void flipRecursive(ModelerBone bone, Axis axis, List<ModelerAction> out) {
        var boneBefore = ModelerAction.BoneMemento.of(bone);
        flipBone(bone, axis);
        var boneAfter = ModelerAction.BoneMemento.of(bone);
        if (boneAfter.differsFrom(boneBefore)) {
            out
                .add(
                    new ModelerAction.BoneMementoAction(
                        "transform_flip_bone",
                        "Flip bone " + bone.name,
                        System.currentTimeMillis(),
                        bone,
                        boneBefore,
                        boneAfter
                    )
                );
        }
        for (var cube : bone.cubes) {
            var cubeBefore = ModelerAction.CubeMemento.of(cube);
            flipCube(cube, axis);
            var cubeAfter = ModelerAction.CubeMemento.of(cube);
            if (cubeAfter.differsFrom(cubeBefore)) {
                out
                    .add(
                        new ModelerAction.CubeMementoAction(
                            "transform_flip_cube",
                            "Flip cube " + cube.name,
                            System.currentTimeMillis(),
                            cube,
                            cubeBefore,
                            cubeAfter
                        )
                    );
            }
        }
        for (var child : bone.children) {
            flipRecursive(child, axis, out);
        }
    }

    private static void flipBone(ModelerBone bone, Axis axis) {
        bone.position = negateComponent(bone.position, axis);
        bone.pivot = negateComponent(bone.pivot, axis);
        bone.rotation = flipRotation(bone.rotation, axis);
    }

    private static void flipCube(ModelerCube cube, Axis axis) {
        cube.origin = mirrorCubeOrigin(cube.origin, cube.size, axis);
        cube.pivot = negateComponent(cube.pivot, axis);
        cube.rotation = flipRotation(cube.rotation, axis);
        cube.mirrorUv = !cube.mirrorUv;
    }

    private static Vec3 negateComponent(Vec3 v, Axis axis) {
        return switch (axis) {
            case X -> new Vec3(-v.x, v.y, v.z);
            case Y -> new Vec3(v.x, -v.y, v.z);
            case Z -> new Vec3(v.x, v.y, -v.z);
        };
    }

    /**
     * Mirrored origin for a cube: the cube's far face on the flipped axis becomes its near face, so the new origin is
     * {@code -(origin + size)} on that axis. Size stays positive since the cube spans the same length, just on the
     * opposite side of the mirror plane.
     */
    private static Vec3 mirrorCubeOrigin(Vec3 origin, Vec3 size, Axis axis) {
        return switch (axis) {
            case X -> new Vec3(-(origin.x + size.x), origin.y, origin.z);
            case Y -> new Vec3(origin.x, -(origin.y + size.y), origin.z);
            case Z -> new Vec3(origin.x, origin.y, -(origin.z + size.z));
        };
    }

    /**
     * Rotation around the flip axis is preserved (the mirror plane is perpendicular to it, so rotation in that axis
     * looks the same on both sides). The other two Euler components negate because mirroring inverts the handedness of
     * those rotations relative to the flipped frame.
     */
    private static Vec3 flipRotation(Vec3 rot, Axis axis) {
        return switch (axis) {
            case X -> new Vec3(rot.x, -rot.y, -rot.z);
            case Y -> new Vec3(-rot.x, rot.y, -rot.z);
            case Z -> new Vec3(-rot.x, -rot.y, rot.z);
        };
    }

    /**
     * Walk every cube in the tree and union its [origin, origin+size] AABB <em>in world space</em>, accumulating bone
     * translations through the chain so the returned bounds reflect where the model actually renders. Without this,
     * repeated centering calls would each subtract the same local center from the root's position and drift away from
     * origin instead of converging on it. Bone rotations and scale are not folded in — same v1 limitation called out in
     * the class doc; entity rest poses keep bones axis-aligned so the approximation holds.
     * <p>
     * Returns null when the model has no cubes (avoids producing Infinity-bounded results).
     */
    private static @Nullable Bounds computeBounds(ModelerBone root) {
        var min = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
        var max = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
        accumulateBounds(root, 0.0, 0.0, 0.0, min, max);
        if (min[0] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Bounds(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    private static @Nullable Bounds computeBounds(List<SelectedCube> selected) {
        var min = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
        var max = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
        for (var selectedCube : selected) {
            var ownerOffset = ownerPosition(selectedCube.owner());
            var cube = selectedCube.cube();
            var cubeMinX = ownerOffset.x + cube.origin.x;
            var cubeMinY = ownerOffset.y + cube.origin.y;
            var cubeMinZ = ownerOffset.z + cube.origin.z;
            min[0] = Math.min(min[0], cubeMinX);
            min[1] = Math.min(min[1], cubeMinY);
            min[2] = Math.min(min[2], cubeMinZ);
            max[0] = Math.max(max[0], cubeMinX + cube.size.x);
            max[1] = Math.max(max[1], cubeMinY + cube.size.y);
            max[2] = Math.max(max[2], cubeMinZ + cube.size.z);
        }
        if (min[0] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Bounds(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    /**
     * Recursive bounds accumulator. {@code parentX/Y/Z} is the world-space origin of the parent bone (or {@code 0,0,0}
     * for the root). Each cube in the current bone gets its corners expressed at
     * {@code parentX/Y/Z + bone.position + cube.origin}; children recurse with the just-computed bone world origin so
     * their cubes layer correctly.
     */
    private static void accumulateBounds(ModelerBone bone, double parentX, double parentY, double parentZ, double[] min, double[] max) {
        var boneX = parentX + bone.position.x;
        var boneY = parentY + bone.position.y;
        var boneZ = parentZ + bone.position.z;
        for (var cube : bone.cubes) {
            var cubeMinX = boneX + cube.origin.x;
            var cubeMinY = boneY + cube.origin.y;
            var cubeMinZ = boneZ + cube.origin.z;
            min[0] = Math.min(min[0], cubeMinX);
            min[1] = Math.min(min[1], cubeMinY);
            min[2] = Math.min(min[2], cubeMinZ);
            max[0] = Math.max(max[0], cubeMinX + cube.size.x);
            max[1] = Math.max(max[1], cubeMinY + cube.size.y);
            max[2] = Math.max(max[2], cubeMinZ + cube.size.z);
        }
        for (var child : bone.children) {
            accumulateBounds(child, boneX, boneY, boneZ, min, max);
        }
    }

    private static Vec3 ownerPosition(ModelerBone owner) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        var cursor = owner;
        while (cursor != null) {
            x += cursor.position.x;
            y += cursor.position.y;
            z += cursor.position.z;
            cursor = cursor.parent;
        }
        return new Vec3(x, y, z);
    }

    private static List<SelectedCube> selectedCubes(ModelerScene scene) {
        if (scene.selection instanceof Selection.CubeSelection cs) {
            return List.of(new SelectedCube(cs.owner(), cs.cube()));
        }
        if (scene.selection instanceof Selection.MultiCubeSelection ms) {
            var selected = new ArrayList<SelectedCube>(ms.cubes().size());
            for (var cs : ms.cubes()) {
                selected.add(new SelectedCube(cs.owner(), cs.cube()));
            }
            return selected;
        }
        return List.of();
    }

    /** Normalize an Euler angle into {@code (-180, 180]} so repeated rotations don't drift toward huge values. */
    private static double normalizeDegrees(double deg) {
        var d = deg % 360.0;
        if (d > 180.0) {
            d -= 360.0;
        } else if (d <= -180.0) {
            d += 360.0;
        }
        return d;
    }

    private record Bounds(
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
    ) {

        Vec3 center() {
            return new Vec3((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0);
        }
    }

    private record SelectedCube(
        ModelerBone owner,
        ModelerCube cube
    ) {}
}
