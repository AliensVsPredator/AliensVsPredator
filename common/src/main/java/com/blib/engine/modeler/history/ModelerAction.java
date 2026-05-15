package com.blib.engine.modeler.history;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.engine.gizmo.BLibItemTransformOverrides;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.texture.TextureEditorState;
import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Sealed root of the engine workspace's client-side authoring undo/redo entries. Every reversible local gesture
 * (modeler gizmo transform, inspector edit, texture paint stroke, texture selection change, etc.) produces one instance
 * which {@link ModelerActionHistory} stores so Ctrl+Z / Ctrl+Y can step through them in order.
 * <p>
 * Concrete variants are records carrying the state needed to round-trip the change. References to bones/cubes are held
 * by identity — when an undo restores a previously-deleted bone/cube, it's the same heap instance that other actions
 * still reference, so layered undo+redo across multiple actions stays coherent.
 */
@ApiStatus.Internal
public sealed interface ModelerAction permits ModelerAction.CubeMementoAction, ModelerAction.BoneMementoAction, ModelerAction.CubeInsertAction, ModelerAction.CubeRemoveAction, ModelerAction.BoneRemoveAction, ModelerAction.ItemTransformMementoAction, ModelerAction.TexturePixelsAction, ModelerAction.TextureSelectionAction, ModelerAction.CompositeAction {

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
        double inflate,
        double uvOriginU,
        double uvOriginV,
        boolean mirrorUv,
        boolean hasPerFaceUv,
        Map<ModelerCube.Face, ModelerCube.FaceUv> faceUvs
    ) {

        public static CubeMemento of(ModelerCube cube) {
            return new CubeMemento(
                cube.name,
                cube.origin,
                cube.size,
                cube.rotation,
                cube.pivot,
                cube.inflate,
                cube.uvOriginU,
                cube.uvOriginV,
                cube.mirrorUv,
                cube.hasPerFaceUv,
                Map.copyOf(cube.faceUvs)
            );
        }

        public void apply(ModelerCube cube) {
            cube.name = name;
            cube.origin = origin;
            cube.size = size;
            cube.rotation = rotation;
            cube.pivot = pivot;
            cube.inflate = inflate;
            cube.uvOriginU = uvOriginU;
            cube.uvOriginV = uvOriginV;
            cube.mirrorUv = mirrorUv;
            cube.hasPerFaceUv = hasPerFaceUv;
            cube.faceUvs.clear();
            cube.faceUvs.putAll(faceUvs);
        }

        public boolean differsFrom(CubeMemento other) {
            return !name.equals(other.name)
                || !origin.equals(other.origin)
                || !size.equals(other.size)
                || !rotation.equals(other.rotation)
                || !pivot.equals(other.pivot)
                || inflate != other.inflate
                || uvOriginU != other.uvOriginU
                || uvOriginV != other.uvOriginV
                || mirrorUv != other.mirrorUv
                || hasPerFaceUv != other.hasPerFaceUv
                || !faceUvs.equals(other.faceUvs);
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

    /**
     * Snapshot of a {@link BLibTransform}'s four mutable {@link Vector3f}s, used as the before/after halves of
     * {@link ItemTransformMementoAction}. Defensive-copies the inputs since {@link BLibTransform}'s vectors are mutable
     * references shared across {@link BLibItemTransformOverrides} reads.
     */
    record ItemTransformMemento(
        Vector3f translation,
        Vector3f rotation,
        Vector3f scale,
        Vector3f pivot
    ) {

        public static ItemTransformMemento of(BLibTransform transform) {
            return new ItemTransformMemento(
                new Vector3f(transform.translation()),
                new Vector3f(transform.rotation()),
                new Vector3f(transform.scale()),
                new Vector3f(transform.pivot())
            );
        }

        public BLibTransform toTransform() {
            return new BLibTransform(new Vector3f(translation), new Vector3f(rotation), new Vector3f(scale), new Vector3f(pivot));
        }

        public boolean differsFrom(ItemTransformMemento other) {
            return !translation.equals(other.translation)
                || !rotation.equals(other.rotation)
                || !scale.equals(other.scale)
                || !pivot.equals(other.pivot);
        }
    }

    /**
     * Field-level memento for {@link BLibTransform} edits made via the Modeler's item config — covers gizmo drags
     * (translate/rotate/scale/pivot) in preview mode and Inspector text-field commits. The slot the transform lives in
     * is identified by {@code (itemId, mode, context)} or {@code (itemId, mode, wallFixed)}; undo / redo apply the
     * corresponding {@link BLibItemTransformOverrides#set} or {@link BLibItemTransformOverrides#setWallFixed}.
     * <p>
     * {@link #wallFixed} disambiguates the regular {@code FIXED} slot from the wall-fixed slot; when true,
     * {@link #context} is implicitly {@link ItemDisplayContext#FIXED} and the wall-fixed setter is used instead.
     */
    record ItemTransformMementoAction(
        String typeId,
        String description,
        long timestamp,
        ResourceLocation itemId,
        BLibItemTransformMode mode,
        ItemDisplayContext context,
        boolean wallFixed,
        ItemTransformMemento before,
        ItemTransformMemento after
    ) implements ModelerAction {

        @Override
        public void undo() {
            applyTransform(before);
        }

        @Override
        public void redo() {
            applyTransform(after);
        }

        private void applyTransform(ItemTransformMemento memento) {
            var transform = memento.toTransform();
            if (wallFixed) {
                BLibItemTransformOverrides.setWallFixed(itemId, mode, transform);
            } else {
                BLibItemTransformOverrides.set(itemId, mode, context, transform);
            }
        }
    }

    /**
     * Pixel snapshot for runtime-loaded texture edits. Stores the texture dimensions alongside a native-order pixel
     * copy so undo/redo can safely no-op if the underlying dynamic texture has been replaced with a different-sized
     * image.
     */
    record TexturePixelsMemento(
        int width,
        int height,
        int[] pixels
    ) {

        public static TexturePixelsMemento of(NativeImage image) {
            var width = image.getWidth();
            var height = image.getHeight();
            var copy = new int[width * height];
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    copy[y * width + x] = image.getPixelRGBA(x, y);
                }
            }
            return new TexturePixelsMemento(width, height, copy);
        }

        public void apply(LoadedTexture texture) {
            var image = texture.texture().getPixels();
            if (image == null || image.getWidth() != width || image.getHeight() != height) {
                return;
            }
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    image.setPixelRGBA(x, y, pixels[y * width + x]);
                }
            }
            texture.texture().upload();
        }

        public boolean differsFrom(TexturePixelsMemento other) {
            if (width != other.width || height != other.height) {
                return true;
            }
            for (var i = 0; i < pixels.length; i++) {
                if (pixels[i] != other.pixels[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Pixel-level texture edit: pencil strokes and bucket fills both restore the whole edited image. */
    record TexturePixelsAction(
        String typeId,
        String description,
        long timestamp,
        LoadedTexture target,
        TexturePixelsMemento before,
        TexturePixelsMemento after
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

    /** Region-selection edit for the texture viewport. Selection state is part of the editable texture workflow. */
    record TextureSelectionAction(
        String typeId,
        String description,
        long timestamp,
        @Nullable TextureEditorState.Selection before,
        @Nullable TextureEditorState.Selection after
    ) implements ModelerAction {

        @Override
        public void undo() {
            apply(before);
        }

        @Override
        public void redo() {
            apply(after);
        }

        private static void apply(@Nullable TextureEditorState.Selection selection) {
            if (selection == null) {
                TextureEditorState.clearSelection();
            } else {
                TextureEditorState.setSelection(selection.x0(), selection.y0(), selection.x1Exclusive(), selection.y1Exclusive());
            }
        }
    }

    /**
     * Bundle of inner actions applied + reverted as a unit. Used by gestures that touch many bones / cubes at once
     * (whole-model flips, batched property edits) so undo and redo round-trip the entire gesture rather than walking
     * through every individual field change. Children apply in list order on redo; undo reverses so layered changes
     * unwind correctly.
     */
    record CompositeAction(
        String typeId,
        String description,
        long timestamp,
        List<ModelerAction> children
    ) implements ModelerAction {

        @Override
        public void undo() {
            for (var i = children.size() - 1; i >= 0; i--) {
                children.get(i).undo();
            }
        }

        @Override
        public void redo() {
            for (var child : children) {
                child.redo();
            }
        }
    }
}
