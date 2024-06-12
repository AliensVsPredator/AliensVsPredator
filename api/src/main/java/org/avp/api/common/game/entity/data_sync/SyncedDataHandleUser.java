package org.avp.api.common.game.entity.data_sync;

import java.util.List;

public interface SyncedDataHandleUser {

    <V> void addSyncedDataHandle(SyncedDataHandle<V> syncedDataHandle);

    List<SyncedDataHandle<?>> getSyncedDataHandleList();
}
