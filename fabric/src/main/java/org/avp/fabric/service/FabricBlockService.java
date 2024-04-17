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
import org.avp.common.item.AVPItemBlockItems;
import org.avp.common.service.BlockService;
import org.avp.common.service.Services;

public class FabricBlockService implements BlockService {

    @Override
    public GameObject<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var gameObject = new GameObject<>(registryName, supplier);
        var holder = Services.ITEM_REGISTRY.createHolder(registryName, () -> new BlockItem(gameObject.get(), new Item.Properties()));
        AVPItemBlockItems.INSTANCE.addHolder(holder);
        return gameObject;
    }

    @Override
    public void register(GameObject<Block> holder) {
        Registry.register(BuiltInRegistries.BLOCK, holder.getResourceLocation(), holder.get());
    }

    @Override
    public StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties) {
        return new StairBlock(blockGameObject.get().defaultBlockState(), properties);
    }
}
