package org.avp.api.registry.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.registry.holder.BLHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AVPAbstractDeferredBlockEntityTypeRegistry {

    protected final List<BLHolder<? extends BlockEntityType<?>>> entries;

    protected AVPAbstractDeferredBlockEntityTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    );

    public abstract void register();

    public List<BLHolder<? extends BlockEntityType<?>>> getEntries() {
        return entries;
    }
}
