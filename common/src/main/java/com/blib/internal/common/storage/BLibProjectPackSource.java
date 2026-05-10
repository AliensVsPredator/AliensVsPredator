package com.blib.internal.common.storage;

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

/**
 * {@link RepositorySource} that exposes BLib projects' {@code datapack/} subfolders to vanilla's
 * {@link net.minecraft.server.packs.repository.PackRepository}. Projects live globally under
 * {@code <gameDir>/blib/projects/}; this source makes each project's datapack appear as an in-memory pack the world can
 * select and reload, without copying any files into the world directory.
 * <p>
 * Pack id format: {@code blib_project/<name>}. Vanilla's {@code FolderRepositorySource} uses {@code file/<name>}, so
 * the prefix difference keeps our packs from colliding with hand-installed packs that happen to share a name.
 * <p>
 * Injected into
 * {@link net.minecraft.server.packs.repository.ServerPacksSource#createPackRepository(Path, net.minecraft.world.level.validation.DirectoryValidator)}
 * via {@code MixinServerPacksSource}, so the world's pack repo sees this source alongside vanilla's built-in and folder
 * sources.
 */
@ApiStatus.Internal
public final class BLibProjectPackSource implements RepositorySource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibProjectPackSource.class);

    /**
     * Default selection config: not required, top-of-stack default position, not fixed. Matching the {@code WORLD}-
     * source flavor — users should be able to enable/disable a project's pack via vanilla's pack selection UI just like
     * any other world datapack.
     */
    private static final PackSelectionConfig SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);

    private final Path projectsRoot;

    public BLibProjectPackSource(Path projectsRoot) {
        this.projectsRoot = projectsRoot;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (!Files.isDirectory(projectsRoot)) {
            // First run before any project has been created — vanilla's pack repo gets nothing from us, no error.
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectsRoot)) {
            for (var entry : stream) {
                tryLoad(entry, consumer);
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] BLibProjectPackSource: failed to scan {}", projectsRoot, e);
        }
    }

    /**
     * Try to materialize one project's datapack as a {@link Pack}. Failures are isolated per-project — a malformed
     * marker, missing datapack subfolder, or unreadable {@code pack.mcmeta} just skips that one project; the rest still
     * load. Anything we skip is logged at WARN so the user can spot problems via the log.
     */
    private void tryLoad(Path projectDir, Consumer<Pack> consumer) {
        if (!Files.isDirectory(projectDir)) {
            return;
        }
        if (!Files.isRegularFile(projectDir.resolve(ProjectInfo.MARKER_FILE_NAME))) {
            // Not a BLib project — skip silently. The directory might be something else the user dropped here.
            return;
        }
        var datapackRoot = projectDir.resolve(EngineProjectIO.DATAPACK_SUBDIR);
        if (!Files.isRegularFile(datapackRoot.resolve("pack.mcmeta"))) {
            LOGGER.warn(
                "[BLib] BLibProjectPackSource: project '{}' has marker but missing or invalid {}/pack.mcmeta; skipping",
                projectDir.getFileName(),
                EngineProjectIO.DATAPACK_SUBDIR
            );
            return;
        }

        var name = projectDir.getFileName().toString();
        var packId = EngineProjectIO.PACK_ID_PREFIX + name;
        var location = new PackLocationInfo(packId, Component.literal(name), PackSource.WORLD, Optional.empty());
        var supplier = new PathPackResources.PathResourcesSupplier(datapackRoot);
        var pack = Pack.readMetaAndCreate(location, supplier, PackType.SERVER_DATA, SELECTION_CONFIG);
        if (pack == null) {
            LOGGER.warn("[BLib] BLibProjectPackSource: project '{}' failed pack metadata read; skipping", name);
            return;
        }
        consumer.accept(pack);
    }
}
