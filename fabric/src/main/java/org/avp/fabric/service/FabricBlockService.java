package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.item.AVPItemBlockItems;
import org.avp.common.service.BlockService;
import org.avp.common.service.Services;

public class FabricBlockService implements BlockService {

    @Override
    public Holder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = new Holder<>(registryName, supplier);
        var blockItemHolder = Services.ITEM_REGISTRY.createHolder(registryName, () -> new BlockItem(holder.get(), new Item.Properties()));
        AVPItemBlockItems.INSTANCE.addHolder(blockItemHolder);
        return holder;
    }

    @Override
    public void register(Holder<Block> holder) {
        Registry.register(BuiltInRegistries.BLOCK, holder.getResourceLocation(), holder.get());
    }

    @Override
    public StairBlock createStairBlock(Holder<Block> blockHolder, BlockBehaviour.Properties properties) {
        return new StairBlock(blockHolder.get().defaultBlockState(), properties);
    }
}
