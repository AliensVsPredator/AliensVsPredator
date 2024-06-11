package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.block.BlockData;
import org.avp.api.registry.holder.BlockHolderSet;
import org.avp.api.registry.holder.BlockHolderSetData;
import org.avp.api.data.block.BlockTagData;
import org.avp.api.data.block.BlockModelData;
import org.avp.api.registry.AVPDeferredBlockRegistry;

public class AVPTempleBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPTempleBlockRegistry INSTANCE = new AVPTempleBlockRegistry();

    public final BlockHolderSet brick;

    public final BLHolder<Block> brickChestburster;

    public final BLHolder<Block> brickFacehugger;

    public final BlockHolderSet brickSingle;

    public final BlockHolderSet floor;

    public final BLHolder<Block> skulls;

    public final BlockHolderSet tile;

    public final BLHolder<Block> wallBase;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("temple_"));
    }

    private AVPTempleBlockRegistry() {
        var brickProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3, 6);
        var skullProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK).strength(3, 6);

        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(brickProperties), ironTier);
        brick = registerBlockHolderSet(new BlockHolderSetData(brickProperties, brickBlockData));

        brickChestburster = createHolder("brick_chestburster", BlockModelData.cube(brickProperties), ironTier);
        brickFacehugger = createHolder("brick_facehugger", BlockModelData.cube(brickProperties), ironTier);

        var brickSingleBlockData = new BlockData("brick_single", BlockModelData.cube(brickProperties), ironTier);
        brickSingle = registerBlockHolderSet(new BlockHolderSetData(brickProperties, brickSingleBlockData));

        var floorBlockData = new BlockData("floor", BlockModelData.cube(brickProperties), ironTier);
        floor = registerBlockHolderSet(new BlockHolderSetData(brickProperties, floorBlockData));

        skulls = createHolder("skulls", BlockModelData.cube(skullProperties), ironTier);

        var tileBlockData = new BlockData("tile", BlockModelData.cube(brickProperties), ironTier);
        tile = registerBlockHolderSet(new BlockHolderSetData(brickProperties, tileBlockData));

        wallBase = createHolder("wall_base", BlockModelData.cube(brickProperties), ironTier);
    }
}
