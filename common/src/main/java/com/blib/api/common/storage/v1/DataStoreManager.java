package com.blib.api.common.storage.v1;

import com.just.core.functional.option.Option;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import com.blib.api.common.registry.v1.BLibHolder;

public interface DataStoreManager {

    <T extends DataStore> T getGlobal(MinecraftServer server, BLibHolder<DataStoreType<T>> type);

    <T extends DataStore> T getLevel(ServerLevel level, BLibHolder<DataStoreType<T>> type);

    <T extends DataStore> Option<T> getChunk(ServerLevel level, ChunkPos pos, BLibHolder<DataStoreType<T>> type);
}
