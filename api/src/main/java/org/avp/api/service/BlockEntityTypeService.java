package org.avp.api.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

import org.avp.api.common.registry.holder.BLHolder;

public interface BlockEntityTypeService {

    <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    );

    void register(BLHolder<BlockEntityType<?>> holder);
}
