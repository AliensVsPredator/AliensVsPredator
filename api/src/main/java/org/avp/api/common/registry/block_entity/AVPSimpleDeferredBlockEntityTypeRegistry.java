package org.avp.api.common.registry.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.Services;

import java.util.function.BiFunction;

public class AVPSimpleDeferredBlockEntityTypeRegistry extends AVPAbstractDeferredBlockEntityTypeRegistry {

    public static final AVPSimpleDeferredBlockEntityTypeRegistry INSTANCE = new AVPSimpleDeferredBlockEntityTypeRegistry();

    @Override
    public <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    ) {
        var holder = Services.BLOCK_ENTITY_TYPE_SERVICE.createHolder(registryName, blockEntityFactory, blockHolder);
        entries.add(holder);
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void register() {
        entries.forEach(holder -> Services.BLOCK_ENTITY_TYPE_SERVICE.register((BLHolder<BlockEntityType<?>>) holder));
    }
}
