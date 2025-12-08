package com.blib.common.network.data;

import java.util.function.Consumer;

public class DataAccessor<T> {

    private final DataContainer dataContainer;

    private final DataKey<T> dataKey;

    public DataAccessor(DataUser dataUser, DataKey<T> dataKey) {
        this(dataUser.getDataContainer(), dataKey);
    }

    public DataAccessor(DataContainer dataContainer, DataKey<T> dataKey) {
        this.dataKey = dataKey;
        this.dataContainer = dataContainer;

        // Auto-initialize.
        dataContainer.set(dataKey, dataKey.initialValue());
    }

    public T get() {
        return dataContainer.get(dataKey);
    }

    public void reset() {
        dataContainer.set(dataKey, dataKey.initialValue());
    }

    public void set(T value) {
        dataContainer.set(dataKey, value);
    }

    public void onChange(Consumer<T> callback) {
        dataContainer.setOnChangeCallback(dataKey, callback);
    }

    public void onLoad(Consumer<T> callback) {
        dataContainer.setOnLoadCallback(dataKey, callback);
    }
}
