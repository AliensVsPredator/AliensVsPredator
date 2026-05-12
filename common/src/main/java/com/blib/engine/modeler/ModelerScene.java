package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;

/**
 * Singleton state for the in-engine modeler. Heap-only (no codecs, no S2C sync, no project files for v1) — closing the
 * engine drops all changes. Panels read/write this directly to keep the v1 plumbing minimal.
 * <p>
 * On first access the scene is seeded with an implicit root bone and a single test cube (so the renderer has something
 * to draw before any user authoring happens). Loading an existing BLib model via {@code ModelerSceneLoader} replaces
 * {@link #root} wholesale.
 */
@ApiStatus.Internal
public final class ModelerScene {

    private static volatile ModelerScene instance;

    public static ModelerScene get() {
        var local = instance;
        if (local == null) {
            synchronized (ModelerScene.class) {
                local = instance;
                if (local == null) {
                    local = new ModelerScene();
                    seed(local);
                    instance = local;
                }
            }
        }
        return local;
    }

    /** Replace the singleton — used by the load-model path to drop the previous scene wholesale. */
    public static void replace(ModelerScene next) {
        synchronized (ModelerScene.class) {
            instance = next;
        }
    }

    public ModelerBone root = new ModelerBone("root");

    public final ModelerCamera camera = new ModelerCamera();

    public @Nullable Selection selection;

    /**
     * Cube currently under the mouse cursor in the viewport, refreshed each frame by
     * {@code ModelerViewportPanel.render}. Drives the hover outline the cube renderer draws so users can see what
     * they'd select before clicking. Null whenever the cursor isn't over a cube, isn't over the panel, or a gizmo drag
     * is in flight (hover is noise during manipulation).
     */
    public @Nullable ModelerCube hoveredCube;

    /**
     * Pair of {@code (owner-bone, selected-cube)} when a cube is selected. Used by the gizmo system to rebuild the bone
     * transform chain that places the cube in scene space.
     */
    public record CubeWithOwner(
        ModelerBone owner,
        ModelerCube cube
    ) {}

    /** Returns the selected cube + its owning bone, or null when the selection isn't a cube. */
    public @Nullable CubeWithOwner selectedCubeWithOwner() {
        if (selection instanceof Selection.CubeSelection cs) {
            return new CubeWithOwner(cs.owner(), cs.cube());
        }
        return null;
    }

    /**
     * Add a default cube to the bone currently containing the selection (or the root if nothing is selected), select
     * it, and return it. Names are auto-generated to avoid collisions ({@code cube_1}, {@code cube_2}, …). Pushes a
     * {@link ModelerAction.CubeInsertAction} so the add round-trips through undo/redo.
     */
    public ModelerCube addDefaultCube() {
        var target = targetBoneForNewCube();
        var name = "cube_" + (target.cubes.size() + 1);
        var cube = ModelerCube.defaultCube(name);
        var index = target.cubes.size();
        target.cubes.add(cube);
        selection = new Selection.CubeSelection(target, cube);
        ModelerActionHistory.push(
            new ModelerAction.CubeInsertAction("cube_insert", "Add cube " + name, System.currentTimeMillis(), target, cube, index)
        );
        return cube;
    }

    /** Delete the currently-selected cube (no-op if a bone is selected or nothing is). */
    public boolean deleteSelectedCube() {
        if (!(selection instanceof Selection.CubeSelection cs)) {
            return false;
        }
        var owner = cs.owner();
        var cube = cs.cube();
        var index = owner.cubes.indexOf(cube);
        if (index < 0) {
            return false;
        }
        owner.cubes.remove(index);
        selection = null;
        ModelerActionHistory.push(
            new ModelerAction.CubeRemoveAction("cube_remove", "Delete cube " + cube.name, System.currentTimeMillis(), owner, cube, index)
        );
        return true;
    }

    /**
     * Delete the currently-selected cube or bone. Bone deletion removes the entire subtree (child bones + cubes) from
     * its parent — no children-reparenting since the modeler treats bones as atomic transform units, so promoting
     * orphans up a level would change their world placement. Refuses to delete the implicit root bone (whole-model
     * delete is a separate workflow). Returns true when something was removed.
     */
    public boolean deleteSelection() {
        if (selection instanceof Selection.CubeSelection) {
            return deleteSelectedCube();
        }
        if (selection instanceof Selection.BoneSelection bs) {
            var bone = bs.bone();
            var parent = bone.parent;
            if (parent == null) {
                return false;
            }
            var index = parent.children.indexOf(bone);
            if (index < 0) {
                return false;
            }
            parent.children.remove(index);
            selection = null;
            ModelerActionHistory.push(
                new ModelerAction.BoneRemoveAction(
                    "bone_remove",
                    "Delete bone " + bone.name,
                    System.currentTimeMillis(),
                    parent,
                    bone,
                    index
                )
            );
            return true;
        }
        return false;
    }

    private ModelerBone targetBoneForNewCube() {
        if (selection instanceof Selection.BoneSelection bs) {
            return bs.bone();
        }
        if (selection instanceof Selection.CubeSelection cs) {
            return cs.owner();
        }
        return root;
    }

    private static void seed(ModelerScene scene) {
        // A single 8x8x8 cube centered around the origin so the user has something to look at on first open. Removed
        // / replaced by the user as soon as they add their own cube or load a model.
        var cube = new ModelerCube(
            "test_cube",
            new Vec3(-4, 0, -4),
            new Vec3(8, 8, 8),
            Vec3.ZERO,
            new Vec3(0, 4, 0),
            0.0
        );
        scene.root.cubes.add(cube);
    }
}
