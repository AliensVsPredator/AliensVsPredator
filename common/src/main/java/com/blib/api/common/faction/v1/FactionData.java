package com.blib.api.common.faction.v1;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.nbt.v1.model.NBTSerializable;
import com.blib.api.common.util.v1.Dirty;

public abstract class FactionData implements NBTSerializable, Dirty {

    private boolean dirty;

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    @ApiStatus.Internal
    public void clearDirty() {
        dirty = false;
    }
}
