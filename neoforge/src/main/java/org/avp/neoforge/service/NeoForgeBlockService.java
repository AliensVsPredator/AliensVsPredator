package org.avp.neoforge.service;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.service.BlockService;
import org.avp.common.service.Services;
import org.avp.neoforge.util.ForgeGameObject;

public class NeoForgeBlockService implements BlockService {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(AVPConstants.MOD_ID);

    @Override
    public GameObject<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var gameObject = new ForgeGameObject<>(BLOCKS, registryName, supplier);
        Services.ITEM_REGISTRY.createHolder(registryName, () -> new BlockItem(gameObject.get(), new Item.Properties()));
        return gameObject;
    }

    @Override
    public void register(GameObject<Block> holder) { /* NO-OP FOR FORGE */ }

    @Override
    public StairBlock createStairBlock(GameObject<Block> blockGameObject, BlockBehaviour.Properties properties) {
        return new StairBlock(() -> blockGameObject.get().defaultBlockState(), properties);
    }
}
