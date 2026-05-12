package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
     * Add a default cube to the bone currently containing the selection (or the root if nothing is selected), select
     * it, and return it. Names are auto-generated to avoid collisions ({@code cube_1}, {@code cube_2}, …).
     */
    public ModelerCube addDefaultCube() {
        var target = targetBoneForNewCube();
        var name = "cube_" + (target.cubes.size() + 1);
        var cube = ModelerCube.defaultCube(name);
        target.cubes.add(cube);
        selection = new Selection.CubeSelection(target, cube);
        return cube;
    }

    /** Delete the currently-selected cube (no-op if a bone is selected or nothing is). */
    public boolean deleteSelectedCube() {
        if (!(selection instanceof Selection.CubeSelection cs)) {
            return false;
        }
        var removed = cs.owner().cubes.remove(cs.cube());
        if (removed) {
            selection = null;
        }
        return removed;
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
