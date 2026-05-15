package com.blib.engine.modeler.texture;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * One PNG imported into the modeler at runtime. The {@link #textureId} is already registered with the
 * {@code TextureManager} so any GL draw that binds it (3D viewport, UV map overlay, list thumbnail) renders the pixels.
 * {@link #texture} is kept so we can {@code close()} the GPU resource when the scene resets or the engine shuts down.
 */
@ApiStatus.Internal
public record LoadedTexture(
    String displayName,
    @Nullable Path sourcePath,
    @Nullable ResourceLocation sourceResource,
    ResourceLocation textureId,
    DynamicTexture texture
) {

    public LoadedTexture(String displayName, Path sourcePath, ResourceLocation textureId, DynamicTexture texture) {
        this(displayName, sourcePath, null, textureId, texture);
    }

    public LoadedTexture(String displayName, ResourceLocation sourceResource, ResourceLocation textureId, DynamicTexture texture) {
        this(displayName, null, sourceResource, textureId, texture);
    }

    public boolean isFileBacked() {
        return sourcePath != null;
    }

    public boolean isResourceBacked() {
        return sourceResource != null;
    }
}
