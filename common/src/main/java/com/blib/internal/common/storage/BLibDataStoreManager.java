package com.blib.internal.common.storage;

import com.just.core.functional.option.Option;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;

@ApiStatus.Internal
public class BLibDataStoreManager {

    public static final BLibDataStoreManager INSTANCE = new BLibDataStoreManager();

    private final BLibChunkDataStoreManager chunk;

    private final BLibGlobalDataStoreManager global;

    private final BLibLevelDataStoreManager level;

    private BLibDataStoreManager() {
        this.chunk = new BLibChunkDataStoreManager();
        this.global = new BLibGlobalDataStoreManager();
        this.level = new BLibLevelDataStoreManager();
    }

    public <T extends DataStore> T getGlobal(MinecraftServer minecraftServer, BLibHolder<DataStoreType<T>> holder) {
        return global.get(minecraftServer, holder);
    }

    public <T extends DataStore> T getLevel(ServerLevel serverLevel, BLibHolder<DataStoreType<T>> holder) {
        return this.level.get(serverLevel, holder);
    }

    public <T extends DataStore> Option<T> getChunk(ServerLevel serverLevel, ChunkPos chunkPos, BLibHolder<DataStoreType<T>> holder) {
        return chunk.get(serverLevel, chunkPos, holder);
    }

    public void saveGlobalData(MinecraftServer minecraftServer) {
        global.save(minecraftServer);
    }

    public void saveLevelData(ServerLevel serverLevel) {
        level.save(serverLevel);
        chunk.saveAllForLevel(serverLevel);
    }

    public void saveChunkData(ServerLevel serverLevel, ChunkPos chunkPos) {
        chunk.saveChunk(serverLevel, chunkPos);
    }

    public void onServerStopped(MinecraftServer minecraftServer) {
        global.clear();
        level.clear();
        chunk.clear();
    }

    public void onChunkUnload(ServerLevel serverLevel, ChunkAccess chunkAccess) {
        onChunkUnload(serverLevel, chunkAccess.getPos());
    }

    public void onChunkUnload(ServerLevel serverLevel, ChunkPos chunkPos) {
        chunk.onChunkUnload(serverLevel, chunkPos);
    }
}
