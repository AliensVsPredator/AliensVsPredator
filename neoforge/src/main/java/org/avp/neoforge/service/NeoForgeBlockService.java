package org.avp.neoforge.service;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.AVPConstants;
import org.avp.common.service.BlockService;
import org.avp.common.service.Services;
import org.avp.neoforge.util.NeoForgeHolder;

public class NeoForgeBlockService implements BlockService {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(AVPConstants.MOD_ID);

    @Override
    public Holder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = new NeoForgeHolder<>(BLOCKS, registryName, supplier);
        Services.ITEM_REGISTRY.createHolder(registryName, () -> new BlockItem(holder.get(), new Item.Properties()));
        return holder;
    }

    @Override
    public void register(Holder<Block> holder) { /* NO-OP FOR FORGE */ }

    @Override
    public StairBlock createStairBlock(Holder<Block> blockHolder, BlockBehaviour.Properties properties) {
        return new StairBlock(() -> blockHolder.get().defaultBlockState(), properties);
    }
}
