package org.avp.api.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class SyncedDataHandle<T> {

    public static <V> SyncedDataHandle<V> attach(String key, V defaultValue, Entity entity, EntityDataAccessor<V> entityDataAccessor, SyncedDataSerializer<V> syncedDataSerializer) {
        // Create the synced data handle.
        var syncedDataHandle = new SyncedDataHandle<>(key, defaultValue, entity, entityDataAccessor, syncedDataSerializer);
        // Attach the synced data handle to the entity.
        if (entity instanceof SyncedDataHandleUser syncedDataHandleUser) {
            syncedDataHandleUser.addSyncedDataHandle(syncedDataHandle);
        }
        return syncedDataHandle;
    }

    private final String key;
    private final Entity entity;
    private final EntityDataAccessor<T> entityDataAccessor;
    private final SyncedDataSerializer<T> syncedDataSerializer;

    private SyncedDataHandle(String key, T defaultValue, Entity entity, EntityDataAccessor<T> entityDataAccessor, SyncedDataSerializer<T> syncedDataSerializer) {
        this.key = key;
        this.entity = entity;
        this.entityDataAccessor = entityDataAccessor;
        this.syncedDataSerializer = syncedDataSerializer;

        entity.getEntityData().define(entityDataAccessor, defaultValue);
    }

    public T get() {
        return entity.getEntityData().get(entityDataAccessor);
    }

    public void set(T value) {
        entity.getEntityData().set(entityDataAccessor, value);
    }

    public void read(CompoundTag compoundTag) {
        var value = syncedDataSerializer.read(compoundTag, key);
        set(value);
    }

    public void write(CompoundTag compoundTag) {
        syncedDataSerializer.write(compoundTag, key, get());
    }

    public Entity getEntity() {
        return entity;
    }
}
