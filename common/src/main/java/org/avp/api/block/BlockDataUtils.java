package org.avp.api.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import org.avp.api.Holder;
import org.avp.api.block.drop.BlockDrops;
import org.avp.api.block.drop.key.BlockDropKeys;
import org.avp.api.block.factory.BlockFactories;

public class BlockDataUtils {

    public static BlockData.Builder fence(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.FENCE)
            .blockTags(BlockTags.FENCES);
    }

    public static BlockData.Builder fenceGate(
        Holder<Block> parentHolder,
        WoodType woodType,
        BlockBehaviour.Properties properties
    ) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.FENCE_GATE.apply(woodType))
            .blockTags(BlockTags.FENCE_GATES);
    }

    public static BlockData.Builder grass(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.GRASS)
            .drop(BlockDrops.OTHER.apply(BlockDropKeys.OTHER, parentHolder::get));
    }

    public static BlockData.Builder rotatedPillar(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.ROTATED_PILLAR);
    }

    public static BlockData.Builder rotatedVariant(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.ROTATED_VARIANT);
    }

    public static BlockData.Builder slab(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.SLAB)
            .blockTags(BlockTags.SLABS);
    }

    public static BlockData.Builder stairs(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.STAIRS.apply(parentHolder))
            .blockTags(BlockTags.STAIRS);
    }

    public static BlockData.Builder transparent(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.TRANSPARENT);
    }

    public static BlockData.Builder wall(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.WALL)
            .blockTags(BlockTags.WALLS);
    }

    public static BlockData.Builder wood(Holder<Block> parentHolder, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentHolder)
            .factory(BlockFactories.WOOD);
    }

    private BlockDataUtils() {
        throw new UnsupportedOperationException();
    }
}
