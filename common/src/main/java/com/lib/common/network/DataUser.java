package com.lib.common.network;

public interface DataUser {

    default DataContainer getDataContainer() {
        throw new UnsupportedOperationException();
    }

    default <T> T get(DataKey<T> key) {
        return getDataContainer().get(key);
    }

    default <T> void set(DataKey<T> key, T value) {
        getDataContainer().set(key, value);
    }
}
