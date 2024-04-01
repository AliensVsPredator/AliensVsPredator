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

/**
 * @author Boston Vanseghi
 */
public class AVPEngineerBlocks {

    public static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    )
        .strength(100.0F, 1800.0F);

    public static final GameObject<Block> BRICK;

    public static final GameObject<Block> BRICK_SLAB;

    public static final GameObject<Block> BRICK_STAIRS;

    public static final GameObject<Block> BRICK_WALL;

    public static final GameObject<Block> BRICK_1;

    public static final GameObject<Block> BRICK_1_SLAB;

    public static final GameObject<Block> BRICK_1_STAIRS;

    public static final GameObject<Block> BRICK_1_WALL;

    public static final GameObject<Block> BRICK_2;

    public static final GameObject<Block> BRICK_2_SLAB;

    public static final GameObject<Block> BRICK_2_STAIRS;

    public static final GameObject<Block> BRICK_2_WALL;

    public static final GameObject<Block> BRICK_3;

    public static final GameObject<Block> BRICK_3_SLAB;

    public static final GameObject<Block> BRICK_3_STAIRS;

    public static final GameObject<Block> BRICK_3_WALL;

    public static final GameObject<Block> COLUMN_1;

    public static final GameObject<Block> COLUMN_2;

    public static final GameObject<Block> FLOOR;

    public static final GameObject<Block> GRAVEL;

    public static final GameObject<Block> MATERIAL_0;

    public static final GameObject<Block> MATERIAL_0_SLAB;

    public static final GameObject<Block> MATERIAL_0_STAIRS;

    public static final GameObject<Block> MATERIAL_0_WALL;

    public static final GameObject<Block> MATERIAL_1;

    public static final GameObject<Block> MATERIAL_1_SLAB;

    public static final GameObject<Block> MATERIAL_1_STAIRS;

    public static final GameObject<Block> MATERIAL_1_WALL;

    public static final GameObject<Block> MATERIAL_2;

    public static final GameObject<Block> MATERIAL_2_SLAB;

    public static final GameObject<Block> MATERIAL_2_STAIRS;

    public static final GameObject<Block> MATERIAL_2_WALL;

    public static final GameObject<Block> ROCK;

    public static final GameObject<Block> ROCK_1;

    public static final GameObject<Block> ROCK_2;

    public static final GameObject<Block> ROCK_3;

    public static final GameObject<Block> WALL;

    public static final GameObject<Block> WALL_SLAB;

    public static final GameObject<Block> WALL_STAIRS;

    public static final GameObject<Block> WALL_WALL;

    public static final GameObject<Block> WALL_1;

    public static final GameObject<Block> WALL_1_SLAB;

    public static final GameObject<Block> WALL_1_STAIRS;

    public static final GameObject<Block> WALL_1_WALL;

    public static final GameObject<Block> WALL_2;

    public static final GameObject<Block> WALL_2_SLAB;

    public static final GameObject<Block> WALL_2_STAIRS;

    public static final GameObject<Block> WALL_2_WALL;

    public static final GameObject<Block> WALL_3;

    public static final GameObject<Block> WALL_3_SLAB;

    public static final GameObject<Block> WALL_3_STAIRS;

    public static final GameObject<Block> WALL_3_WALL;

    public static final GameObject<Block> WALL_4;

    public static final GameObject<Block> WALL_4_SLAB;

    public static final GameObject<Block> WALL_4_STAIRS;

    public static final GameObject<Block> WALL_4_WALL;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("engineer_ship_" + name, builder);
    }

    private AVPEngineerBlocks() {}

    static {
        var soft = List.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.NEEDS_DIAMOND_TOOL);
        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(METAL_PROPERTIES).tags(stoneOrMetal);
        Supplier<BlockData.Builder> shovelProps = () -> BlockData.simple(METAL_PROPERTIES).tags(soft);
        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, METAL_PROPERTIES).tags(stoneOrMetal);
        Function<GameObject<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, METAL_PROPERTIES).tags(stoneOrMetal);

        BRICK = register("brick", pickProps.get());
        BRICK_SLAB = register("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = register("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = register("brick_wall", wallProps.apply(BRICK));

        BRICK_1 = register("brick_1", BlockData.simple(METAL_PROPERTIES));
        BRICK_1_SLAB = register("brick_1_slab", slabProps.apply(BRICK_1));
        BRICK_1_STAIRS = register("brick_1_stairs", stairProps.apply(BRICK_1));
        BRICK_1_WALL = register("brick_1_wall", wallProps.apply(BRICK_1));

        BRICK_2 = register("brick_2", pickProps.get());
        BRICK_2_SLAB = register("brick_2_slab", slabProps.apply(BRICK_2));
        BRICK_2_STAIRS = register("brick_2_stairs", stairProps.apply(BRICK_2));
        BRICK_2_WALL = register("brick_2_wall", wallProps.apply(BRICK_2));

        BRICK_3 = register("brick_3", pickProps.get());
        BRICK_3_SLAB = register("brick_3_slab", slabProps.apply(BRICK_3));
        BRICK_3_STAIRS = register("brick_3_stairs", stairProps.apply(BRICK_3));
        BRICK_3_WALL = register("brick_3_wall", wallProps.apply(BRICK_3));

        COLUMN_1 = register("column_1", pickProps.get());
        COLUMN_2 = register("column_2", pickProps.get());
        FLOOR = register("floor", shovelProps.get());
        GRAVEL = register("gravel", shovelProps.get());

        MATERIAL_0 = register("material_0", pickProps.get());
        MATERIAL_0_SLAB = register("material_0_slab", slabProps.apply(MATERIAL_0));
        MATERIAL_0_STAIRS = register("material_0_stairs", stairProps.apply(MATERIAL_0));
        MATERIAL_0_WALL = register("material_0_wall", wallProps.apply(MATERIAL_0));

        MATERIAL_1 = register("material_1", pickProps.get());
        MATERIAL_1_SLAB = register("material_1_slab", slabProps.apply(MATERIAL_1));
        MATERIAL_1_STAIRS = register("material_1_stairs", stairProps.apply(MATERIAL_1));
        MATERIAL_1_WALL = register("material_1_wall", wallProps.apply(MATERIAL_1));

        MATERIAL_2 = register("material_2", pickProps.get());
        MATERIAL_2_SLAB = register("material_2_slab", slabProps.apply(MATERIAL_2));
        MATERIAL_2_STAIRS = register("material_2_stairs", stairProps.apply(MATERIAL_2));
        MATERIAL_2_WALL = register("material_2_wall", wallProps.apply(MATERIAL_2));

        ROCK = register("rock", pickProps.get());
        ROCK_1 = register("rock_1", pickProps.get());
        ROCK_2 = register("rock_2", pickProps.get());
        ROCK_3 = register("rock_3", pickProps.get());

        WALL = register("wall", pickProps.get());
        WALL_SLAB = register("wall_slab", slabProps.apply(WALL));
        WALL_STAIRS = register("wall_stairs", stairProps.apply(WALL));
        WALL_WALL = register("wall_wall", wallProps.apply(WALL));

        WALL_1 = register("wall_1", pickProps.get());
        WALL_1_SLAB = register("wall_1_slab", slabProps.apply(WALL_1));
        WALL_1_STAIRS = register("wall_1_stairs", stairProps.apply(WALL_1));
        WALL_1_WALL = register("wall_1_wall", wallProps.apply(WALL_1));

        WALL_2 = register("wall_2", pickProps.get());
        WALL_2_SLAB = register("wall_2_slab", slabProps.apply(WALL_2));
        WALL_2_STAIRS = register("wall_2_stairs", stairProps.apply(WALL_2));
        WALL_2_WALL = register("wall_2_wall", wallProps.apply(WALL_2));

        WALL_3 = register("wall_3", pickProps.get());
        WALL_3_SLAB = register("wall_3_slab", slabProps.apply(WALL_3));
        WALL_3_STAIRS = register("wall_3_stairs", stairProps.apply(WALL_3));
        WALL_3_WALL = register("wall_3_wall", wallProps.apply(WALL_3));

        WALL_4 = register("wall_4", pickProps.get());
        WALL_4_SLAB = register("wall_4_slab", slabProps.apply(WALL_4));
        WALL_4_STAIRS = register("wall_4_stairs", stairProps.apply(WALL_4));
        WALL_4_WALL = register("wall_4_wall", wallProps.apply(WALL_4));
    }

}
