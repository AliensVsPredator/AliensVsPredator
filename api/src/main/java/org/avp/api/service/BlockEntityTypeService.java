package org.avp.api.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.registry.holder.BLHolder;

import java.util.function.BiFunction;

public interface BlockEntityTypeService {

    <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    );

    void register(BLHolder<BlockEntityType<?>> holder);
}
