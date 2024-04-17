package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPIndustrialBlocks extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialBlocks INSTANCE = new AVPIndustrialBlocks();

    public final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public final BlockBehaviour.Properties INDUSTRIAL_GLASS_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.GLASS
    );

    public final GameObject<Block> BRICK;

    public final GameObject<Block> BRICK_SLAB;

    public final GameObject<Block> BRICK_STAIRS;

    public final GameObject<Block> FLOOR_GRILL;

    public final GameObject<Block> GLASS;

    public final GameObject<Block> LAMP;

    public final GameObject<Block> METAL_PANEL_0;

    public final GameObject<Block> METAL_PANEL_0_SLAB;

    public final GameObject<Block> METAL_PANEL_0_STAIRS;

    public final GameObject<Block> METAL_PANEL_1;

    public final GameObject<Block> METAL_PANEL_1_SLAB;

    public final GameObject<Block> METAL_PANEL_1_STAIRS;

    public final GameObject<Block> METAL_PANEL_2;

    public final GameObject<Block> METAL_PANEL_2_SLAB;

    public final GameObject<Block> METAL_PANEL_2_STAIRS;

    public final GameObject<Block> VENT;

    public final GameObject<Block> WALL;

    public final GameObject<Block> WALL_SLAB;

    public final GameObject<Block> WALL_STAIRS;

    public final GameObject<Block> WALL_HAZARD;

    @Override
    protected GameObject<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("industrial_" + registryName, blockDataBuilder);
    }

    private AVPIndustrialBlocks() {
        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, METAL_PROPERTIES).tags(stoneOrMetal);

        BRICK = createHolder("brick", pickProps.get());
        BRICK_SLAB = createHolder("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = createHolder("brick_stairs", stairProps.apply(BRICK));

        FLOOR_GRILL = createHolder("floor_grill", pickProps.get());
        GLASS = createHolder("glass", BlockDataUtils.transparent(INDUSTRIAL_GLASS_PROPERTIES).tags(stoneOrMetal));
        LAMP = createHolder("lamp", pickProps.get());

        METAL_PANEL_0 = createHolder("metal_panel_0", pickProps.get());
        METAL_PANEL_0_SLAB = createHolder("metal_panel_0_slab", slabProps.apply(METAL_PANEL_0));
        METAL_PANEL_0_STAIRS = createHolder("metal_panel_0_stairs", stairProps.apply(METAL_PANEL_0));

        METAL_PANEL_1 = createHolder("metal_panel_1", pickProps.get());
        METAL_PANEL_1_SLAB = createHolder("metal_panel_1_slab", slabProps.apply(METAL_PANEL_1));
        METAL_PANEL_1_STAIRS = createHolder("metal_panel_1_stairs", stairProps.apply(METAL_PANEL_1));

        METAL_PANEL_2 = createHolder("metal_panel_2", pickProps.get());
        METAL_PANEL_2_SLAB = createHolder("metal_panel_2_slab", slabProps.apply(METAL_PANEL_2));
        METAL_PANEL_2_STAIRS = createHolder("metal_panel_2_stairs", stairProps.apply(METAL_PANEL_2));

        VENT = createHolder("vent", pickProps.get());

        WALL = createHolder("wall", pickProps.get());
        WALL_SLAB = createHolder("wall_slab", slabProps.apply(WALL));
        WALL_STAIRS = createHolder("wall_stairs", stairProps.apply(WALL));

        WALL_HAZARD = createHolder("wall_hazard", BlockDataUtils.rotatedPillar(METAL_PROPERTIES).tags(stoneOrMetal));
    }
}
