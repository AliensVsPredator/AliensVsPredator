package org.avp.common.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.Holder;

import java.util.function.BiFunction;

public interface BlockEntityTypeService {

    <T extends BlockEntity> Holder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        Holder<Block> blockHolder
    );

    void register(Holder<BlockEntityType<?>> holder);
}
