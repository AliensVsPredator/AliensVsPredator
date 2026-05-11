package com.blib.internal.common.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import com.blib.api.BLibAPI;

/**
 * Filesystem helpers for the engine workspace's project system. Each project is a multi-asset directory under
 * {@code <gameDir>/blib/projects/<name>/} marked by a {@link ProjectInfo#MARKER_FILE_NAME} file at the project root.
 * The marker is what distinguishes BLib-managed projects from arbitrary directories in the same location — directories
 * without it are invisible to {@link #listProjects} and refused by {@link #deleteProject} so we never touch folders we
 * didn't create.
 * <p>
 * Project layout:
 *
 * <pre>
 *   &lt;gameDir&gt;/blib/projects/&lt;name&gt;/
 *   ├── blib_project.json     # metadata (name, description, createdAt, mcVersion)
 *   ├── datapack/
 *   │   ├── pack.mcmeta
 *   │   └── data/&lt;ns&gt;/...    # worldgen JSON written by the engine
 *   └── captures/             # user-triggered .nbt block snapshots (future)
 * </pre>
 * <p>
 * Projects live in the game directory (not the world's datapack folder) so they survive world deletes and can be reused
 * across saves. The active world sees a project's datapack via {@code BLibProjectPackSource} — a custom
 * {@link net.minecraft.server.packs.repository.RepositorySource} injected into vanilla's pack repo. Pack id format is
 * {@code blib_project/<name>}.
 * <p>
 * This class is the single seam between the engine and disk: server handlers go through {@link #writePoolJson} (or the
 * more generic {@link #writeDataJson}) for every datapack change, and {@link #reloadProject} is the only way the live
 * registry sees those changes. Capture IO primitives ({@link #writeCaptureNbt} etc.) exist for the future
 * explicit-capture tool but are not auto-invoked by anything yet — file IO must always be a deliberate user action.
 */
@ApiStatus.Internal
public final class EngineProjectIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineProjectIO.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Subdirectory under {@code <gameDir>/blib/} where projects live. */
    private static final String PROJECTS_SUBDIR = "blib/projects";

    /** Subdirectory inside a project that holds the vanilla datapack tree. */
    public static final String DATAPACK_SUBDIR = "datapack";

    /** Subdirectory inside a project that holds user-triggered NBT block captures. */
    public static final String CAPTURES_SUBDIR = "captures";

    /** Pack id prefix used by {@code BLibProjectPackSource}. Kept here so {@link #findPackId} can match it. */
    public static final String PACK_ID_PREFIX = "blib_project/";

    /**
     * Project folder names must match this. Lowercase ASCII alphanumeric plus underscore / hyphen, 1–32 chars. The
     * character set mirrors what vanilla's pack-id parser accepts without escaping; the length cap keeps status-bar /
     * picker-card layouts predictable.
     */
    private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");

    /**
     * Names that would collide with vanilla's special pack ids or with mod-loaded resources. Refusing them at create
     * time avoids a class of confusing "my project disappeared" reports where the user picked a name vanilla quietly
     * co-opts.
     */
    private static final Set<String> RESERVED_NAMES = Set.of("vanilla", "mod_resources", "fabric", "neoforge");

    private EngineProjectIO() {}

    /** {@code <gameDir>/blib/projects/}. Resolved via {@link BLibAPI#getGameDirectory()} so loader-agnostic. */
    public static Path projectsRoot() {
        return BLibAPI.getGameDirectory().resolve(PROJECTS_SUBDIR);
    }

    /** {@code <gameDir>/blib/projects/<name>/}. Does not create the directory. */
    public static Path projectRoot(String name) {
        return projectsRoot().resolve(name);
    }

    /** {@code <gameDir>/blib/projects/<name>/datapack/}. The datapack vanilla actually loads. */
    public static Path datapackRoot(String name) {
        return projectRoot(name).resolve(DATAPACK_SUBDIR);
    }

    /** {@code <gameDir>/blib/projects/<name>/captures/}. Future block-snapshot storage. */
    public static Path captureDir(String name) {
        return projectRoot(name).resolve(CAPTURES_SUBDIR);
    }

    /** True if {@code projectDir} contains a {@link ProjectInfo#MARKER_FILE_NAME} file at its root. */
    public static boolean isBLibProject(Path projectDir) {
        return Files.isRegularFile(projectDir.resolve(ProjectInfo.MARKER_FILE_NAME));
    }

    /**
     * Validate a project name without touching the filesystem. Throws {@link IllegalArgumentException} with a
     * user-facing message on rejection — the picker UI reuses these messages for live validation feedback.
     */
    public static void validateProjectName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Project name is empty");
        }
        if (!PROJECT_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Project name must be 1–32 lowercase letters, digits, '_' or '-'");
        }
        if (RESERVED_NAMES.contains(name)) {
            throw new IllegalArgumentException("Project name '" + name + "' is reserved");
        }
    }

    /**
     * Scan {@code <gameDir>/blib/projects/} for folders containing the BLib project marker and return them sorted by
     * name. Empty if the directory doesn't exist yet (e.g. fresh install). Skips entries that fail to parse the marker
     * so one corrupt project doesn't hide its siblings; the broken project is logged at WARN.
     */
    public static List<ProjectInfo> listProjects() {
        var root = projectsRoot();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        var out = new ArrayList<ProjectInfo>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (var entry : stream) {
                if (!Files.isDirectory(entry)) {
                    continue;
                }
                if (!isBLibProject(entry)) {
                    continue;
                }
                try {
                    out.add(ProjectInfo.readMarker(entry));
                } catch (IOException e) {
                    LOGGER.warn("[BLib] listProjects: failed to read marker at {}; skipping", entry, e);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] listProjects: failed to scan {}", root, e);
            return List.of();
        }
        out.sort(Comparator.comparing(ProjectInfo::name));
        return out;
    }

    /**
     * Create a new project at {@code <gameDir>/blib/projects/<name>/}: writes the marker at the project root, creates
     * the {@code datapack/} subfolder with a {@code pack.mcmeta}, and creates an empty {@code captures/} placeholder so
     * the layout is stable even before any captures are saved.
     * <p>
     * Failure modes:
     * <ul>
     * <li>{@link IllegalArgumentException} — name fails {@link #validateProjectName}.</li>
     * <li>{@link IOException} — folder already exists (whether a BLib project or some other directory). Distinguishing
     * case is in the message so the picker can show "already a project" vs "name collides with an existing folder". We
     * never silently take over a non-BLib folder.</li>
     * </ul>
     */
    public static ProjectInfo createProject(String name, String description) throws IOException {
        validateProjectName(name);
        var projectRoot = projectRoot(name);
        if (Files.exists(projectRoot)) {
            if (isBLibProject(projectRoot)) {
                throw new IOException("Project '" + name + "' already exists");
            }
            throw new IOException("A non-BLib folder named '" + name + "' already exists at " + projectRoot);
        }
        Files.createDirectories(projectRoot);
        var datapackRoot = projectRoot.resolve(DATAPACK_SUBDIR);
        Files.createDirectories(datapackRoot);
        Files.createDirectories(projectRoot.resolve(CAPTURES_SUBDIR));
        writePackMcmeta(datapackRoot, description);
        var info = new ProjectInfo(
            name,
            description == null ? "" : description,
            Instant.now().toString(),
            SharedConstants.getCurrentVersion().getName(),
            ProjectInfo.CURRENT_VERSION
        );
        Files.writeString(projectRoot.resolve(ProjectInfo.MARKER_FILE_NAME), GSON.toJson(info.toJson()));
        LOGGER.info("[BLib] createProject: '{}' at {}", name, projectRoot);
        return info;
    }

    /**
     * Remove the project folder. Order matters: unselect-from-pack-repo + reloadResources is done <em>before</em> the
     * actual delete so vanilla doesn't see a dangling reference to a folder that's mid-disappearing. Refuses to delete
     * a folder that doesn't have the BLib marker — we never delete folders we didn't create.
     */
    public static void deleteProject(MinecraftServer server, String name) throws IOException {
        validateProjectName(name);
        var projectRoot = projectRoot(name);
        if (!Files.exists(projectRoot)) {
            return;
        }
        if (!isBLibProject(projectRoot)) {
            throw new IOException("Refusing to delete '" + name + "': missing " + ProjectInfo.MARKER_FILE_NAME + " marker");
        }

        // Unselect from the pack repo, reload resources, *then* nuke the folder. Doing it in the other order
        // leaves vanilla holding a Reference to a now-missing pack and prints an error to the log on the next
        // reload. The reload is synchronous-ish (CompletableFuture-backed) — by the time it returns, the live
        // registry has already re-imported without our pack.
        var packRepo = server.getPackRepository();
        var selected = new ArrayList<>(packRepo.getSelectedIds());
        var packId = findPackId(server, name);
        if (packId != null && selected.remove(packId)) {
            server.reloadResources(selected);
        }

        deleteRecursive(projectRoot);
        LOGGER.info("[BLib] deleteProject: removed '{}' from {}", name, projectRoot);
    }

    /**
     * Read the pool JSON for {@code poolId} from {@code projectName}'s datapack, if present. Empty if the project has
     * not yet authored an override for that pool.
     */
    public static Optional<JsonElement> readPoolJson(String projectName, ResourceLocation poolId) {
        var path = datapackRoot(projectName).resolve(poolJsonRelPath(poolId));
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(JsonParser.parseString(Files.readString(path)));
        } catch (IOException e) {
            LOGGER.warn("[BLib] readPoolJson: failed to read {}", path, e);
            return Optional.empty();
        }
    }

    /** Write the pool JSON for {@code poolId} into {@code projectName}'s datapack. Creates parent dirs as needed. */
    public static Path writePoolJson(String projectName, ResourceLocation poolId, JsonElement json) throws IOException {
        return writeDataJson(projectName, poolJsonRelPath(poolId), json);
    }

    /**
     * Generic data-tree write for {@code projectName} — the seam future panels (biomes, loot, etc.) reuse without
     * needing a registry-aware abstraction. {@code relPath} is the pack-relative path (e.g.
     * {@code data/<ns>/worldgen/template_pool/<path>.json}); the file lands at {@code <project>/datapack/<relPath>}.
     */
    public static Path writeDataJson(String projectName, String relPath, JsonElement json) throws IOException {
        var projectRoot = projectRoot(projectName);
        if (!isBLibProject(projectRoot)) {
            throw new IOException("Project '" + projectName + "' does not exist");
        }
        var filePath = projectRoot.resolve(DATAPACK_SUBDIR).resolve(relPath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, GSON.toJson(json));
        return filePath;
    }

    /**
     * NBT counterpart to {@link #writeDataJson} — writes a {@link CompoundTag} into the project's datapack tree at
     * {@code <project>/datapack/<relPath>}. Used by the block-capture tool for jigsaw sub-pieces (saved at
     * {@code data/<ns>/structure/<name>.nbt}) so vanilla's structure manager can load them directly.
     */
    public static Path writeDataNbt(String projectName, String relPath, CompoundTag tag) throws IOException {
        var projectRoot = projectRoot(projectName);
        if (!isBLibProject(projectRoot)) {
            throw new IOException("Project '" + projectName + "' does not exist");
        }
        var filePath = projectRoot.resolve(DATAPACK_SUBDIR).resolve(relPath);
        Files.createDirectories(filePath.getParent());
        NbtIo.writeCompressed(tag, filePath);
        return filePath;
    }

    /** Pack-relative path for a structure NBT: {@code data/<namespace>/structure/<structurePath>.nbt}. */
    public static String structureRelPath(String namespace, String structurePath) {
        return "data/" + namespace + "/structure/" + structurePath + ".nbt";
    }

    /**
     * Persist a block-snapshot {@link CompoundTag} to {@code <project>/captures/<captureName>.nbt}. The future "Capture
     * Block(s)" tool is the intended caller; nothing in the engine invokes this automatically — file IO is always an
     * explicit user-triggered action.
     */
    public static Path writeCaptureNbt(String projectName, String captureName, CompoundTag tag) throws IOException {
        validateCaptureName(captureName);
        var projectRoot = projectRoot(projectName);
        if (!isBLibProject(projectRoot)) {
            throw new IOException("Project '" + projectName + "' does not exist");
        }
        var dir = projectRoot.resolve(CAPTURES_SUBDIR);
        Files.createDirectories(dir);
        var path = dir.resolve(captureName + ".nbt");
        NbtIo.writeCompressed(tag, path);
        return path;
    }

    /** Read a capture by name. Empty if the project / capture doesn't exist or the file fails to parse. */
    public static Optional<CompoundTag> readCaptureNbt(String projectName, String captureName) {
        try {
            validateCaptureName(captureName);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        var path = projectRoot(projectName).resolve(CAPTURES_SUBDIR).resolve(captureName + ".nbt");
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
        } catch (IOException e) {
            LOGGER.warn("[BLib] readCaptureNbt: failed to read {}", path, e);
            return Optional.empty();
        }
    }

    /** List capture names (without the {@code .nbt} extension) under the project's captures folder. Sorted. */
    public static List<String> listCaptureNames(String projectName) {
        var dir = projectRoot(projectName).resolve(CAPTURES_SUBDIR);
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        var out = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.nbt")) {
            for (var entry : stream) {
                if (!Files.isRegularFile(entry)) {
                    continue;
                }
                var fileName = entry.getFileName().toString();
                if (fileName.endsWith(".nbt")) {
                    out.add(fileName.substring(0, fileName.length() - 4));
                }
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] listCaptureNames: failed to scan {}", dir, e);
            return List.of();
        }
        out.sort(Comparator.naturalOrder());
        return out;
    }

    /** Delete a capture file. Returns true if a file existed and was deleted. */
    public static boolean deleteCapture(String projectName, String captureName) throws IOException {
        validateCaptureName(captureName);
        var path = projectRoot(projectName).resolve(CAPTURES_SUBDIR).resolve(captureName + ".nbt");
        return Files.deleteIfExists(path);
    }

    /**
     * List the project's authored pools — every {@code data/<ns>/worldgen/template_pool/<path>.json} under the
     * project's datapack tree. Walks each namespace's pool subtree recursively so nested paths (e.g.
     * {@code village/plains/houses}) appear as their full id. Sorted by namespaced id. Empty if the datapack folder is
     * missing or has no pools.
     */
    public static List<ResourceLocation> listProjectPools(String projectName) {
        return listProjectAssets(projectName, "worldgen/template_pool", ".json");
    }

    /**
     * List the project's authored structures — every {@code data/<ns>/structure/<path>.nbt} under the datapack tree.
     * Same recursive-walk semantics as {@link #listProjectPools}. Note these are <em>datapack-loadable</em> structures
     * (referenced by pools); the user-triggered NBT block snapshots in {@code captures/} are listed by
     * {@link #listCaptureNames} separately.
     */
    public static List<ResourceLocation> listProjectStructures(String projectName) {
        return listProjectAssets(projectName, "structure", ".nbt");
    }

    /**
     * Generic datapack-asset enumerator. Walks {@code data/<ns>/<categoryPath>/<...>/<file>.<extension>} under the
     * project's datapack root and returns each match as a {@code ResourceLocation(ns, relativePathWithoutExtension)}.
     * The recursive descent is bounded to the {@code categoryPath} subtree per namespace; we don't traverse upward so a
     * malformed datapack with weird symlinks can't escape the project root.
     */
    private static List<ResourceLocation> listProjectAssets(String projectName, String categoryPath, String extension) {
        var dataRoot = projectRoot(projectName).resolve(DATAPACK_SUBDIR).resolve("data");
        if (!Files.isDirectory(dataRoot)) {
            return List.of();
        }
        var out = new ArrayList<ResourceLocation>();
        try (DirectoryStream<Path> namespaces = Files.newDirectoryStream(dataRoot)) {
            for (var nsDir : namespaces) {
                if (!Files.isDirectory(nsDir)) {
                    continue;
                }
                var namespace = nsDir.getFileName().toString();
                var categoryRoot = nsDir.resolve(categoryPath);
                if (!Files.isDirectory(categoryRoot)) {
                    continue;
                }
                try (var stream = Files.walk(categoryRoot)) {
                    stream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(extension))
                        .forEach(p -> {
                            var rel = categoryRoot.relativize(p).toString().replace('\\', '/');
                            // Strip the extension. Length is bounded by isRegularFile + endsWith above.
                            var path = rel.substring(0, rel.length() - extension.length());
                            try {
                                out.add(ResourceLocation.fromNamespaceAndPath(namespace, path));
                            } catch (ResourceLocationException ignored) {
                                // Path contains characters vanilla refuses (uppercase, etc.). Skip silently — the
                                // datapack itself wouldn't be loadable, and listing it would just confuse the user.
                            }
                        });
                } catch (IOException e) {
                    LOGGER.warn("[BLib] listProjectAssets: failed to walk {}", categoryRoot, e);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] listProjectAssets: failed to scan {}", dataRoot, e);
            return List.of();
        }
        out.sort(Comparator.comparing(ResourceLocation::toString));
        return out;
    }

    /**
     * Delete the JSON file backing the given pool id under the project's datapack. Returns true iff a file existed and
     * was deleted. Refuses ids whose resolved path escapes the datapack root (defense against malformed packets).
     */
    public static boolean deleteProjectPool(String projectName, ResourceLocation poolId) throws IOException {
        return deleteProjectAsset(projectName, poolId, "worldgen/template_pool", ".json");
    }

    /**
     * Delete the NBT file backing the given structure id under the project's datapack. Same semantics as
     * {@link #deleteProjectPool}.
     */
    public static boolean deleteProjectStructure(String projectName, ResourceLocation structureId) throws IOException {
        return deleteProjectAsset(projectName, structureId, "structure", ".nbt");
    }

    private static boolean deleteProjectAsset(
        String projectName,
        ResourceLocation assetId,
        String categoryPath,
        String extension
    ) throws IOException {
        var datapackRoot = projectRoot(projectName).resolve(DATAPACK_SUBDIR).normalize();
        var target = datapackRoot
            .resolve("data")
            .resolve(assetId.getNamespace())
            .resolve(categoryPath)
            .resolve(assetId.getPath() + extension)
            .normalize();
        // Defense in depth: a malformed packet could craft an id whose path contains "../"; refuse to delete anything
        // outside the datapack tree even if vanilla's ResourceLocation parser somehow let it through.
        if (!target.startsWith(datapackRoot)) {
            throw new IOException("Refusing to delete '" + assetId + "': path escapes datapack root");
        }
        return Files.deleteIfExists(target);
    }

    /**
     * Ensure the project's pack is selected, then reload the pack repository and re-import all selected packs. Returns
     * the {@link CompletableFuture} produced by {@code server.reloadResources(...)} so callers can chain post-reload
     * work (sending success acks, clearing draft caches) onto its completion. The future completes once the registry
     * reflects on-disk state. Throws synchronously if the project doesn't exist (caller mis-routed) — silently
     * no-op'ing would mask a class of bugs.
     */
    public static CompletableFuture<Void> reloadProject(MinecraftServer server, String projectName) throws IOException {
        validateProjectName(projectName);
        var projectRoot = projectRoot(projectName);
        if (!isBLibProject(projectRoot)) {
            throw new IOException("Project '" + projectName + "' does not exist");
        }
        var packRepo = server.getPackRepository();
        packRepo.reload();
        var packId = findPackId(server, projectName);
        if (packId == null) {
            throw new IOException(
                "Project pack '" + projectName + "' not found in available ids after reload (available: " + packRepo.getAvailableIds() + ")"
            );
        }
        var selected = new ArrayList<>(packRepo.getSelectedIds());
        if (!selected.contains(packId)) {
            selected.add(packId);
        }
        return server.reloadResources(selected);
    }

    /**
     * Find the pack id (as the {@link net.minecraft.server.packs.repository.PackRepository} sees it) for our project.
     * Prefers the {@code blib_project/<name>} form produced by {@code BLibProjectPackSource}; falls back to the legacy
     * {@code file/<name>} (or bare-name) match in case a hand-installed datapack happens to share the project name.
     * Returns null if no match exists.
     */
    public static String findPackId(MinecraftServer server, String projectName) {
        var available = server.getPackRepository().getAvailableIds();
        var preferred = PACK_ID_PREFIX + projectName;
        if (available.contains(preferred)) {
            return preferred;
        }
        return available.stream()
            .filter(id -> id.equals(projectName) || id.endsWith("/" + projectName))
            .findFirst()
            .orElse(null);
    }

    private static String poolJsonRelPath(ResourceLocation poolId) {
        return "data/" + poolId.getNamespace() + "/worldgen/template_pool/" + poolId.getPath() + ".json";
    }

    private static void writePackMcmeta(Path datapackRoot, String description) throws IOException {
        var packFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
        var safeDescription = description == null || description.isEmpty()
            ? "BLib engine project"
            : description.replace("\"", "'");
        var content = """
            {
              "pack": {
                "pack_format": %d,
                "description": "%s"
              }
            }
            """.formatted(packFormat, safeDescription);
        Files.writeString(datapackRoot.resolve("pack.mcmeta"), content);
    }

    /**
     * Validate that {@code captureName} is a safe single-segment file name — no path separators, no traversal, length
     * cap to keep filenames sane. Less strict than project names since captures are user-named artifacts the engine UI
     * presents to humans, not pack ids.
     */
    private static void validateCaptureName(String captureName) {
        if (captureName == null || captureName.isEmpty()) {
            throw new IllegalArgumentException("Capture name is empty");
        }
        if (captureName.contains("/") || captureName.contains("\\") || captureName.contains("..")) {
            throw new IllegalArgumentException("Capture name must not contain path separators");
        }
        if (captureName.length() > 64) {
            throw new IllegalArgumentException("Capture name must be 1–64 characters");
        }
    }

    /**
     * Recursive directory removal. The JDK's {@link Files#walk} returns a stream we drain in reverse so child entries
     * are deleted before their parents. Failures are surfaced (not swallowed) so the caller — which has already updated
     * the pack repo — knows about partial deletes.
     */
    private static void deleteRecursive(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var stream = Files.walk(root)) {
            var paths = stream.sorted(Comparator.reverseOrder()).toList();
            for (var path : paths) {
                Files.delete(path);
            }
        }
    }
}
