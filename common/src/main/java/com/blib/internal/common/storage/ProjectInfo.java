package com.blib.internal.common.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parsed form of the {@code blib_project.json} marker file at the root of a BLib engine project. The presence of this
 * file is what distinguishes a BLib-managed project from an arbitrary directory: folders under
 * {@code <gameDir>/blib/projects/} that lack this marker are ignored by {@link EngineProjectIO#listProjects}.
 * <p>
 * The marker lives at the project root (alongside the {@code datapack/} and {@code captures/} subfolders), not inside
 * the datapack tree. The datapack subfolder has its own vanilla {@code pack.mcmeta}; this file is BLib-specific
 * project-level metadata.
 * <p>
 * Schema:
 *
 * <pre>{@code
 * { "name": "<folder name>", "description": "...", "createdAt": "<ISO-8601>",
 *   "mcVersion": "1.21.1", "blibProjectVersion": 1 }
 * }</pre>
 * <p>
 * {@code blibProjectVersion} is bumped when the schema changes so future BLib releases can migrate older projects in
 * place. {@code mcVersion} is recorded at create-time so a future "upgrade project" tool can detect cross-version
 * pack-format drift without re-reading {@code pack.mcmeta}.
 */
@ApiStatus.Internal
public record ProjectInfo(
    String name,
    String description,
    String createdAt,
    String mcVersion,
    int blibProjectVersion
) {

    public static final String MARKER_FILE_NAME = "blib_project.json";

    public static final int CURRENT_VERSION = 1;

    /**
     * Wire-format codec for transferring projects from the server's filesystem scan to the client's project picker.
     * Defined here so any future S2C/C2S payload can reuse it via {@code .asList()} without each packet redefining the
     * field-by-field encoding.
     */
    public static final StreamCodec<ProjectInfo> STREAM_CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        ProjectInfo::name,
        StreamCodecs.STRING_UTF8,
        ProjectInfo::description,
        StreamCodecs.STRING_UTF8,
        ProjectInfo::createdAt,
        StreamCodecs.STRING_UTF8,
        ProjectInfo::mcVersion,
        StreamCodecs.INT,
        ProjectInfo::blibProjectVersion,
        ProjectInfo::new
    );

    public JsonObject toJson() {
        var obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("description", description);
        obj.addProperty("createdAt", createdAt);
        obj.addProperty("mcVersion", mcVersion);
        obj.addProperty("blibProjectVersion", blibProjectVersion);
        return obj;
    }

    public static ProjectInfo fromJson(JsonObject obj) {
        // Defensive defaults: a marker file written by a future BLib version may omit fields we don't recognize, or
        // may have been hand-edited. Missing required fields fall back to sensible empties so listProjects doesn't
        // crash on a malformed marker — the project still appears in the picker, just with blanks.
        var name = obj.has("name") ? obj.get("name").getAsString() : "";
        var description = obj.has("description") ? obj.get("description").getAsString() : "";
        var createdAt = obj.has("createdAt") ? obj.get("createdAt").getAsString() : "";
        var mcVersion = obj.has("mcVersion") ? obj.get("mcVersion").getAsString() : "";
        var version = obj.has("blibProjectVersion") ? obj.get("blibProjectVersion").getAsInt() : CURRENT_VERSION;
        return new ProjectInfo(name, description, createdAt, mcVersion, version);
    }

    public static ProjectInfo readMarker(Path packRoot) throws IOException {
        var marker = packRoot.resolve(MARKER_FILE_NAME);
        var text = Files.readString(marker);
        var parsed = JsonParser.parseString(text);
        if (!parsed.isJsonObject()) {
            throw new IOException("Malformed " + MARKER_FILE_NAME + ": expected JSON object");
        }
        return fromJson(parsed.getAsJsonObject());
    }
}
