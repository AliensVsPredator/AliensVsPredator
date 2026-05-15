package com.blib.engine.modeler;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Clipboard for modeler outliner objects. It stores snapshots rather than live scene references so Paste reproduces the
 * object as it looked when Copy was invoked, even if the source is edited or deleted afterward.
 */
@ApiStatus.Internal
public final class ModelerClipboard {

    private static @Nullable ModelerBone copiedBone;

    private ModelerClipboard() {}

    public static void copyBone(ModelerBone bone) {
        copiedBone = copyBoneTree(bone);
    }

    public static boolean hasCopiedBone() {
        return copiedBone != null;
    }

    public static @Nullable ModelerBone copiedBoneForPaste() {
        return copiedBone == null ? null : copyBoneTree(copiedBone);
    }

    public static ModelerBone copyBoneTree(ModelerBone source) {
        var copy = new ModelerBone(source.name, source.position, source.rotation, source.scale, source.pivot);
        for (var cube : source.cubes) {
            copy.cubes.add(copyCube(cube));
        }
        for (var child : source.children) {
            copy.addChild(copyBoneTree(child));
        }
        return copy;
    }

    private static ModelerCube copyCube(ModelerCube source) {
        var copy = new ModelerCube(source.name, source.origin, source.size, source.rotation, source.pivot, source.inflate);
        copy.blockElementRescale = source.blockElementRescale;
        copy.uvOriginU = source.uvOriginU;
        copy.uvOriginV = source.uvOriginV;
        copy.mirrorUv = source.mirrorUv;
        copy.hasPerFaceUv = source.hasPerFaceUv;
        copy.faceUvs.putAll(source.faceUvs);
        return copy;
    }
}
