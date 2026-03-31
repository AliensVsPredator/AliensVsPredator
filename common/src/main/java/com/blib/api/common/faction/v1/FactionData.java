package com.blib.api.common.faction.v1;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.nbt.v1.model.NBTSerializable;
import com.blib.api.common.util.v1.Dirty;

public abstract class FactionData implements NBTSerializable, Dirty {

    private boolean dirty;

    public void onMemberAdded(FactionMember member) {}

    public void onMemberAdded(FactionMember member, Entity entity) {
        onMemberAdded(member);
    }

    public void onMemberRemoved(FactionMember member) {}

    public void onMemberLoaded(Entity entity) {}

    public void onMemberUnloaded(Entity entity) {}

    @Override
    public void markDirty() {
        this.dirty = true;
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
