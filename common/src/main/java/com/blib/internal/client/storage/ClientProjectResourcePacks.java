package com.blib.internal.client.storage;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Client-side selection helper for BLib project resource packs. The repository source only makes project packs
 * available; this helper is what turns the active project's pack on in the client resource-pack repository and reloads
 * resources when that selection changes.
 */
@ApiStatus.Internal
public final class ClientProjectResourcePacks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProjectResourcePacks.class);

    private ClientProjectResourcePacks() {}

    public static void applyProject(String projectName, boolean forceReload) {
        if (projectName == null || projectName.isBlank()) {
            return;
        }
        Minecraft.getInstance().execute(() -> applyProjectOnClientThread(projectName, forceReload));
    }

    public static void removeProject(String projectName) {
        if (projectName == null || projectName.isBlank()) {
            return;
        }
        Minecraft.getInstance().execute(() -> {
            var mc = Minecraft.getInstance();
            var packRepo = mc.getResourcePackRepository();
            var packId = EngineProjectIO.PACK_ID_PREFIX + projectName;
            var selected = new ArrayList<>(packRepo.getSelectedIds());

            if (selected.remove(packId)) {
                packRepo.setSelected(selected);
                mc.reloadResourcePacks();
            }
        });
    }

    private static void applyProjectOnClientThread(String projectName, boolean forceReload) {
        var mc = Minecraft.getInstance();
        var packRepo = mc.getResourcePackRepository();
        var packId = EngineProjectIO.PACK_ID_PREFIX + projectName;

        // Rescan first so a pack whose resourcepack/ subdir was created lazily on first asset save becomes visible.
        packRepo.reload();

        var selected = new ArrayList<>(packRepo.getSelectedIds());
        var changed = selected.removeIf(id -> id.startsWith(EngineProjectIO.PACK_ID_PREFIX) && !id.equals(packId));

        if (packRepo.getAvailableIds().contains(packId)) {
            if (!selected.contains(packId)) {
                selected.add(packId);
                changed = true;
            }
        } else {
            LOGGER.debug("[BLib] ClientProjectResourcePacks: project '{}' has no available resource pack", projectName);
        }

        if (changed) {
            packRepo.setSelected(selected);
        }
        if (changed || forceReload) {
            mc.reloadResourcePacks();
        }
    }
}
