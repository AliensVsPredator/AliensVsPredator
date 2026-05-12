package com.blib.mod.common.gameplay.history.exec;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import com.blib.internal.common.storage.EngineProjectIO;
import com.blib.internal.common.storage.ProjectDraftStore;
import com.blib.internal.common.storage.ProjectTagDraftStore;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CPoolDraftPayload;
import com.blib.mod.common.network.packet.S2CTagDraftPayload;
import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Shared execution path for pool/tag JSON mutations. The C2S handlers AND the undo/redo {@code ProjectJsonEdit} action
 * route through here so disk write + S2C broadcast logic lives in exactly one place. Broadcasts go to every connected
 * client (not just the initiator) so anyone with an editor open sees the result on the next frame.
 */
@ApiStatus.Internal
public final class ProjectActionExec {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectActionExec.class);

    private ProjectActionExec() {}

    /**
     * Write the pool draft for {@code (projectName, poolId)} to disk and broadcast a fresh {@link S2CPoolDraftPayload}.
     * {@code null} JSON is treated as a no-op for pools (v1 doesn't support deleting pool files via the history pathway
     * — see plan).
     */
    public static void applyPoolDraft(
        MinecraftServer server,
        String projectName,
        ResourceLocation poolId,
        @Nullable JsonObject json
    ) {
        if (json == null) {
            return;
        }
        try {
            ProjectDraftStore.INSTANCE.writeAndPersist(projectName, poolId, json);
        } catch (IOException e) {
            LOGGER.error("[BLib] ProjectActionExec.applyPoolDraft: write failed for project {} pool {}", projectName, poolId, e);
            return;
        }
        var elements = ProjectDraftStore.extractDraftElements(json);
        BLib.MOD.networking().sendToAllClients(server, new S2CPoolDraftPayload(projectName, poolId, elements));
    }

    /**
     * Apply a tag draft. {@code null} JSON deletes the project's override file (matching the persist-or-cleanup
     * pathway). Either way, broadcasts a fresh {@link S2CTagDraftPayload} reflecting current disk state.
     */
    public static void applyTagDraft(
        MinecraftServer server,
        String projectName,
        ResourceLocation registryKey,
        ResourceLocation tagId,
        @Nullable JsonObject json
    ) {
        ResourceKey<? extends Registry<?>> registryRk = ResourceKey.createRegistryKey(registryKey);
        if (json == null) {
            try {
                ProjectTagDraftStore.INSTANCE.deleteProjectTag(projectName, registryRk, tagId);
            } catch (IOException e) {
                LOGGER.error(
                    "[BLib] ProjectActionExec.applyTagDraft: delete failed for project {} tag {}/{}",
                    projectName,
                    registryKey,
                    tagId,
                    e
                );
                return;
            }
            var reseed = ProjectTagDraftStore.INSTANCE.getOrSeedTag(server, projectName, registryRk, tagId);
            if (reseed != null) {
                broadcastTagDraft(server, projectName, registryKey, registryRk, tagId, reseed);
            }
            return;
        }
        try {
            ProjectTagDraftStore.INSTANCE.writeAndPersist(projectName, registryRk, tagId, json);
        } catch (IOException e) {
            LOGGER.error(
                "[BLib] ProjectActionExec.applyTagDraft: write failed for project {} tag {}/{}",
                projectName,
                registryKey,
                tagId,
                e
            );
            return;
        }
        broadcastTagDraft(server, projectName, registryKey, registryRk, tagId, json);
    }

    private static void broadcastTagDraft(
        MinecraftServer server,
        String projectName,
        ResourceLocation registryKey,
        ResourceKey<? extends Registry<?>> registryRk,
        ResourceLocation tagId,
        JsonObject tag
    ) {
        var rawEntries = ProjectTagDraftStore.extractDraftEntries(tag);
        var replace = ProjectTagDraftStore.readReplace(tag);
        var resolved = ProjectTagDraftStore.extractResolvedMembers(server, registryRk, tagId);
        var upstreamKeys = ProjectTagDraftStore.extractUpstreamEntryKeys(server, projectName, registryRk, tagId);
        var entries = rawEntries.stream()
            .map(
                e -> new TagEntryDraft(
                    e.rawIndex(),
                    e.isTagRef(),
                    e.id(),
                    e.required(),
                    upstreamKeys.contains(ProjectTagDraftStore.entryKey(e.isTagRef(), e.id()))
                )
            )
            .toList();
        var inProject = EngineProjectIO.hasProjectTagJson(projectName, registryRk, tagId);
        BLib.MOD.networking()
            .sendToAllClients(server, new S2CTagDraftPayload(projectName, registryKey, tagId, replace, entries, resolved, inProject));
    }
}
