package com.blib.common.network.data;

public interface DataUser {

    default DataContainer getDataContainer() {
        throw new UnsupportedOperationException();
    }
}
