package com.blib.api.common.mod.v1.model.access;

import com.just.core.functional.option.Option;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreManager;
import com.blib.api.common.storage.v1.DataStoreType;
import com.blib.internal.common.storage.BLibDataStoreManager;

public class BLibStorageAccess implements DataStoreManager {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibStorageAccess(BLibMod mod) {
        this.mod = mod;
    }

    @Override
    public <T extends DataStore> T getGlobal(MinecraftServer server, BLibHolder<DataStoreType<T>> type) {
        return BLibDataStoreManager.INSTANCE.getGlobal(server, type);
    }

    @Override
    public <T extends DataStore> T getLevel(ServerLevel level, BLibHolder<DataStoreType<T>> type) {
        return BLibDataStoreManager.INSTANCE.getLevel(level, type);
    }

    @Override
    public <T extends DataStore> Option<T> getChunk(ServerLevel level, ChunkPos pos, BLibHolder<DataStoreType<T>> type) {
        return BLibDataStoreManager.INSTANCE.getChunk(level, pos, type);
    }
}
