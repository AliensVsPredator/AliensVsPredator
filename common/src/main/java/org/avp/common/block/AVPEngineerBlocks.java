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

public class AVPEngineerBlocks extends AVPDeferredBlockRegistry {

    public static final AVPEngineerBlocks INSTANCE = new AVPEngineerBlocks();

    public final BlockHolderSet brick;

    public final BlockHolderSet brick1;

    public final BlockHolderSet brick2;

    public final BlockHolderSet brick3;

    public final Holder<Block> column1;

    public final Holder<Block> column2;

    public final Holder<Block> floor;

    public final Holder<Block> gravel;

    public final BlockHolderSet material0;

    public final BlockHolderSet material1;

    public final BlockHolderSet material2;

    public final Holder<Block> rock;

    public final Holder<Block> rock1;

    public final Holder<Block> rock2;

    public final Holder<Block> rock3;

    public final BlockHolderSet wall;

    public final BlockHolderSet wall1;

    public final BlockHolderSet wall2;

    public final BlockHolderSet wall3;

    public final BlockHolderSet wall4;

    @Override
    protected Holder<Block> createHolder(BlockData blockData) {
        var prefixedBlockData = blockData.withRegistryName("engineer_ship_" + blockData.registryName());
        return super.createHolder(prefixedBlockData);
    }

    private AVPEngineerBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(100, 1800);

        var soft = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.NEEDS_DIAMOND_TOOL));
        var stoneOrMetalTags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(properties), stoneOrMetalTags);
        brick = registerBlockHolderSet(new BlockHolderSetData(properties, brickBlockData));

        var brick1BlockData = new BlockData("brick_1", BlockModelData.cube(properties), stoneOrMetalTags);
        brick1 = registerBlockHolderSet(new BlockHolderSetData(properties, brick1BlockData));

        var brick2BlockData = new BlockData("brick_2", BlockModelData.cube(properties), stoneOrMetalTags);
        brick2 = registerBlockHolderSet(new BlockHolderSetData(properties, brick2BlockData));

        var brick3BlockData = new BlockData("brick_3", BlockModelData.cube(properties), stoneOrMetalTags);
        brick3 = registerBlockHolderSet(new BlockHolderSetData(properties, brick3BlockData));

        column1 = createHolder(new BlockData("column_1", BlockModelData.cube(properties), stoneOrMetalTags));
        column2 = createHolder(new BlockData("column_2", BlockModelData.cube(properties), stoneOrMetalTags));
        floor = createHolder(new BlockData("floor", BlockModelData.cube(properties), soft));
        gravel = createHolder(new BlockData("gravel", BlockModelData.cube(properties), soft));

        var material0BlockData = new BlockData("material_0", BlockModelData.cube(properties), stoneOrMetalTags);
        material0 = registerBlockHolderSet(new BlockHolderSetData(properties, material0BlockData));

        var material1BlockData = new BlockData("material_1", BlockModelData.cube(properties), stoneOrMetalTags);
        material1 = registerBlockHolderSet(new BlockHolderSetData(properties, material1BlockData));

        var material2BlockData = new BlockData("material_2", BlockModelData.cube(properties), stoneOrMetalTags);
        material2 = registerBlockHolderSet(new BlockHolderSetData(properties, material2BlockData));

        rock = createHolder(new BlockData("rock", BlockModelData.cube(properties), stoneOrMetalTags));
        rock1 = createHolder(new BlockData("rock_1", BlockModelData.cube(properties), stoneOrMetalTags));
        rock2 = createHolder(new BlockData("rock_2", BlockModelData.cube(properties), stoneOrMetalTags));
        rock3 = createHolder(new BlockData("rock_3", BlockModelData.cube(properties), stoneOrMetalTags));

        var wallBlockData = new BlockData("wall", BlockModelData.cube(properties), stoneOrMetalTags);
        wall = registerBlockHolderSet(new BlockHolderSetData(properties, wallBlockData));

        var wall1BlockData = new BlockData("wall_1", BlockModelData.cube(properties), stoneOrMetalTags);
        wall1 = registerBlockHolderSet(new BlockHolderSetData(properties, wall1BlockData));

        var wall2BlockData = new BlockData("wall_2", BlockModelData.cube(properties), stoneOrMetalTags);
        wall2 = registerBlockHolderSet(new BlockHolderSetData(properties, wall2BlockData));

        var wall3BlockData = new BlockData("wall_3", BlockModelData.cube(properties), stoneOrMetalTags);
        wall3 = registerBlockHolderSet(new BlockHolderSetData(properties, wall3BlockData));

        var wall4BlockData = new BlockData("wall_4", BlockModelData.cube(properties), stoneOrMetalTags);
        wall4 = registerBlockHolderSet(new BlockHolderSetData(properties, wall4BlockData));
    }

}
