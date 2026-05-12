package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.common.territory.BLibTerritoryManager;

/**
 * One (chunk, faction) claim addition or removal captured for undo/redo. {@link Direction} discriminates which way the
 * gesture went: {@code ADDED} means the user added the claim (so revert removes, redo re-adds); {@code REMOVED} is the
 * mirror.
 * <p>
 * Claims are many-to-many (multiple factions may claim the same chunk), so this action affects exactly one pair — other
 * claimants on the same chunk are left alone, which matches the underlying {@link BLibTerritoryManager#addClaim} /
 * {@link BLibTerritoryManager#removeClaim} semantics.
 */
@ApiStatus.Internal
public record ChunkClaimEdit(
    ResourceKey<Level> dimension,
    int chunkX,
    int chunkZ,
    ResourceLocation factionId,
    Direction direction,
    String description,
    long timestamp
) implements WorldAction {

    public enum Direction {
        ADDED,
        REMOVED
    }

    public static final String TYPE_ID = "chunk_claim";

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
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var pos = new ChunkPos(chunkX, chunkZ);
        switch (direction) {
            case ADDED -> BLibTerritoryManager.INSTANCE.removeClaim(level, pos, factionId);
            case REMOVED -> BLibTerritoryManager.INSTANCE.addClaim(level, pos, factionId);
        }
    }

    @Override
    public void redo(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        var pos = new ChunkPos(chunkX, chunkZ);
        switch (direction) {
            case ADDED -> BLibTerritoryManager.INSTANCE.addClaim(level, pos, factionId);
            case REMOVED -> BLibTerritoryManager.INSTANCE.removeClaim(level, pos, factionId);
        }
    }
}
