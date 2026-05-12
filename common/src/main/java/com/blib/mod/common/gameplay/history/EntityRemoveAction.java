package com.blib.mod.common.gameplay.history;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Engine-mode entity removal captured for undo/redo. Mirror of {@link EntitySpawnAction} with revert/redo swapped: undo
 * re-spawns from the captured NBT, redo discards the entity by UUID.
 */
@ApiStatus.Internal
public record EntityRemoveAction(
    ResourceKey<Level> dimension,
    ResourceLocation entityTypeId,
    UUID entityUuid,
    CompoundTag savedNbt,
    BlockPos pos,
    String description,
    long timestamp
) implements WorldAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRemoveAction.class);

    public static final String TYPE_ID = "entity_remove";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        return 64L + savedNbt.sizeInBytes();
    }

    @Override
    public void revert(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var typeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId);
        if (typeOpt.isEmpty()) {
            LOGGER.warn("[BLib] EntityRemoveAction.revert: unknown entity type {}", entityTypeId);
            return;
        }
        var entity = typeOpt.get().create(level);
        if (entity == null) {
            return;
        }
        entity.load(savedNbt);
        entity.setUUID(entityUuid);
        level.addFreshEntity(entity);
    }

    @Override
    public void redo(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var entity = level.getEntity(entityUuid);
        if (entity != null) {
            entity.discard();
        }
    }
}
