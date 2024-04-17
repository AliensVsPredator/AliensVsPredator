package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.BlockService;
import org.avp.common.service.Services;

public class FabricBlockService implements BlockService {

    @Override
    public GameObject<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var gameObject = new GameObject<>(registryName, supplier);
        Services.ITEM_REGISTRY.register(registryName, () -> new BlockItem(gameObject.get(), new Item.Properties()));
        return gameObject;
    }

    @Override
    public void register(GameObject<Block> blockGameObject) {
        Registry.register(BuiltInRegistries.BLOCK, blockGameObject.getResourceLocation(), blockGameObject.get());
    }

    @Override
    public StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties) {
        return new StairBlock(blockGameObject.get().defaultBlockState(), properties);
    }
}
