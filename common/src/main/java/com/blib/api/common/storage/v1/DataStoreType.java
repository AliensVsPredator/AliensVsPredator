package com.blib.api.common.storage.v1;

import java.util.function.Supplier;

/**
 * A registered type that defines how to create instances of a {@link DataStore}.
 * <p>
 * Data store types are registered to the {@link com.blib.api.common.registry.v1.BLibBuiltInRegistries#DATA_STORE_TYPES}
 * registry, which allows the storage system to look up factories by {@link net.minecraft.resources.ResourceLocation}
 * when loading saved data from disk.
 *
 * @param factory the factory used to create new instances of this data store
 * @param <T>     the type of data store this type creates
 */
public record DataStoreType<T extends DataStore>(Supplier<T> factory) {

    /**
     * Creates a new instance of this data store type.
     *
     * @return a new data store instance
     */
    public T createInstance() {
        return factory.get();
    }
}
