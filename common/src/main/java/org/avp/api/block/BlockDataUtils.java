package org.avp.api.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import org.avp.api.GameObject;
import org.avp.api.block.drop.BlockDrops;
import org.avp.api.block.drop.key.BlockDropKeys;
import org.avp.api.block.factory.BlockFactories;

public class BlockDataUtils {

    public static BlockData.Builder fence(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.FENCE)
            .tags(BlockTags.FENCES);
    }

    public static BlockData.Builder fenceGate(
        GameObject<Block> parentGameObject,
        WoodType woodType,
        BlockBehaviour.Properties properties
    ) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.FENCE_GATE.apply(woodType))
            .tags(BlockTags.FENCE_GATES);
    }

    public static BlockData.Builder grass(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.GRASS)
            .drop(BlockDrops.OTHER.apply(BlockDropKeys.OTHER, parentGameObject::get));
    }

    public static BlockData.Builder rotatedPillar(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.ROTATED_PILLAR);
    }

    public static BlockData.Builder rotatedVariant(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.ROTATED_VARIANT);
    }

    public static BlockData.Builder slab(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.SLAB)
            .tags(BlockTags.SLABS);
    }

    public static BlockData.Builder stairs(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.STAIRS.apply(parentGameObject))
            .tags(BlockTags.STAIRS);
    }

    public static BlockData.Builder transparent(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties).factory(BlockFactories.TRANSPARENT);
    }

    public static BlockData.Builder wall(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.WALL)
            .tags(BlockTags.WALLS);
    }

    public static BlockData.Builder wood(GameObject<Block> parentGameObject, BlockBehaviour.Properties properties) {
        return BlockData.builder(properties)
            .parent(parentGameObject)
            .factory(BlockFactories.WOOD);
    }

    private BlockDataUtils() {
        throw new UnsupportedOperationException();
    }
}
