package com.blib.common.network.data;

import java.util.function.Consumer;

public class DataAccessor<T> {

    private final DataContainer dataContainer;

    private final DataSyncKey<T> dataSyncKey;

    public DataAccessor(DataUser dataUser, DataSyncKey<T> dataSyncKey) {
        this(dataUser.getDataContainer(), dataSyncKey);
    }

    public DataAccessor(DataContainer dataContainer, DataSyncKey<T> dataSyncKey) {
        this.dataSyncKey = dataSyncKey;
        this.dataContainer = dataContainer;

        // Auto-initialize.
        dataContainer.set(dataSyncKey, dataSyncKey.initialValue());
    }

    public T get() {
        return dataContainer.get(dataSyncKey);
    }

    public void reset() {
        dataContainer.set(dataSyncKey, dataSyncKey.initialValue());
    }

    public void set(T value) {
        dataContainer.set(dataSyncKey, value);
    }

    public void onChange(Consumer<T> callback) {
        dataContainer.setOnChangeCallback(dataSyncKey, callback);
    }

    public void onLoad(Consumer<T> callback) {
        dataContainer.setOnLoadCallback(dataSyncKey, callback);
    }
}
