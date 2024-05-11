package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPIndustrialBlocks extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialBlocks INSTANCE = new AVPIndustrialBlocks();

    public final Holder<Block> brick;

    public final Holder<Block> brickSlab;

    public final Holder<Block> brickStairs;

    public final Holder<Block> floorGrill;

    public final Holder<Block> lamp;

    public final Holder<Block> metalPanel0;

    public final Holder<Block> metalPanel0Slab;

    public final Holder<Block> metalPanel0Stairs;

    public final Holder<Block> metalPanel1;

    public final Holder<Block> metalPanel1Slab;

    public final Holder<Block> metalPanel1Stairs;

    public final Holder<Block> metalPanel2;

    public final Holder<Block> metalPanel2Slab;

    public final Holder<Block> metalPanel2Stairs;

    public final Holder<Block> vent;

    public final Holder<Block> wall;

    public final Holder<Block> wallSlab;

    public final Holder<Block> wallStairs;

    public final Holder<Block> wallHazard;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("industrial_" + registryName, blockDataBuilder);
    }

    private AVPIndustrialBlocks() {
        var metalProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK);

        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(metalProperties).tags(stoneOrMetal);
        Function<Holder<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, metalProperties).tags(stoneOrMetal);
        Function<Holder<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, metalProperties).tags(stoneOrMetal);

        brick = createHolder("brick", pickProps.get());
        brickSlab = createHolder("brick_slab", slabProps.apply(brick));
        brickStairs = createHolder("brick_stairs", stairProps.apply(brick));

        floorGrill = createHolder("floor_grill", pickProps.get());

        lamp = createHolder("lamp", pickProps.get());

        metalPanel0 = createHolder("metal_panel_0", pickProps.get());
        metalPanel0Slab = createHolder("metal_panel_0_slab", slabProps.apply(metalPanel0));
        metalPanel0Stairs = createHolder("metal_panel_0_stairs", stairProps.apply(metalPanel0));

        metalPanel1 = createHolder("metal_panel_1", pickProps.get());
        metalPanel1Slab = createHolder("metal_panel_1_slab", slabProps.apply(metalPanel1));
        metalPanel1Stairs = createHolder("metal_panel_1_stairs", stairProps.apply(metalPanel1));

        metalPanel2 = createHolder("metal_panel_2", pickProps.get());
        metalPanel2Slab = createHolder("metal_panel_2_slab", slabProps.apply(metalPanel2));
        metalPanel2Stairs = createHolder("metal_panel_2_stairs", stairProps.apply(metalPanel2));

        vent = createHolder("vent", pickProps.get());

        wall = createHolder("wall", pickProps.get());
        wallSlab = createHolder("wall_slab", slabProps.apply(wall));
        wallStairs = createHolder("wall_stairs", stairProps.apply(wall));

        wallHazard = createHolder("wall_hazard", BlockDataUtils.rotatedPillar(metalProperties).tags(stoneOrMetal));
    }
}
