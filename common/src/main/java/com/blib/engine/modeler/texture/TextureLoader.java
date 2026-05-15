package com.blib.engine.modeler.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Loads PNGs from arbitrary disk paths into runtime {@link DynamicTexture} instances and registers them with
 * Minecraft's {@code TextureManager} under a unique {@link ResourceLocation}. PNG only — {@link NativeImage#read} is
 * the only decoder available without pulling in an extra JPG dependency, which is out of v1 scope for the textures
 * panel.
 */
@ApiStatus.Internal
public final class TextureLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextureLoader.class);

    private static final String NAMESPACE = "blib";

    private static final String PATH_PREFIX = "modeler/textures/";

    private TextureLoader() {}

    /**
     * Read {@code path} as a PNG, upload to a {@link DynamicTexture}, register under a freshly minted
     * {@link ResourceLocation}, and return the {@link LoadedTexture} bundle. Returns null (with a logged warning) on
     * any IO or decode failure so the panel caller can keep going instead of crashing the engine.
     */
    public static @Nullable LoadedTexture loadFromDisk(Path path) {
        var image = readImage(path);
        if (image == null) {
            return null;
        }

        var fileName = path.getFileName().toString();
        return register(fileName, path, image);
    }

    /** Reload the pixels of an existing runtime texture from its original source path. */
    public static boolean reloadFromDisk(LoadedTexture target) {
        var image = readImage(target.sourcePath());
        if (image == null) {
            return false;
        }
        target.texture().setPixels(image);
        target.texture().upload();
        return true;
    }

    private static @Nullable NativeImage readImage(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            return NativeImage.read(in);
        } catch (IOException e) {
            LOGGER.warn("TextureLoader: failed to read {}: {}", path, e.getMessage());
            return null;
        } catch (RuntimeException e) {
            // NativeImage.read wraps decode errors in unchecked exceptions for non-PNG inputs.
            LOGGER.warn("TextureLoader: not a valid PNG at {}: {}", path, e.getMessage());
            return null;
        }
    }

    /**
     * Create a second runtime texture from the current in-memory pixels of {@code source}. This preserves modeler paint
     * edits that may not have been written back to disk.
     */
    public static @Nullable LoadedTexture duplicate(LoadedTexture source) {
        var pixels = source.texture().getPixels();
        if (pixels == null) {
            LOGGER.warn("TextureLoader: cannot duplicate {} because its pixels are unavailable", source.displayName());
            return null;
        }

        var copy = new NativeImage(pixels.getWidth(), pixels.getHeight(), false);
        copy.copyFrom(pixels);
        return register(source.displayName(), source.sourcePath(), copy);
    }

    private static LoadedTexture register(String displayName, Path sourcePath, NativeImage image) {
        var dynamic = new DynamicTexture(image);
        var id = ResourceLocation.fromNamespaceAndPath(
            NAMESPACE,
            PATH_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        );
        Minecraft.getInstance().getTextureManager().register(id, dynamic);
        return new LoadedTexture(displayName, sourcePath, id, dynamic);
    }

    /**
     * Drop a previously-loaded texture: free its GPU memory and unregister it from the texture manager. Safe to call
     * twice (the second {@code release}/{@code close} pair is a no-op in MC 1.21).
     */
    public static void release(LoadedTexture loaded) {
        Minecraft.getInstance().getTextureManager().release(loaded.textureId());
        loaded.texture().close();
    }

}
