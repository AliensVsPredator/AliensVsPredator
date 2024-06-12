package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BlockHolderSetData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;

public class AVPEngineerBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPEngineerBlockRegistry INSTANCE = new AVPEngineerBlockRegistry();

    public final BlockHolderSet brick;

    public final BlockHolderSet brick1;

    public final BlockHolderSet brick2;

    public final BlockHolderSet brick3;

    public final BLHolder<Block> column1;

    public final BLHolder<Block> column2;

    public final BLHolder<Block> floor;

    public final BLHolder<Block> gravel;

    public final BlockHolderSet material0;

    public final BlockHolderSet material1;

    public final BlockHolderSet material2;

    public final BLHolder<Block> rock;

    public final BLHolder<Block> rock1;

    public final BLHolder<Block> rock2;

    public final BLHolder<Block> rock3;

    public final BlockHolderSet wall;

    public final BlockHolderSet wall1;

    public final BlockHolderSet wall2;

    public final BlockHolderSet wall3;

    public final BlockHolderSet wall4;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        var prefixedBlockData = blockData.withRegistryName("engineer_ship_" + blockData.registryName());
        return super.createHolder(prefixedBlockData);
    }

    private AVPEngineerBlockRegistry() {
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

        column1 = createHolder("column_1", BlockModelData.cube(properties), stoneOrMetalTags);
        column2 = createHolder("column_2", BlockModelData.cube(properties), stoneOrMetalTags);
        floor = createHolder("floor", BlockModelData.cube(properties), soft);
        gravel = createHolder("gravel", BlockModelData.cube(properties), soft);

        var material0BlockData = new BlockData("material_0", BlockModelData.cube(properties), stoneOrMetalTags);
        material0 = registerBlockHolderSet(new BlockHolderSetData(properties, material0BlockData));

        var material1BlockData = new BlockData("material_1", BlockModelData.cube(properties), stoneOrMetalTags);
        material1 = registerBlockHolderSet(new BlockHolderSetData(properties, material1BlockData));

        var material2BlockData = new BlockData("material_2", BlockModelData.cube(properties), stoneOrMetalTags);
        material2 = registerBlockHolderSet(new BlockHolderSetData(properties, material2BlockData));

        rock = createHolder("rock", BlockModelData.cube(properties), stoneOrMetalTags);
        rock1 = createHolder("rock_1", BlockModelData.cube(properties), stoneOrMetalTags);
        rock2 = createHolder("rock_2", BlockModelData.cube(properties), stoneOrMetalTags);
        rock3 = createHolder("rock_3", BlockModelData.cube(properties), stoneOrMetalTags);

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
