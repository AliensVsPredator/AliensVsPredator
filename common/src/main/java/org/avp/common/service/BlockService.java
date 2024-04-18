package org.avp.common.service;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface BlockService {

    Holder<Block> createHolder(String registryName, Supplier<Block> supplier);

    void register(Holder<Block> holder);

    StairBlock createStairBlock(Holder<Block> blockHolder, BlockBehaviour.Properties properties);
}
