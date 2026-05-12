package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * Entity teleport captured for undo/redo. Stores the pre/post positions so revert teleports back and redo re-teleports
 * forward. If the entity has been discarded between the original gesture and the revert (e.g. the user removed it via a
 * separate gesture and that gesture was independently undone), the action becomes a no-op rather than throwing —
 * authoring sessions are forgiving about non-strict ordering.
 */
@ApiStatus.Internal
public record EntityTranslateAction(
    ResourceKey<Level> dimension,
    UUID entityUuid,
    double beforeX,
    double beforeY,
    double beforeZ,
    double afterX,
    double afterY,
    double afterZ,
    String description,
    long timestamp
) implements WorldAction {

    public static final String TYPE_ID = "entity_translate";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        return 96L;
    }

    @Override
    public void revert(MinecraftServer server) {
        teleport(server, beforeX, beforeY, beforeZ);
    }

    @Override
    public void redo(MinecraftServer server) {
        teleport(server, afterX, afterY, afterZ);
    }

    private void teleport(MinecraftServer server, double x, double y, double z) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var entity = level.getEntity(entityUuid);
        if (entity == null || entity instanceof Player) {
            return;
        }
        entity.teleportTo(x, y, z);
    }
}
