package com.lib.common.network;

public interface SyncedDataUser {

    default SyncedDataContainer getSyncedDataContainer() {
        throw new UnsupportedOperationException();
    }

    default <T> T get(SyncedDataKey<T> key) {
        return getSyncedDataContainer().get(key);
    }

    default <T> void set(SyncedDataKey<T> key, T value) {
        getSyncedDataContainer().set(key, value);
    }
}
