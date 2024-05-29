package org.avp.api.block.entity.data;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.avp.api.Holder;

public abstract class BlockEntityData<T extends BlockEntity> {

    private final Holder<BlockEntityType<T>> holder = createHolder();

    protected abstract Holder<BlockEntityType<T>> createHolder();

    public Holder<BlockEntityType<T>> getHolder() {
        return holder;
    }

    public final String getRegistryName() {
        return getHolder().getResourceLocation().getPath();
    }
}
