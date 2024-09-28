package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.OldBlockData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BlockHolderSetData;

/**
 * @deprecated
 */
public class AVPIndustrialBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialBlockRegistry INSTANCE = new AVPIndustrialBlockRegistry();

    public final BlockHolderSet brick;

    public final BLHolder<Block> floorGrill;

    public final BLHolder<Block> lamp;

    public final BlockHolderSet metalPanel0;

    public final BlockHolderSet metalPanel1;

    public final BlockHolderSet metalPanel2;

    public final BLHolder<Block> vent;

    public final BlockHolderSet wall;

    public final BLHolder<Block> wallHazard;

    @Override
    protected BLHolder<Block> createHolder(OldBlockData oldBlockData) {
        return super.createHolder(oldBlockData.withPrefixRegistryName("industrial_"));
    }

    private AVPIndustrialBlockRegistry() {
        var metalProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK);

        var stoneOrMetal = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));

        var brickBlockData = new OldBlockData("brick", BlockModelData.cube(metalProperties), stoneOrMetal);
        brick = registerBlockHolderSet(new BlockHolderSetData(metalProperties, brickBlockData));

        floorGrill = createHolder("floor_grill", BlockModelData.cube(metalProperties), stoneOrMetal);

        lamp = createHolder("lamp", BlockModelData.cube(metalProperties), stoneOrMetal);

        var metalPanel0BlockData = new OldBlockData("metal_panel_0", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel0 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel0BlockData));

        var metalPanel1BlockData = new OldBlockData("metal_panel_1", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel1 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel1BlockData));

        var metalPanel2BlockData = new OldBlockData("metal_panel_2", BlockModelData.cube(metalProperties), stoneOrMetal);
        metalPanel2 = registerBlockHolderSet(new BlockHolderSetData(metalProperties, metalPanel2BlockData));

        vent = createHolder("vent", BlockModelData.cube(metalProperties), stoneOrMetal);

        var wallBlockData = new OldBlockData("wall", BlockModelData.cube(metalProperties), stoneOrMetal);
        wall = registerBlockHolderSet(new BlockHolderSetData(metalProperties, wallBlockData));

        wallHazard = createHolder("wall_hazard", BlockModelData.rotatedPillar(metalProperties), stoneOrMetal);
    }
}
