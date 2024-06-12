package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.holder.AVPHolder;
import org.avp.common.registry.item.AVPItemBlockItemRegistry;
import org.avp.api.service.BlockService;
import org.avp.api.service.Services;

public class FabricBlockService implements BlockService {

    @Override
    public BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = new AVPHolder<>(registryName, supplier);
        var blockItemHolder = Services.ITEM_SERVICE.createHolder(registryName, () -> new BlockItem(holder.get(), new Item.Properties()));
        AVPItemBlockItemRegistry.INSTANCE.addHolder(registryName, blockItemHolder);
        return holder;
    }

    @Override
    public void register(BLHolder<Block> holder) {
        Registry.register(BuiltInRegistries.BLOCK, holder.getResourceLocation(), holder.get());
    }

    @Override
    public StairBlock createStairBlock(BLHolder<Block> blockHolder, BlockBehaviour.Properties properties) {
        return new StairBlock(blockHolder.get().defaultBlockState(), properties);
    }
}
