package com.blib.internal.common.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Filesystem helpers for the engine workspace's "Save Pool" feature. All edits land in an auto-managed
 * {@code blib_engine} datapack at {@code <world>/datapacks/blib_engine/} — vanilla's pack-overlay rules mean files
 * here override the originals when both packs are loaded, so we can safely edit vanilla pools without ever writing
 * to the read-only vanilla JAR.
 * <p>
 * The pack is created on first save (folder + {@code pack.mcmeta}) and added to the world's selected pack ids by
 * the calling handler so subsequent reloads pick it up. Pack format derives from the running MC version's
 * {@link SharedConstants} so we don't drift on engine updates.
 */
@ApiStatus.Internal
public final class PoolEditorSaveIO {

    public static final String PACK_NAME = "blib_engine";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private PoolEditorSaveIO() {}

    /**
     * Write {@code json} as the pool definition for {@code poolId} into the auto-managed pack. Creates the pack
     * folder + {@code pack.mcmeta} on first call. Returns the full file path written, for logging / verification.
     */
    public static Path writePool(MinecraftServer server, ResourceLocation poolId, JsonElement json) throws IOException {
        var packRoot = ensureBlibEnginePack(server);
        var fileRel = "data/" + poolId.getNamespace() + "/worldgen/template_pool/" + poolId.getPath() + ".json";
        var filePath = packRoot.resolve(fileRel);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, GSON.toJson(json));
        return filePath;
    }

    /**
     * Returns the path to the {@code blib_engine} datapack folder, creating the folder + {@code pack.mcmeta} on
     * first call. Idempotent: subsequent calls just return the path.
     */
    public static Path ensureBlibEnginePack(MinecraftServer server) throws IOException {
        var packRoot = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(PACK_NAME);
        if (Files.notExists(packRoot)) {
            Files.createDirectories(packRoot);
        }
        var meta = packRoot.resolve("pack.mcmeta");
        if (Files.notExists(meta)) {
            // SharedConstants picks the pack format matching the running MC version — old worlds opened on a newer
            // engine will see the new format, but we never overwrite an existing meta, so worlds with stale formats
            // keep loading (vanilla logs a warning but accepts the pack).
            var packFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
            var content = """
                {
                  "pack": {
                    "pack_format": %d,
                    "description": "BLib engine workspace — auto-managed datapack for in-game pool edits"
                  }
                }
                """.formatted(packFormat);
            Files.writeString(meta, content);
        }
        return packRoot;
    }
}
