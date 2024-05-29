package org.avp.common.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.Holder;
import org.avp.common.service.Services;

import java.util.function.BiFunction;

public class AVPSimpleDeferredBlockEntityTypeRegistry extends AVPAbstractDeferredBlockEntityTypeRegistry {

    public static final AVPSimpleDeferredBlockEntityTypeRegistry INSTANCE = new AVPSimpleDeferredBlockEntityTypeRegistry();

    @Override
    public <T extends BlockEntity> Holder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        Holder<Block> blockHolder
    ) {
        var holder = Services.BLOCK_ENTITY_TYPE_SERVICE.createHolder(registryName, blockEntityFactory, blockHolder);
        entries.add(holder);
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void register() {
        entries.forEach(holder -> Services.BLOCK_ENTITY_TYPE_SERVICE.register((Holder<BlockEntityType<?>>) holder));
    }
}
