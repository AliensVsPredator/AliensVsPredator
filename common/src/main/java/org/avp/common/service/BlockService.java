package org.avp.common.service;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface BlockService {

    GameObject<Block> createHolder(String registryName, Supplier<Block> supplier);

    void register(GameObject<Block> blockGameObject);

    StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties);
}
