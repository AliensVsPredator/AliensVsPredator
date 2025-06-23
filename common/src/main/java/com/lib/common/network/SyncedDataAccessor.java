package com.lib.common.network;

public record SyncedDataAccessor<T>(
    SyncedDataContainer syncedDataContainer,
    AbstractSyncedDataKey<T> key
) {

    public T get() {
        return syncedDataContainer.get(key);
    }

    public void set(T value) {
        syncedDataContainer.set(key, value);
    }
}
