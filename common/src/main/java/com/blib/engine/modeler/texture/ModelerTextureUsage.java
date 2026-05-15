package com.blib.engine.modeler.texture;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;

@ApiStatus.Internal
public final class ModelerTextureUsage {

    private ModelerTextureUsage() {}

    public static int faceUsageCount(ModelerScene scene, LoadedTexture texture) {
        return faceUsageCount(scene.root, texture);
    }

    public static boolean usesTexture(@Nullable LoadedTexture texture, ModelerCube.FaceUv uv) {
        if (texture == null || texture.sourceResource() == null || uv.textureSource() == null) {
            return false;
        }
        return texture.sourceResource().equals(uv.textureSource());
    }

    private static int faceUsageCount(ModelerBone bone, LoadedTexture texture) {
        var count = 0;
        for (var cube : bone.cubes) {
            for (var uv : cube.faceUvs.values()) {
                if (usesTexture(texture, uv)) {
                    count++;
                }
            }
        }
        for (var child : bone.children) {
            count += faceUsageCount(child, texture);
        }
        return count;
    }
}
