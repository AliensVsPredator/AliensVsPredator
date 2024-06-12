package org.avp.neoforge.service;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.BlockService;
import org.avp.api.service.Services;
import org.avp.common.AVPConstants;
import org.avp.neoforge.util.NeoForgeHolder;

import java.util.function.Supplier;

public class NeoForgeBlockService implements BlockService {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(AVPConstants.MOD_ID);

    @Override
    public BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = new NeoForgeHolder<>(BLOCKS, registryName, supplier);
        Services.ITEM_SERVICE.createHolder(registryName, () -> new BlockItem(holder.get(), new Item.Properties()));
        return holder;
    }

    @Override
    public void register(BLHolder<Block> holder) { /* NO-OP FOR FORGE */ }

    @Override
    public StairBlock createStairBlock(BLHolder<Block> blockHolder, BlockBehaviour.Properties properties) {
        return new StairBlock(() -> blockHolder.get().defaultBlockState(), properties);
    }
}
