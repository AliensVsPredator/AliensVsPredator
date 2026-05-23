package com.blib.mod.common.registry.init;

import java.util.function.Supplier;

import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;
import com.blib.internal.common.territory.ChunkClaimDataStore;
import com.blib.mod.BLib;

public class BLibTerritoryDataStoreTypes {

    private static final BLibRegistry<DataStoreType<?>> REGISTRY = BLib.MOD.registries()
        .create(BLibBuiltInRegistries.DATA_STORE_TYPES);

    public static final BLibHolder<DataStoreType<ChunkClaimDataStore>> CHUNK_CLAIMS = register(
        "chunk_claims",
        () -> new DataStoreType<>(ChunkClaimDataStore::new)
    );

    private static <T extends DataStore> BLibHolder<DataStoreType<T>> register(
        String path,
        Supplier<DataStoreType<T>> supplier
    ) {
        return REGISTRY.createHolder(path, supplier);
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
