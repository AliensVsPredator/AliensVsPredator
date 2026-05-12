package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * Entity SCALE-attribute edit captured for undo/redo. Stores pre/post scale values; both revert and redo set the base
 * value on the live entity's SCALE attribute. No-ops if the entity is gone or doesn't expose SCALE (some mob types
 * don't register the attribute).
 */
@ApiStatus.Internal
public record EntityScaleAction(
    ResourceKey<Level> dimension,
    UUID entityUuid,
    double beforeScale,
    double afterScale,
    String description,
    long timestamp
) implements WorldAction {

    public static final String TYPE_ID = "entity_scale";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        return 64L;
    }

    @Override
    public void revert(MinecraftServer server) {
        applyScale(server, beforeScale);
    }

    @Override
    public void redo(MinecraftServer server) {
        applyScale(server, afterScale);
    }

    private void applyScale(MinecraftServer server, double scale) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var entity = level.getEntity(entityUuid);
        if (!(entity instanceof LivingEntity le) || le instanceof Player) {
            return;
        }
        var attr = le.getAttribute(Attributes.SCALE);
        if (attr == null) {
            return;
        }
        attr.setBaseValue(scale);
    }
}
