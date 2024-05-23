package org.avp.api.entity.data.sync;

import java.util.List;

public interface SyncedDataHandleUser {

    <V> void addSyncedDataHandle(SyncedDataHandle<V> syncedDataHandle);

    List<SyncedDataHandle<?>> getSyncedDataHandleList();
}
