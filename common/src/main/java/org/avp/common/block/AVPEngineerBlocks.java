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

public class AVPEngineerBlocks extends AVPDeferredBlockRegistry {

    public static final AVPEngineerBlocks INSTANCE = new AVPEngineerBlocks();

    public final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    )
        .strength(100.0F, 1800.0F);

    public final GameObject<Block> BRICK;

    public final GameObject<Block> BRICK_SLAB;

    public final GameObject<Block> BRICK_STAIRS;

    public final GameObject<Block> BRICK_WALL;

    public final GameObject<Block> BRICK_1;

    public final GameObject<Block> BRICK_1_SLAB;

    public final GameObject<Block> BRICK_1_STAIRS;

    public final GameObject<Block> BRICK_1_WALL;

    public final GameObject<Block> BRICK_2;

    public final GameObject<Block> BRICK_2_SLAB;

    public final GameObject<Block> BRICK_2_STAIRS;

    public final GameObject<Block> BRICK_2_WALL;

    public final GameObject<Block> BRICK_3;

    public final GameObject<Block> BRICK_3_SLAB;

    public final GameObject<Block> BRICK_3_STAIRS;

    public final GameObject<Block> BRICK_3_WALL;

    public final GameObject<Block> COLUMN_1;

    public final GameObject<Block> COLUMN_2;

    public final GameObject<Block> FLOOR;

    public final GameObject<Block> GRAVEL;

    public final GameObject<Block> MATERIAL_0;

    public final GameObject<Block> MATERIAL_0_SLAB;

    public final GameObject<Block> MATERIAL_0_STAIRS;

    public final GameObject<Block> MATERIAL_0_WALL;

    public final GameObject<Block> MATERIAL_1;

    public final GameObject<Block> MATERIAL_1_SLAB;

    public final GameObject<Block> MATERIAL_1_STAIRS;

    public final GameObject<Block> MATERIAL_1_WALL;

    public final GameObject<Block> MATERIAL_2;

    public final GameObject<Block> MATERIAL_2_SLAB;

    public final GameObject<Block> MATERIAL_2_STAIRS;

    public final GameObject<Block> MATERIAL_2_WALL;

    public final GameObject<Block> ROCK;

    public final GameObject<Block> ROCK_1;

    public final GameObject<Block> ROCK_2;

    public final GameObject<Block> ROCK_3;

    public final GameObject<Block> WALL;

    public final GameObject<Block> WALL_SLAB;

    public final GameObject<Block> WALL_STAIRS;

    public final GameObject<Block> WALL_WALL;

    public final GameObject<Block> WALL_1;

    public final GameObject<Block> WALL_1_SLAB;

    public final GameObject<Block> WALL_1_STAIRS;

    public final GameObject<Block> WALL_1_WALL;

    public final GameObject<Block> WALL_2;

    public final GameObject<Block> WALL_2_SLAB;

    public final GameObject<Block> WALL_2_STAIRS;

    public final GameObject<Block> WALL_2_WALL;

    public final GameObject<Block> WALL_3;

    public final GameObject<Block> WALL_3_SLAB;

    public final GameObject<Block> WALL_3_STAIRS;

    public final GameObject<Block> WALL_3_WALL;

    public final GameObject<Block> WALL_4;

    public final GameObject<Block> WALL_4_SLAB;

    public final GameObject<Block> WALL_4_STAIRS;

    public final GameObject<Block> WALL_4_WALL;

    @Override
    protected GameObject<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("engineer_ship_" + registryName, blockDataBuilder);
    }

    private AVPEngineerBlocks() {
        super();
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

        BRICK = createHolder("brick", pickProps.get());
        BRICK_SLAB = createHolder("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = createHolder("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = createHolder("brick_wall", wallProps.apply(BRICK));

        BRICK_1 = createHolder("brick_1", BlockData.simple(METAL_PROPERTIES));
        BRICK_1_SLAB = createHolder("brick_1_slab", slabProps.apply(BRICK_1));
        BRICK_1_STAIRS = createHolder("brick_1_stairs", stairProps.apply(BRICK_1));
        BRICK_1_WALL = createHolder("brick_1_wall", wallProps.apply(BRICK_1));

        BRICK_2 = createHolder("brick_2", pickProps.get());
        BRICK_2_SLAB = createHolder("brick_2_slab", slabProps.apply(BRICK_2));
        BRICK_2_STAIRS = createHolder("brick_2_stairs", stairProps.apply(BRICK_2));
        BRICK_2_WALL = createHolder("brick_2_wall", wallProps.apply(BRICK_2));

        BRICK_3 = createHolder("brick_3", pickProps.get());
        BRICK_3_SLAB = createHolder("brick_3_slab", slabProps.apply(BRICK_3));
        BRICK_3_STAIRS = createHolder("brick_3_stairs", stairProps.apply(BRICK_3));
        BRICK_3_WALL = createHolder("brick_3_wall", wallProps.apply(BRICK_3));

        COLUMN_1 = createHolder("column_1", pickProps.get());
        COLUMN_2 = createHolder("column_2", pickProps.get());
        FLOOR = createHolder("floor", shovelProps.get());
        GRAVEL = createHolder("gravel", shovelProps.get());

        MATERIAL_0 = createHolder("material_0", pickProps.get());
        MATERIAL_0_SLAB = createHolder("material_0_slab", slabProps.apply(MATERIAL_0));
        MATERIAL_0_STAIRS = createHolder("material_0_stairs", stairProps.apply(MATERIAL_0));
        MATERIAL_0_WALL = createHolder("material_0_wall", wallProps.apply(MATERIAL_0));

        MATERIAL_1 = createHolder("material_1", pickProps.get());
        MATERIAL_1_SLAB = createHolder("material_1_slab", slabProps.apply(MATERIAL_1));
        MATERIAL_1_STAIRS = createHolder("material_1_stairs", stairProps.apply(MATERIAL_1));
        MATERIAL_1_WALL = createHolder("material_1_wall", wallProps.apply(MATERIAL_1));

        MATERIAL_2 = createHolder("material_2", pickProps.get());
        MATERIAL_2_SLAB = createHolder("material_2_slab", slabProps.apply(MATERIAL_2));
        MATERIAL_2_STAIRS = createHolder("material_2_stairs", stairProps.apply(MATERIAL_2));
        MATERIAL_2_WALL = createHolder("material_2_wall", wallProps.apply(MATERIAL_2));

        ROCK = createHolder("rock", pickProps.get());
        ROCK_1 = createHolder("rock_1", pickProps.get());
        ROCK_2 = createHolder("rock_2", pickProps.get());
        ROCK_3 = createHolder("rock_3", pickProps.get());

        WALL = createHolder("wall", pickProps.get());
        WALL_SLAB = createHolder("wall_slab", slabProps.apply(WALL));
        WALL_STAIRS = createHolder("wall_stairs", stairProps.apply(WALL));
        WALL_WALL = createHolder("wall_wall", wallProps.apply(WALL));

        WALL_1 = createHolder("wall_1", pickProps.get());
        WALL_1_SLAB = createHolder("wall_1_slab", slabProps.apply(WALL_1));
        WALL_1_STAIRS = createHolder("wall_1_stairs", stairProps.apply(WALL_1));
        WALL_1_WALL = createHolder("wall_1_wall", wallProps.apply(WALL_1));

        WALL_2 = createHolder("wall_2", pickProps.get());
        WALL_2_SLAB = createHolder("wall_2_slab", slabProps.apply(WALL_2));
        WALL_2_STAIRS = createHolder("wall_2_stairs", stairProps.apply(WALL_2));
        WALL_2_WALL = createHolder("wall_2_wall", wallProps.apply(WALL_2));

        WALL_3 = createHolder("wall_3", pickProps.get());
        WALL_3_SLAB = createHolder("wall_3_slab", slabProps.apply(WALL_3));
        WALL_3_STAIRS = createHolder("wall_3_stairs", stairProps.apply(WALL_3));
        WALL_3_WALL = createHolder("wall_3_wall", wallProps.apply(WALL_3));

        WALL_4 = createHolder("wall_4", pickProps.get());
        WALL_4_SLAB = createHolder("wall_4_slab", slabProps.apply(WALL_4));
        WALL_4_STAIRS = createHolder("wall_4_stairs", stairProps.apply(WALL_4));
        WALL_4_WALL = createHolder("wall_4_wall", wallProps.apply(WALL_4));
    }

}
