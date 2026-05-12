package com.blib.engine.modeler.history;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Sealed root of the modeler's client-side undo/redo entries. Every reversible modeler gesture (gizmo transform,
 * inspector edit, add cube, delete cube/bone) produces one instance which {@link ModelerActionHistory} stores so Ctrl+Z
 * / Ctrl+Y can step through them in order.
 * <p>
 * Concrete variants are records carrying the state needed to round-trip the change. References to bones/cubes are held
 * by identity — when an undo restores a previously-deleted bone/cube, it's the same heap instance that other actions
 * still reference, so layered undo+redo across multiple actions stays coherent.
 */
@ApiStatus.Internal
public sealed interface ModelerAction permits ModelerAction.CubeMementoAction, ModelerAction.BoneMementoAction, ModelerAction.CubeInsertAction, ModelerAction.CubeRemoveAction, ModelerAction.BoneRemoveAction {

    String typeId();

    String description();

    long timestamp();

    void undo();

    void redo();

    default ActionDescriptor toDescriptor() {
        return new ActionDescriptor(typeId(), description(), timestamp(), null, null);
    }

    /**
     * Snapshot of a cube's mutable fields, used as the before/after halves of {@link CubeMementoAction}. Captures every
     * editable field so the same record type covers gizmo transforms (which only change origin/size/rotation) and
     * inspector edits (which can also touch pivot/inflate).
     */
    record CubeMemento(
        String name,
        Vec3 origin,
        Vec3 size,
        Vec3 rotation,
        Vec3 pivot,
        double inflate
    ) {

        public static CubeMemento of(ModelerCube cube) {
            return new CubeMemento(cube.name, cube.origin, cube.size, cube.rotation, cube.pivot, cube.inflate);
        }

        public void apply(ModelerCube cube) {
            cube.name = name;
            cube.origin = origin;
            cube.size = size;
            cube.rotation = rotation;
            cube.pivot = pivot;
            cube.inflate = inflate;
        }

        public boolean differsFrom(CubeMemento other) {
            return !name.equals(other.name)
                || !origin.equals(other.origin)
                || !size.equals(other.size)
                || !rotation.equals(other.rotation)
                || !pivot.equals(other.pivot)
                || inflate != other.inflate;
        }
    }

    /** Snapshot of a bone's mutable fields. Same role as {@link CubeMemento} but for bones. */
    record BoneMemento(
        String name,
        Vec3 position,
        Vec3 rotation,
        Vec3 scale,
        Vec3 pivot
    ) {

        public static BoneMemento of(ModelerBone bone) {
            return new BoneMemento(bone.name, bone.position, bone.rotation, bone.scale, bone.pivot);
        }

        public void apply(ModelerBone bone) {
            bone.name = name;
            bone.position = position;
            bone.rotation = rotation;
            bone.scale = scale;
            bone.pivot = pivot;
        }

        public boolean differsFrom(BoneMemento other) {
            return !name.equals(other.name)
                || !position.equals(other.position)
                || !rotation.equals(other.rotation)
                || !scale.equals(other.scale)
                || !pivot.equals(other.pivot);
        }
    }

    /**
     * Field-level memento for cube edits — covers gizmo translate / rotate / resize and inspector edits. Undo restores
     * {@link #before}; redo restores {@link #after}.
     */
    record CubeMementoAction(
        String typeId,
        String description,
        long timestamp,
        ModelerCube target,
        CubeMemento before,
        CubeMemento after
    ) implements ModelerAction {

        @Override
        public void undo() {
            before.apply(target);
        }

        @Override
        public void redo() {
            after.apply(target);
        }
    }

    /** Field-level memento for bone edits via the inspector. */
    record BoneMementoAction(
        String typeId,
        String description,
        long timestamp,
        ModelerBone target,
        BoneMemento before,
        BoneMemento after
    ) implements ModelerAction {

        @Override
        public void undo() {
            before.apply(target);
        }

        @Override
        public void redo() {
            after.apply(target);
        }
    }

    /**
     * Cube insertion into a parent bone's cube list. Undo removes the cube; redo re-inserts it at the original index
     * (clamped so a stale index from an unrelated concurrent change doesn't throw).
     */
    record CubeInsertAction(
        String typeId,
        String description,
        long timestamp,
        ModelerBone parent,
        ModelerCube cube,
        int index
    ) implements ModelerAction {

        @Override
        public void undo() {
            parent.cubes.remove(cube);
            // Clear the active selection if it was this cube — otherwise the inspector would keep showing a removed
            // target. Bone-selecting parent is fine; only the exact cube reference invalidates.
            var sel = ModelerScene.get().selection;
            if (sel instanceof Selection.CubeSelection cs && cs.cube() == cube) {
                ModelerScene.get().selection = null;
            }
        }

        @Override
        public void redo() {
            if (!parent.cubes.contains(cube)) {
                var target = Math.min(Math.max(0, index), parent.cubes.size());
                parent.cubes.add(target, cube);
            }
            ModelerScene.get().selection = new Selection.CubeSelection(parent, cube);
        }
    }

    /** Cube removal from a parent bone's cube list. Mirror image of {@link CubeInsertAction}. */
    record CubeRemoveAction(
        String typeId,
        String description,
        long timestamp,
        ModelerBone parent,
        ModelerCube cube,
        int index
    ) implements ModelerAction {

        @Override
        public void undo() {
            if (!parent.cubes.contains(cube)) {
                var target = Math.min(Math.max(0, index), parent.cubes.size());
                parent.cubes.add(target, cube);
            }
            ModelerScene.get().selection = new Selection.CubeSelection(parent, cube);
        }

        @Override
        public void redo() {
            parent.cubes.remove(cube);
            var sel = ModelerScene.get().selection;
            if (sel instanceof Selection.CubeSelection cs && cs.cube() == cube) {
                ModelerScene.get().selection = null;
            }
        }
    }

    /**
     * Bone removal from a parent bone's children list. The bone's own subtree (its child bones and cubes) stays intact
     * inside the bone object, so reinserting restores the whole subtree as it was at delete time.
     */
    record BoneRemoveAction(
        String typeId,
        String description,
        long timestamp,
        ModelerBone parent,
        ModelerBone bone,
        int index
    ) implements ModelerAction {

        @Override
        public void undo() {
            if (!parent.children.contains(bone)) {
                var target = Math.min(Math.max(0, index), parent.children.size());
                parent.children.add(target, bone);
                bone.parent = parent;
            }
            ModelerScene.get().selection = new Selection.BoneSelection(bone);
        }

        @Override
        public void redo() {
            parent.children.remove(bone);
            var sel = ModelerScene.get().selection;
            if (sel instanceof Selection.BoneSelection bs && bs.bone() == bone) {
                ModelerScene.get().selection = null;
            }
        }
    }
}
