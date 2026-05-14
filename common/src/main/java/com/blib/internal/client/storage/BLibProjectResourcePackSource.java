package com.blib.internal.client.storage;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import com.blib.internal.common.storage.EngineProjectIO;
import com.blib.internal.common.storage.ProjectInfo;

/**
 * Client-resource-pack counterpart to {@link com.blib.internal.common.storage.BLibProjectPackSource}. Exposes each BLib
 * project's {@code resourcepack/} subfolder to vanilla's client-side
 * {@link net.minecraft.server.packs.repository.PackRepository}, so JSON the modeler writes there (item-renderer
 * configs, future client assets) becomes a real resource pack the user can enable/disable in the Resource Packs screen
 * and that participates in {@code Minecraft.reloadResourcePacks()}.
 * <p>
 * Pack id format: {@code blib_project/<name>} — same as the data-pack side. The two sources feed distinct
 * {@code PackRepository} instances (server-data vs client-resources), so the id collision is intentional and helps the
 * user mentally pair them.
 */
@ApiStatus.Internal
public final class BLibProjectResourcePackSource implements RepositorySource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibProjectResourcePackSource.class);

    private static final PackSelectionConfig SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);

    private final Path projectsRoot;

    public BLibProjectResourcePackSource(Path projectsRoot) {
        this.projectsRoot = projectsRoot;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (!Files.isDirectory(projectsRoot)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectsRoot)) {
            for (var entry : stream) {
                tryLoad(entry, consumer);
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] BLibProjectResourcePackSource: failed to scan {}", projectsRoot, e);
        }
    }

    /**
     * Try to mount {@code <project>/resourcepack/} as a client-resources pack. Silently skipped when the project hasn't
     * created the resourcepack subdir yet — existing projects from before the resource-pack feature don't need to be
     * migrated, they just don't contribute a client pack until something writes into them.
     */
    private void tryLoad(Path projectDir, Consumer<Pack> consumer) {
        if (!Files.isDirectory(projectDir)) {
            return;
        }

        if (!Files.isRegularFile(projectDir.resolve(ProjectInfo.MARKER_FILE_NAME))) {
            return;
        }

        var resourcepackRoot = projectDir.resolve(EngineProjectIO.RESOURCEPACK_SUBDIR);

        if (!Files.isRegularFile(resourcepackRoot.resolve("pack.mcmeta"))) {
            return;
        }

        var name = projectDir.getFileName().toString();
        var packId = EngineProjectIO.PACK_ID_PREFIX + name;
        var location = new PackLocationInfo(packId, Component.literal(name), PackSource.WORLD, Optional.empty());
        var supplier = new PathPackResources.PathResourcesSupplier(resourcepackRoot);
        var pack = Pack.readMetaAndCreate(location, supplier, PackType.CLIENT_RESOURCES, SELECTION_CONFIG);

        if (pack == null) {
            LOGGER.warn("[BLib] BLibProjectResourcePackSource: project '{}' failed pack metadata read; skipping", name);
            return;
        }

        consumer.accept(pack);
    }
}
