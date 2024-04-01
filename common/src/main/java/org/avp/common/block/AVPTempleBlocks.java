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
public class AVPTempleBlocks {

    public static final BlockBehaviour.Properties BRICK_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(3.0F, 6.0F);

    public static final BlockBehaviour.Properties SKULLS_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.BONE_BLOCK
    )
        .strength(3.0F, 6.0F);

    public static final GameObject<Block> BRICK;

    public static final GameObject<Block> BRICK_SLAB;

    public static final GameObject<Block> BRICK_STAIRS;

    public static final GameObject<Block> BRICK_WALL;

    public static final GameObject<Block> BRICK_CHESTBURSTER;

    public static final GameObject<Block> BRICK_FACEHUGGER;

    public static final GameObject<Block> BRICK_SINGLE;

    public static final GameObject<Block> BRICK_SINGLE_SLAB;

    public static final GameObject<Block> BRICK_SINGLE_STAIRS;

    public static final GameObject<Block> BRICK_SINGLE_WALL;

    public static final GameObject<Block> FLOOR;

    public static final GameObject<Block> FLOOR_SLAB;

    public static final GameObject<Block> FLOOR_STAIRS;

    public static final GameObject<Block> FLOOR_WALL;

    public static final GameObject<Block> SKULLS;

    public static final GameObject<Block> TILE;

    public static final GameObject<Block> TILE_SLAB;

    public static final GameObject<Block> TILE_STAIRS;

    public static final GameObject<Block> TILE_WALL;

    public static final GameObject<Block> WALL_BASE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("temple_" + name, builder);
    }

    private AVPTempleBlocks() {}

    static {
        var stone = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(BRICK_PROPERTIES).tags(stone);

        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, BRICK_PROPERTIES).tags(stone);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, BRICK_PROPERTIES).tags(stone);
        Function<GameObject<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, BRICK_PROPERTIES).tags(stone);

        BRICK = register("brick", pickProps.get());
        BRICK_SLAB = register("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = register("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = register("brick_wall", wallProps.apply(BRICK));

        BRICK_CHESTBURSTER = register("brick_chestburster", pickProps.get());
        BRICK_FACEHUGGER = register("brick_facehugger", pickProps.get());

        BRICK_SINGLE = register("brick_single", pickProps.get());
        BRICK_SINGLE_SLAB = register("brick_single_slab", slabProps.apply(BRICK_SINGLE));
        BRICK_SINGLE_STAIRS = register("brick_single_stairs", stairProps.apply(BRICK_SINGLE));
        BRICK_SINGLE_WALL = register("brick_single_wall", wallProps.apply(BRICK_SINGLE));

        FLOOR = register("floor", pickProps.get());
        FLOOR_SLAB = register("floor_slab", slabProps.apply(FLOOR));
        FLOOR_STAIRS = register("floor_stairs", stairProps.apply(FLOOR));
        FLOOR_WALL = register("floor_wall", wallProps.apply(FLOOR));

        SKULLS = register("skulls", BlockData.simple(SKULLS_PROPERTIES).tags(stone));

        TILE = register("tile", pickProps.get());
        TILE_SLAB = register("tile_slab", slabProps.apply(TILE));
        TILE_STAIRS = register("tile_stairs", stairProps.apply(TILE));
        TILE_WALL = register("tile_wall", wallProps.apply(TILE));

        WALL_BASE = register("wall_base", pickProps.get());
    }
}
