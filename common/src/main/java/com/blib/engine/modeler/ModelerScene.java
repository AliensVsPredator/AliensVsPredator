package com.blib.engine.modeler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.item.ModelerItemSession;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.TextureLoader;

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

    /**
     * Texture sheet dimensions in pixels, sourced from {@code minecraft:geometry.description.texture_width} on load.
     * Drives the UV map panel's grid + bounds rectangle. Defaults to 64 (matches a fresh-entity baseline) and is reset
     * by {@link #resetToEntity}; {@code ModelerSceneLoader.applyModel} overwrites both when a model carries its own.
     */
    public double textureWidth = 64.0;

    public double textureHeight = 64.0;

    public final ModelerCamera camera = new ModelerCamera();

    public @Nullable Selection selection;

    /**
     * When non-null, the modeler gizmo renderer and input target this selection instead of {@link #selection}.
     * Inspector / outliner still read {@link #selection}, so the override is invisible to the rest of the UI. Used by
     * the item-preview path to point the gizmo at {@link com.blib.engine.modeler.item.ModelerItemSession#gizmoShimBone}
     * without confusing the inspector / outliner.
     */
    public @Nullable Selection gizmoTargetSelection;

    /**
     * Cube currently under the mouse cursor in the viewport, refreshed each frame by
     * {@code ModelerViewportPanel.render}. Drives the hover outline the cube renderer draws so users can see what
     * they'd select before clicking. Null whenever the cursor isn't over a cube, isn't over the panel, or a gizmo drag
     * is in flight (hover is noise during manipulation).
     */
    public @Nullable ModelerCube hoveredCube;

    /**
     * PNG textures the user has loaded via the Textures panel. Insertion-ordered; the panel renders rows in the same
     * order and uses identity for "is this the active one". Cleared on {@link #resetToEntity}.
     */
    public final List<LoadedTexture> textures = new ArrayList<>();

    /**
     * Currently selected texture (or null when nothing is selected). When non-null, the cube renderer applies it to
     * cube faces and the UV map panel overlays it on the texture canvas.
     */
    public @Nullable LoadedTexture activeTexture;

    /**
     * Item-authoring session for the geo-bone item renderer. When non-null, the Inspector exposes the per-pose
     * transform editor and the Viewport gains a "Preview as" picker. Transform data lives in the existing tuner
     * ({@link com.blib.engine.gizmo.BLibItemTransformOverrides}), not here — this just tracks which item is attached
     * and which fields the Inspector/Viewport currently target.
     */
    public @Nullable ModelerItemSession itemSession;

    /**
     * Pair of {@code (owner-bone, selected-cube)} when a cube is selected. Used by the gizmo system to rebuild the bone
     * transform chain that places the cube in scene space.
     */
    public record CubeWithOwner(
        ModelerBone owner,
        ModelerCube cube
    ) {}

    /**
     * Returns the primary selected cube + its owning bone, or null when the selection isn't a cube. For multi-cube
     * selections this returns the primary (last-clicked) cube — single-cube tools (gizmo, inspector) operate on it.
     */
    public @Nullable CubeWithOwner selectedCubeWithOwner() {
        if (selection instanceof Selection.CubeSelection cs) {
            return new CubeWithOwner(cs.owner(), cs.cube());
        }
        if (selection instanceof Selection.MultiCubeSelection ms) {
            var primary = ms.primary();
            return new CubeWithOwner(primary.owner(), primary.cube());
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
        if (selection instanceof Selection.MultiCubeSelection ms) {
            return deleteMultiCubeSelection(ms);
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

    /**
     * Remove every cube in a multi-selection. Each individual removal is captured as its own
     * {@link ModelerAction.CubeRemoveAction} (at the cube's current index at removal time, which accounts for shifting
     * when multiple cubes share an owner). The actions are bundled in a {@link ModelerAction.CompositeAction} so a
     * single {@code Ctrl+Z} restores the whole group.
     */
    private boolean deleteMultiCubeSelection(Selection.MultiCubeSelection ms) {
        var actions = new ArrayList<ModelerAction>();
        for (var cs : ms.cubes()) {
            var owner = cs.owner();
            var cube = cs.cube();
            // indexOf reflects the post-prior-removals position, so subsequent CubeRemoveActions store the index they
            // were at when removed — undo (reverse order) reinserts them at those indices, putting the list back the
            // way it started.
            var index = owner.cubes.indexOf(cube);
            if (index < 0) {
                continue;
            }
            owner.cubes.remove(index);
            actions.add(
                new ModelerAction.CubeRemoveAction(
                    "cube_remove",
                    "Delete cube " + cube.name,
                    System.currentTimeMillis(),
                    owner,
                    cube,
                    index
                )
            );
        }
        if (actions.isEmpty()) {
            return false;
        }
        selection = null;
        if (actions.size() == 1) {
            ModelerActionHistory.push(actions.get(0));
        } else {
            ModelerActionHistory.push(
                new ModelerAction.CompositeAction(
                    "cube_remove_multi",
                    "Delete " + actions.size() + " cubes",
                    System.currentTimeMillis(),
                    List.copyOf(actions)
                )
            );
        }
        return true;
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

    /**
     * Drop any current scene contents (entity model + textures + selection + history) and attach an item-config session
     * for {@code itemId}. After this call the modeler is in item-config mode: the viewport renders the item via
     * vanilla's {@code ItemRenderer} for the session's default {@link net.minecraft.world.item.ItemDisplayContext}, and
     * the inspector exposes only the Item Config section.
     */
    public void attachItemSession(ResourceLocation itemId) {
        resetToEntity();
        // resetToEntity reseeds the entity scene with a default cube. Wipe the root again so the item-config viewport
        // doesn't see a stray cube in the background.
        this.root = new ModelerBone("root");
        this.itemSession = new ModelerItemSession(itemId);
    }

    /**
     * Reset the scene to the fresh-entity-model starting state — same seed as a brand-new ModelerScene instance, but
     * applied to the existing singleton so panels holding references stay valid. Clears the selection and local action
     * history because the old action entries point at bones/cubes/textures from the discarded scene.
     */
    public void resetToEntity() {
        closeTextures();
        this.itemSession = null;
        this.root = new ModelerBone("root");
        this.textureWidth = 64.0;
        this.textureHeight = 64.0;
        seed(this);
        this.selection = null;
        ModelerActionHistory.clear();
    }

    /**
     * Release every loaded texture's GPU resource and drop the list. Called from {@link #resetToEntity} so a fresh
     * scene starts with no imported textures. Safe to call when the list is already empty.
     */
    public void closeTextures() {
        for (var loaded : textures) {
            TextureLoader.release(loaded);
        }
        textures.clear();
        activeTexture = null;
    }
}
