package org.avp.common.service;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface BlockRegistry {

    GameObject<Block> register(String registryName, Supplier<Block> supplier);

    StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties);
}
