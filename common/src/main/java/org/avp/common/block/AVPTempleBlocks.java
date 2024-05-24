package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockHolderSet;
import org.avp.api.block.BlockHolderSetData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

import java.util.Set;

public class AVPTempleBlocks extends AVPDeferredBlockRegistry {

    public static final AVPTempleBlocks INSTANCE = new AVPTempleBlocks();

    public final BlockHolderSet brick;

    public final Holder<Block> brickChestburster;

    public final Holder<Block> brickFacehugger;

    public final BlockHolderSet brickSingle;

    public final BlockHolderSet floor;

    public final Holder<Block> skulls;

    public final BlockHolderSet tile;

    public final Holder<Block> wallBase;

    @Override
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("temple_"));
    }

    private AVPTempleBlocks() {
        var brickProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3, 6);
        var skullProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK).strength(3, 6);

        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(brickProperties), ironTier);
        brick = registerBlockHolderSet(new BlockHolderSetData(brickProperties, brickBlockData));

        brickChestburster = createHolder(new BlockData("brick_chestburster", BlockModelData.cube(brickProperties), ironTier));
        brickFacehugger = createHolder(new BlockData("brick_facehugger", BlockModelData.cube(brickProperties), ironTier));

        var brickSingleBlockData = new BlockData("brick_single", BlockModelData.cube(brickProperties), ironTier);
        brickSingle = registerBlockHolderSet(new BlockHolderSetData(brickProperties, brickSingleBlockData));

        var floorBlockData = new BlockData("floor", BlockModelData.cube(brickProperties), ironTier);
        floor = registerBlockHolderSet(new BlockHolderSetData(brickProperties, floorBlockData));

        skulls = createHolder(new BlockData("skulls", BlockModelData.cube(skullProperties), ironTier));

        var tileBlockData = new BlockData("tile", BlockModelData.cube(brickProperties), ironTier);
        tile = registerBlockHolderSet(new BlockHolderSetData(brickProperties, tileBlockData));

        wallBase = createHolder(new BlockData("wall_base", BlockModelData.cube(brickProperties), ironTier));
    }
}
