package com.lib.common.network;

public interface DataUser {

    default DataContainer getDataContainer() {
        throw new UnsupportedOperationException();
    }
}
