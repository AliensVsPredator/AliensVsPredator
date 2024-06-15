package org.avp.api.service;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;

public interface BlockService {

    BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier);

    void register(BLHolder<Block> holder);

    StairBlock createStairBlock(BLHolder<Block> blockHolder, BlockBehaviour.Properties properties);
}
