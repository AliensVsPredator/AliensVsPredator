package org.avp.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.GameObject;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;

public class AVPPaddingBlocks {

    public static final BlockBehaviour.Properties ORANGE_PADDING_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.ORANGE_WOOL
    );

    public static final BlockBehaviour.Properties WHITE_PADDING_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.WHITE_WOOL
    );

    public static final GameObject<Block> PADDING_ORANGE_PANEL;

    public static final GameObject<Block> PADDING_ORANGE_PIPES;

    public static final GameObject<Block> PADDING_ORANGE_SQUARE;

    public static final GameObject<Block> PADDING_ORANGE_SQUARE_SLAB;

    public static final GameObject<Block> PADDING_ORANGE_SQUARE_STAIRS;

    public static final GameObject<Block> PADDING_ORANGE_SQUARE_WALL;

    public static final GameObject<Block> PADDING_ORANGE_TILES;

    public static final GameObject<Block> PADDING_ORANGE_TILES_SLAB;

    public static final GameObject<Block> PADDING_ORANGE_TILES_STAIRS;

    public static final GameObject<Block> PADDING_ORANGE_TILES_WALL;

    public static final GameObject<Block> PADDING_WHITE_PANEL;

    public static final GameObject<Block> PADDING_WHITE_PIPES;

    public static final GameObject<Block> PADDING_WHITE_SQUARE;

    public static final GameObject<Block> PADDING_WHITE_SQUARE_SLAB;

    public static final GameObject<Block> PADDING_WHITE_SQUARE_STAIRS;

    public static final GameObject<Block> PADDING_WHITE_SQUARE_WALL;

    public static final GameObject<Block> PADDING_WHITE_TILES;

    public static final GameObject<Block> PADDING_WHITE_TILES_SLAB;

    public static final GameObject<Block> PADDING_WHITE_TILES_STAIRS;

    public static final GameObject<Block> PADDING_WHITE_TILES_WALL;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("padding_" + name, builder);
    }

    private AVPPaddingBlocks() {}

    static {
        PADDING_ORANGE_PANEL = register("orange_panel", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_PIPES = register("orange_pipes", BlockData.simple(ORANGE_PADDING_PROPERTIES));

        PADDING_ORANGE_SQUARE = register("orange_square", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_SQUARE_SLAB = register("orange_square_slab", BlockDataUtils.slab(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_SQUARE_STAIRS = register(
            "orange_square_stairs",
            BlockDataUtils.stairs(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES)
        );
        PADDING_ORANGE_SQUARE_WALL = register("orange_square_wall", BlockDataUtils.wall(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES));

        PADDING_ORANGE_TILES = register("orange_tiles", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_TILES_SLAB = register("orange_tiles_slab", BlockDataUtils.slab(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_TILES_STAIRS = register(
            "orange_tiles_stairs",
            BlockDataUtils.stairs(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES)
        );
        PADDING_ORANGE_TILES_WALL = register("orange_tiles_wall", BlockDataUtils.wall(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES));

        PADDING_WHITE_PANEL = register("white_panel", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_PIPES = register("white_pipes", BlockData.simple(WHITE_PADDING_PROPERTIES));

        PADDING_WHITE_SQUARE = register("white_square", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_SQUARE_SLAB = register("white_square_slab", BlockDataUtils.slab(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_SQUARE_STAIRS = register(
            "white_square_stairs",
            BlockDataUtils.stairs(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES)
        );
        PADDING_WHITE_SQUARE_WALL = register("white_square_wall", BlockDataUtils.wall(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES));

        PADDING_WHITE_TILES = register("white_tiles", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_SLAB = register("white_tiles_slab", BlockDataUtils.slab(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_STAIRS = register("white_tiles_stairs", BlockDataUtils.stairs(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_WALL = register("white_tiles_wall", BlockDataUtils.wall(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
    }
}
