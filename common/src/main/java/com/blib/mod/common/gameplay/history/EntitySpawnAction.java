package com.blib.mod.common.gameplay.history;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Engine-mode entity spawn captured for undo/redo. Holds the spawned entity's full NBT and UUID — undo discards by
 * UUID, redo reconstructs from NBT at the same position.
 * <p>
 * NBT is taken via {@code saveWithoutId} after the spawn settles, so passive setup (default attributes, AI goals,
 * starting equipment) is preserved across the round-trip. Redo uses {@link EntityType#create} + {@code load} rather
 * than a fresh {@link EntityType#spawn}, so the restored entity matches the original byte-for-byte; that's important
 * when the user has been editing attributes (scale, etc.) and then undoes the spawn — without re-loading NBT, the redo
 * would lose those edits.
 */
@ApiStatus.Internal
public record EntitySpawnAction(
    ResourceKey<Level> dimension,
    ResourceLocation entityTypeId,
    UUID entityUuid,
    CompoundTag savedNbt,
    BlockPos pos,
    String description,
    long timestamp
) implements WorldAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySpawnAction.class);

    public static final String TYPE_ID = "entity_spawn";

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
        var entity = level.getEntity(entityUuid);
        if (entity != null) {
            entity.discard();
        }
    }

    @Override
    public void redo(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var typeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId);
        if (typeOpt.isEmpty()) {
            LOGGER.warn("[BLib] EntitySpawnAction.redo: unknown entity type {}", entityTypeId);
            return;
        }
        var entity = typeOpt.get().create(level);
        if (entity == null) {
            return;
        }
        entity.load(savedNbt);
        entity.setUUID(entityUuid);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (entity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, null);
        }
        level.addFreshEntity(entity);
    }
}
