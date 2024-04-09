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

public class AVPIndustrialBlocks {

    public static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public static final BlockBehaviour.Properties INDUSTRIAL_GLASS_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.GLASS
    );

    public static final GameObject<Block> BRICK;

    public static final GameObject<Block> BRICK_SLAB;

    public static final GameObject<Block> BRICK_STAIRS;

    public static final GameObject<Block> FLOOR_GRILL;

    public static final GameObject<Block> GLASS;

    public static final GameObject<Block> LAMP;

    public static final GameObject<Block> METAL_PANEL_0;

    public static final GameObject<Block> METAL_PANEL_0_SLAB;

    public static final GameObject<Block> METAL_PANEL_0_STAIRS;

    public static final GameObject<Block> METAL_PANEL_1;

    public static final GameObject<Block> METAL_PANEL_1_SLAB;

    public static final GameObject<Block> METAL_PANEL_1_STAIRS;

    public static final GameObject<Block> METAL_PANEL_2;

    public static final GameObject<Block> METAL_PANEL_2_SLAB;

    public static final GameObject<Block> METAL_PANEL_2_STAIRS;

    public static final GameObject<Block> VENT;

    public static final GameObject<Block> WALL;

    public static final GameObject<Block> WALL_SLAB;

    public static final GameObject<Block> WALL_STAIRS;

    public static final GameObject<Block> WALL_HAZARD;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("industrial_" + name, builder);
    }

    private AVPIndustrialBlocks() {}

    static {
        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, METAL_PROPERTIES).tags(stoneOrMetal);

        BRICK = register("brick", pickProps.get());
        BRICK_SLAB = register("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = register("brick_stairs", stairProps.apply(BRICK));

        FLOOR_GRILL = register("floor_grill", pickProps.get());
        GLASS = register("glass", BlockDataUtils.transparent(INDUSTRIAL_GLASS_PROPERTIES).tags(stoneOrMetal));
        LAMP = register("lamp", pickProps.get());

        METAL_PANEL_0 = register("metal_panel_0", pickProps.get());
        METAL_PANEL_0_SLAB = register("metal_panel_0_slab", slabProps.apply(METAL_PANEL_0));
        METAL_PANEL_0_STAIRS = register("metal_panel_0_stairs", stairProps.apply(METAL_PANEL_0));

        METAL_PANEL_1 = register("metal_panel_1", pickProps.get());
        METAL_PANEL_1_SLAB = register("metal_panel_1_slab", slabProps.apply(METAL_PANEL_1));
        METAL_PANEL_1_STAIRS = register("metal_panel_1_stairs", stairProps.apply(METAL_PANEL_1));

        METAL_PANEL_2 = register("metal_panel_2", pickProps.get());
        METAL_PANEL_2_SLAB = register("metal_panel_2_slab", slabProps.apply(METAL_PANEL_2));
        METAL_PANEL_2_STAIRS = register("metal_panel_2_stairs", stairProps.apply(METAL_PANEL_2));

        VENT = register("vent", pickProps.get());

        WALL = register("wall", pickProps.get());
        WALL_SLAB = register("wall_slab", slabProps.apply(WALL));
        WALL_STAIRS = register("wall_stairs", stairProps.apply(WALL));

        WALL_HAZARD = register("wall_hazard", BlockDataUtils.rotatedPillar(METAL_PROPERTIES).tags(stoneOrMetal));
    }
}
