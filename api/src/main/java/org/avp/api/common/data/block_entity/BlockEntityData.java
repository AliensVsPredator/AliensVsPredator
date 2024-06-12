package org.avp.api.common.data.block_entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.avp.api.common.registry.holder.BLHolder;

public abstract class BlockEntityData<T extends BlockEntity> {

    private final BLHolder<BlockEntityType<T>> holder = createHolder();

    protected abstract BLHolder<BlockEntityType<T>> createHolder();

    public BLHolder<BlockEntityType<T>> getHolder() {
        return holder;
    }

    public final String getRegistryName() {
        return getHolder().getResourceLocation().getPath();
    }
}
