package com.blib.mod.common.gameplay.history;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.mod.common.gameplay.history.exec.ProjectActionExec;

/**
 * Captures one pool / tag JSON edit for undo/redo. {@link Kind} discriminates which on-disk store the action targets;
 * the corresponding id fields point at the affected file ({@code poolId} for pools, {@code registryKey + tagId} for
 * tags).
 * <p>
 * {@link #beforeJson} and {@link #afterJson} are deep copies of the JSON state on each side of the gesture.
 * {@code null} means "no project override file exists" (relevant only to tags, where the persist-or-cleanup path can
 * delete a file when its contents become equivalent to upstream). Revert writes {@code beforeJson}; redo writes
 * {@code afterJson}; either side may be a deletion if the value is {@code null}.
 */
@ApiStatus.Internal
public record ProjectJsonEdit(
    String projectName,
    Kind kind,
    @Nullable ResourceLocation poolId,
    @Nullable ResourceLocation registryKey,
    @Nullable ResourceLocation tagId,
    @Nullable JsonObject beforeJson,
    @Nullable JsonObject afterJson,
    String description,
    long timestamp
) implements ProjectAction {

    public enum Kind {
        POOL,
        TAG
    }

    public static final String TYPE_ID = "project_json";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        var size = 96L;
        if (beforeJson != null) {
            size += beforeJson.toString().length() * 2L;
        }
        if (afterJson != null) {
            size += afterJson.toString().length() * 2L;
        }
        return size;
    }

    @Override
    public void revert(MinecraftServer server) {
        apply(server, beforeJson);
    }

    @Override
    public void redo(MinecraftServer server) {
        apply(server, afterJson);
    }

    private void apply(MinecraftServer server, @Nullable JsonObject json) {
        switch (kind) {
            case POOL -> {
                if (poolId == null) {
                    return;
                }
                ProjectActionExec.applyPoolDraft(server, projectName, poolId, json);
            }
            case TAG -> {
                if (registryKey == null || tagId == null) {
                    return;
                }
                ProjectActionExec.applyTagDraft(server, projectName, registryKey, tagId, json);
            }
        }
    }
}
