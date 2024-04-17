package org.avp.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPPaddingBlocks extends AVPDeferredBlockRegistry {

    public static final AVPPaddingBlocks INSTANCE = new AVPPaddingBlocks();

    public final BlockBehaviour.Properties ORANGE_PADDING_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.ORANGE_WOOL
    );

    public final BlockBehaviour.Properties WHITE_PADDING_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.WHITE_WOOL
    );

    public final Holder<Block> PADDING_ORANGE_PANEL;

    public final Holder<Block> PADDING_ORANGE_PIPES;

    public final Holder<Block> PADDING_ORANGE_SQUARE;

    public final Holder<Block> PADDING_ORANGE_SQUARE_SLAB;

    public final Holder<Block> PADDING_ORANGE_SQUARE_STAIRS;

    public final Holder<Block> PADDING_ORANGE_SQUARE_WALL;

    public final Holder<Block> PADDING_ORANGE_TILES;

    public final Holder<Block> PADDING_ORANGE_TILES_SLAB;

    public final Holder<Block> PADDING_ORANGE_TILES_STAIRS;

    public final Holder<Block> PADDING_ORANGE_TILES_WALL;

    public final Holder<Block> PADDING_WHITE_PANEL;

    public final Holder<Block> PADDING_WHITE_PIPES;

    public final Holder<Block> PADDING_WHITE_SQUARE;

    public final Holder<Block> PADDING_WHITE_SQUARE_SLAB;

    public final Holder<Block> PADDING_WHITE_SQUARE_STAIRS;

    public final Holder<Block> PADDING_WHITE_SQUARE_WALL;

    public final Holder<Block> PADDING_WHITE_TILES;

    public final Holder<Block> PADDING_WHITE_TILES_SLAB;

    public final Holder<Block> PADDING_WHITE_TILES_STAIRS;

    public final Holder<Block> PADDING_WHITE_TILES_WALL;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("padding_" + registryName, blockDataBuilder);
    }

    private AVPPaddingBlocks() {
        PADDING_ORANGE_PANEL = createHolder("orange_panel", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_PIPES = createHolder("orange_pipes", BlockData.simple(ORANGE_PADDING_PROPERTIES));

        PADDING_ORANGE_SQUARE = createHolder("orange_square", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_SQUARE_SLAB = createHolder("orange_square_slab", BlockDataUtils.slab(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_SQUARE_STAIRS = createHolder(
            "orange_square_stairs",
            BlockDataUtils.stairs(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES)
        );
        PADDING_ORANGE_SQUARE_WALL = createHolder("orange_square_wall", BlockDataUtils.wall(PADDING_ORANGE_SQUARE, ORANGE_PADDING_PROPERTIES));

        PADDING_ORANGE_TILES = createHolder("orange_tiles", BlockData.simple(ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_TILES_SLAB = createHolder("orange_tiles_slab", BlockDataUtils.slab(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES));
        PADDING_ORANGE_TILES_STAIRS = createHolder(
            "orange_tiles_stairs",
            BlockDataUtils.stairs(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES)
        );
        PADDING_ORANGE_TILES_WALL = createHolder("orange_tiles_wall", BlockDataUtils.wall(PADDING_ORANGE_TILES, ORANGE_PADDING_PROPERTIES));

        PADDING_WHITE_PANEL = createHolder("white_panel", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_PIPES = createHolder("white_pipes", BlockData.simple(WHITE_PADDING_PROPERTIES));

        PADDING_WHITE_SQUARE = createHolder("white_square", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_SQUARE_SLAB = createHolder("white_square_slab", BlockDataUtils.slab(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_SQUARE_STAIRS = createHolder(
            "white_square_stairs",
            BlockDataUtils.stairs(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES)
        );
        PADDING_WHITE_SQUARE_WALL = createHolder("white_square_wall", BlockDataUtils.wall(PADDING_WHITE_SQUARE, WHITE_PADDING_PROPERTIES));

        PADDING_WHITE_TILES = createHolder("white_tiles", BlockData.simple(WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_SLAB = createHolder("white_tiles_slab", BlockDataUtils.slab(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_STAIRS = createHolder("white_tiles_stairs", BlockDataUtils.stairs(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
        PADDING_WHITE_TILES_WALL = createHolder("white_tiles_wall", BlockDataUtils.wall(PADDING_WHITE_TILES, WHITE_PADDING_PROPERTIES));
    }
}
