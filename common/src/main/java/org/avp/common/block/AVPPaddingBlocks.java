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

    public final Holder<Block> paddingOrangePanel;

    public final Holder<Block> paddingOrangePipes;

    public final Holder<Block> paddingOrangeSquare;

    public final Holder<Block> paddingOrangeSquareSlab;

    public final Holder<Block> paddingOrangeSquareStairs;

    public final Holder<Block> paddingOrangeSquareWall;

    public final Holder<Block> paddingOrangeTiles;

    public final Holder<Block> paddingOrangeTilesSlab;

    public final Holder<Block> paddingOrangeTilesStairs;

    public final Holder<Block> paddingOrangeTilesWall;

    public final Holder<Block> paddingWhitePanel;

    public final Holder<Block> paddingWhitePipes;

    public final Holder<Block> paddingWhiteSquare;

    public final Holder<Block> paddingWhiteSquareSlab;

    public final Holder<Block> paddingWhiteSquareStairs;

    public final Holder<Block> paddingWhiteSquareWall;

    public final Holder<Block> paddingWhiteTiles;

    public final Holder<Block> paddingWhiteTilesSlab;

    public final Holder<Block> paddingWhiteTilesStairs;

    public final Holder<Block> paddingWhiteTilesWall;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("padding_" + registryName, blockDataBuilder);
    }

    private AVPPaddingBlocks() {
        var orangePaddingProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_WOOL);
        var whitePaddingProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL);

        paddingOrangePanel = createHolder("orange_panel", BlockData.simple(orangePaddingProperties));
        paddingOrangePipes = createHolder("orange_pipes", BlockData.simple(orangePaddingProperties));

        paddingOrangeSquare = createHolder("orange_square", BlockData.simple(orangePaddingProperties));
        paddingOrangeSquareSlab = createHolder("orange_square_slab", BlockDataUtils.slab(paddingOrangeSquare, orangePaddingProperties));
        paddingOrangeSquareStairs = createHolder(
            "orange_square_stairs",
            BlockDataUtils.stairs(paddingOrangeSquare, orangePaddingProperties)
        );
        paddingOrangeSquareWall = createHolder("orange_square_wall", BlockDataUtils.wall(paddingOrangeSquare, orangePaddingProperties));

        paddingOrangeTiles = createHolder("orange_tiles", BlockData.simple(orangePaddingProperties));
        paddingOrangeTilesSlab = createHolder("orange_tiles_slab", BlockDataUtils.slab(paddingOrangeTiles, orangePaddingProperties));
        paddingOrangeTilesStairs = createHolder(
            "orange_tiles_stairs",
            BlockDataUtils.stairs(paddingOrangeTiles, orangePaddingProperties)
        );
        paddingOrangeTilesWall = createHolder("orange_tiles_wall", BlockDataUtils.wall(paddingOrangeTiles, orangePaddingProperties));

        paddingWhitePanel = createHolder("white_panel", BlockData.simple(whitePaddingProperties));
        paddingWhitePipes = createHolder("white_pipes", BlockData.simple(whitePaddingProperties));

        paddingWhiteSquare = createHolder("white_square", BlockData.simple(whitePaddingProperties));
        paddingWhiteSquareSlab = createHolder("white_square_slab", BlockDataUtils.slab(paddingWhiteSquare, whitePaddingProperties));
        paddingWhiteSquareStairs = createHolder(
            "white_square_stairs",
            BlockDataUtils.stairs(paddingWhiteSquare, whitePaddingProperties)
        );
        paddingWhiteSquareWall = createHolder("white_square_wall", BlockDataUtils.wall(paddingWhiteSquare, whitePaddingProperties));

        paddingWhiteTiles = createHolder("white_tiles", BlockData.simple(whitePaddingProperties));
        paddingWhiteTilesSlab = createHolder("white_tiles_slab", BlockDataUtils.slab(paddingWhiteTiles, whitePaddingProperties));
        paddingWhiteTilesStairs = createHolder("white_tiles_stairs", BlockDataUtils.stairs(paddingWhiteTiles, whitePaddingProperties));
        paddingWhiteTilesWall = createHolder("white_tiles_wall", BlockDataUtils.wall(paddingWhiteTiles, whitePaddingProperties));
    }
}
