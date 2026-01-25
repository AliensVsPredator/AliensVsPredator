package com.blib.api.common.entity.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class PlayerUtil {

    public static List<ServerPlayer> getTrackingPlayers(Entity entity) {
        return getTrackingPlayers(entity.level(), entity.blockPosition());
    }

    public static List<ServerPlayer> getTrackingPlayers(Level level, BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        return serverLevel.getChunkSource().chunkMap.getPlayersCloseForSpawning(new ChunkPos(blockPos));
    }
}
