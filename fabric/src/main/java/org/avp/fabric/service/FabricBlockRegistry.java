package org.avp.fabric.service;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.BlockRegistry;
import org.avp.common.service.Services;
import org.avp.fabric.common.registry.AVPDeferredBlockRegistry;

/**
 * @author Boston Vanseghi
 */
public class FabricBlockRegistry implements BlockRegistry {

    @Override
    public GameObject<Block> register(String registryName, Supplier<Block> supplier) {
        var gameObject = new GameObject<>(registryName, supplier);
        AVPDeferredBlockRegistry.enqueue(gameObject);
        Services.ITEM_REGISTRY.register(registryName, () -> new BlockItem(gameObject.get(), new Item.Properties()));
        return gameObject;
    }

    @Override
    public StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties) {
        return new StairBlock(blockGameObject.get().defaultBlockState(), properties);
    }
}
