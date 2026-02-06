package com.blib.mod.common.registry.init;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;
import com.blib.mod.BLib;

public class BLibDataStoreTypes {

    private static final BLibRegistry<DataStoreType<?>> REGISTRY = BLib.MOD.registries().create(BLibBuiltInRegistries.DATA_STORE_TYPES);

    public static final BLibHolder<DataStoreType<EmptyDataStore>> EMPTY = register(
        "empty",
        () -> new DataStoreType<>(EmptyDataStore::new)
    );

    private static <T extends DataStore> BLibHolder<DataStoreType<T>> register(String path, Supplier<DataStoreType<T>> supplier) {
        return REGISTRY.createHolder(path, supplier);
    }

    public static class EmptyDataStore implements DataStore {

        @Override
        public void load(CompoundTag compoundTag) {}

        @Override
        public void save(CompoundTag compoundTag) {}
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
