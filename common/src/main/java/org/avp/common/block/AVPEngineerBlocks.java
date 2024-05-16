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

public class AVPEngineerBlocks extends AVPDeferredBlockRegistry {

    public static final AVPEngineerBlocks INSTANCE = new AVPEngineerBlocks();

    public final Holder<Block> brick;

    public final Holder<Block> brickSlab;

    public final Holder<Block> brickStairs;

    public final Holder<Block> brickWall;

    public final Holder<Block> brick1;

    public final Holder<Block> brick1Slab;

    public final Holder<Block> brick1Stairs;

    public final Holder<Block> brick1Wall;

    public final Holder<Block> brick2;

    public final Holder<Block> brick2Slab;

    public final Holder<Block> brick2Stairs;

    public final Holder<Block> brick2Wall;

    public final Holder<Block> brick3;

    public final Holder<Block> brick3Slab;

    public final Holder<Block> brick3Stairs;

    public final Holder<Block> brick3Wall;

    public final Holder<Block> column1;

    public final Holder<Block> column2;

    public final Holder<Block> floor;

    public final Holder<Block> gravel;

    public final Holder<Block> material0;

    public final Holder<Block> material0Slab;

    public final Holder<Block> material0Stairs;

    public final Holder<Block> material0Wall;

    public final Holder<Block> material1;

    public final Holder<Block> material1Slab;

    public final Holder<Block> material1Stairs;

    public final Holder<Block> material1Wall;

    public final Holder<Block> material2;

    public final Holder<Block> material2Slab;

    public final Holder<Block> material2Stairs;

    public final Holder<Block> material2Wall;

    public final Holder<Block> rock;

    public final Holder<Block> rock1;

    public final Holder<Block> rock2;

    public final Holder<Block> rock3;

    public final Holder<Block> wall;

    public final Holder<Block> wallSlab;

    public final Holder<Block> wallStairs;

    public final Holder<Block> wallWall;

    public final Holder<Block> wall1;

    public final Holder<Block> wall1Slab;

    public final Holder<Block> wall1Stairs;

    public final Holder<Block> wall1Wall;

    public final Holder<Block> wall2;

    public final Holder<Block> wall2Slab;

    public final Holder<Block> wall2Stairs;

    public final Holder<Block> wall2Wall;

    public final Holder<Block> wall3;

    public final Holder<Block> wall3Slab;

    public final Holder<Block> wall3Stairs;

    public final Holder<Block> wall3Wall;

    public final Holder<Block> wall4;

    public final Holder<Block> wall4Slab;

    public final Holder<Block> wall4Stairs;

    public final Holder<Block> wall4Wall;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("engineer_ship_" + registryName, blockDataBuilder);
    }

    private AVPEngineerBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(100, 1800);

        var soft = List.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.NEEDS_DIAMOND_TOOL);
        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(properties).blockTags(stoneOrMetal);
        Supplier<BlockData.Builder> shovelProps = () -> BlockData.simple(properties).blockTags(soft);
        Function<Holder<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, properties).blockTags(stoneOrMetal);
        Function<Holder<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, properties).blockTags(stoneOrMetal);
        Function<Holder<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, properties).blockTags(stoneOrMetal);

        brick = createHolder("brick", pickProps.get());
        brickSlab = createHolder("brick_slab", slabProps.apply(brick));
        brickStairs = createHolder("brick_stairs", stairProps.apply(brick));
        brickWall = createHolder("brick_wall", wallProps.apply(brick));

        brick1 = createHolder("brick_1", BlockData.simple(properties));
        brick1Slab = createHolder("brick_1_slab", slabProps.apply(brick1));
        brick1Stairs = createHolder("brick_1_stairs", stairProps.apply(brick1));
        brick1Wall = createHolder("brick_1_wall", wallProps.apply(brick1));

        brick2 = createHolder("brick_2", pickProps.get());
        brick2Slab = createHolder("brick_2_slab", slabProps.apply(brick2));
        brick2Stairs = createHolder("brick_2_stairs", stairProps.apply(brick2));
        brick2Wall = createHolder("brick_2_wall", wallProps.apply(brick2));

        brick3 = createHolder("brick_3", pickProps.get());
        brick3Slab = createHolder("brick_3_slab", slabProps.apply(brick3));
        brick3Stairs = createHolder("brick_3_stairs", stairProps.apply(brick3));
        brick3Wall = createHolder("brick_3_wall", wallProps.apply(brick3));

        column1 = createHolder("column_1", pickProps.get());
        column2 = createHolder("column_2", pickProps.get());
        floor = createHolder("floor", shovelProps.get());
        gravel = createHolder("gravel", shovelProps.get());

        material0 = createHolder("material_0", pickProps.get());
        material0Slab = createHolder("material_0_slab", slabProps.apply(material0));
        material0Stairs = createHolder("material_0_stairs", stairProps.apply(material0));
        material0Wall = createHolder("material_0_wall", wallProps.apply(material0));

        material1 = createHolder("material_1", pickProps.get());
        material1Slab = createHolder("material_1_slab", slabProps.apply(material1));
        material1Stairs = createHolder("material_1_stairs", stairProps.apply(material1));
        material1Wall = createHolder("material_1_wall", wallProps.apply(material1));

        material2 = createHolder("material_2", pickProps.get());
        material2Slab = createHolder("material_2_slab", slabProps.apply(material2));
        material2Stairs = createHolder("material_2_stairs", stairProps.apply(material2));
        material2Wall = createHolder("material_2_wall", wallProps.apply(material2));

        rock = createHolder("rock", pickProps.get());
        rock1 = createHolder("rock_1", pickProps.get());
        rock2 = createHolder("rock_2", pickProps.get());
        rock3 = createHolder("rock_3", pickProps.get());

        wall = createHolder("wall", pickProps.get());
        wallSlab = createHolder("wall_slab", slabProps.apply(wall));
        wallStairs = createHolder("wall_stairs", stairProps.apply(wall));
        wallWall = createHolder("wall_wall", wallProps.apply(wall));

        wall1 = createHolder("wall_1", pickProps.get());
        wall1Slab = createHolder("wall_1_slab", slabProps.apply(wall1));
        wall1Stairs = createHolder("wall_1_stairs", stairProps.apply(wall1));
        wall1Wall = createHolder("wall_1_wall", wallProps.apply(wall1));

        wall2 = createHolder("wall_2", pickProps.get());
        wall2Slab = createHolder("wall_2_slab", slabProps.apply(wall2));
        wall2Stairs = createHolder("wall_2_stairs", stairProps.apply(wall2));
        wall2Wall = createHolder("wall_2_wall", wallProps.apply(wall2));

        wall3 = createHolder("wall_3", pickProps.get());
        wall3Slab = createHolder("wall_3_slab", slabProps.apply(wall3));
        wall3Stairs = createHolder("wall_3_stairs", stairProps.apply(wall3));
        wall3Wall = createHolder("wall_3_wall", wallProps.apply(wall3));

        wall4 = createHolder("wall_4", pickProps.get());
        wall4Slab = createHolder("wall_4_slab", slabProps.apply(wall4));
        wall4Stairs = createHolder("wall_4_stairs", stairProps.apply(wall4));
        wall4Wall = createHolder("wall_4_wall", wallProps.apply(wall4));
    }

}
