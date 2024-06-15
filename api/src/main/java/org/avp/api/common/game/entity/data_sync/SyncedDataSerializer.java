package org.avp.api.common.game.entity.data_sync;

import net.minecraft.nbt.CompoundTag;

public interface SyncedDataSerializer<T> {

    T read(CompoundTag compoundTag, String key);

    void write(CompoundTag compoundTag, String key, T value);

    SyncedDataSerializer<Boolean> BOOLEAN = new SyncedDataSerializer<>() {

        @Override
        public Boolean read(CompoundTag compoundTag, String key) {
            return compoundTag.getBoolean(key);
        }

        @Override
        public void write(CompoundTag compoundTag, String key, Boolean value) {
            compoundTag.putBoolean(key, value);
        }
    };
}
