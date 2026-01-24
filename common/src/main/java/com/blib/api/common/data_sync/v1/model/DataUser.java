package com.blib.api.common.data_sync.v1.model;

import com.blib.api.common.data_sync.v1.DataContainer;

public interface DataUser {

    default DataContainer getDataContainer() {
        throw new UnsupportedOperationException();
    }
}
