package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockHolderSet;
import org.avp.api.block.BlockHolderSetData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPIndustrialBlocks extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialBlocks INSTANCE = new AVPIndustrialBlocks();

    public final BlockHolderSet brick;

    public final Holder<Block> floorGrill;

    public final Holder<Block> lamp;

    public final BlockHolderSet metalPanel0;

    public final BlockHolderSet metalPanel1;

    public final BlockHolderSet metalPanel2;

    public final Holder<Block> vent;

    public final BlockHolderSet wall;

    public final Holder<Block> wallHazard;

    @Override
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("industrial_"));
    }

    private AVPIndustrialBlocks() {
        var metalProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK);

        var stoneOrMetal = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(metalProperties), stoneOrMetal);
        brick = registerBlockHolderSet(new BlockHolderSetData(metalProperties, brickBlockData));

        floorGrill = createHolder(new BlockData("floor_grill", BlockModelData.cube(metalProperties), stoneOrMetal));

        lamp = createHolder(new BlockData("lamp", BlockModelData.cube(metalProperties), stoneOrMetal));

        var metalPanel0BlockData = new BlockData("metal_panel_0", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel0 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel0BlockData));

        var metalPanel1BlockData = new BlockData("metal_panel_1", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel1 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel1BlockData));

        var metalPanel2BlockData = new BlockData("metal_panel_2", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel2 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel2BlockData));

        vent = createHolder(new BlockData("vent", BlockModelData.cube(metalProperties), stoneOrMetal));

        var wallBlockData = new BlockData("wall", BlockModelData.cube(metalProperties), stoneOrMetal);
        wall = registerBlockHolderSet(new BlockHolderSetData(metalProperties, wallBlockData));

        wallHazard = createHolder(new BlockData("wall_hazard", BlockModelData.cube(metalProperties), stoneOrMetal));
    }
}
