package com.lib.common.network;

public interface SyncedDataUser {

    default SyncedDataContainer getSyncedDataContainer() {
        throw new UnsupportedOperationException();
    }

    default <T> T get(AbstractSyncedDataKey<T> key) {
        return getSyncedDataContainer().get(key);
    }

    default <T> void set(AbstractSyncedDataKey<T> key, T value) {
        getSyncedDataContainer().set(key, value);
    }
}
