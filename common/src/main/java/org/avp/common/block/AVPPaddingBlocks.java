package org.avp.common.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPPaddingBlocks extends AVPDeferredBlockRegistry {

    public static final AVPPaddingBlocks INSTANCE = new AVPPaddingBlocks();

    public final PaddingBlockSet paddingBlack;

    public final PaddingBlockSet paddingBlue;

    public final PaddingBlockSet paddingBrown;

    public final PaddingBlockSet paddingCyan;

    public final PaddingBlockSet paddingGray;

    public final PaddingBlockSet paddingGreen;

    public final PaddingBlockSet paddingLightBlue;

    public final PaddingBlockSet paddingLightGray;

    public final PaddingBlockSet paddingLime;

    public final PaddingBlockSet paddingMagenta;

    public final PaddingBlockSet paddingOrange;

    public final PaddingBlockSet paddingPink;

    public final PaddingBlockSet paddingPurple;

    public final PaddingBlockSet paddingRed;

    public final PaddingBlockSet paddingWhite;

    public final PaddingBlockSet paddingYellow;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("padding_" + registryName, blockDataBuilder);
    }

    private AVPPaddingBlocks() {
        paddingBlack = createSet(Items.BLACK_DYE, "black", BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL));
        paddingBlue = createSet(Items.BLUE_DYE, "blue", BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_WOOL));
        paddingBrown = createSet(Items.BROWN_DYE, "brown", BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_WOOL));
        paddingCyan = createSet(Items.CYAN_DYE, "cyan", BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_WOOL));
        paddingGray = createSet(Items.GRAY_DYE, "gray", BlockBehaviour.Properties.ofFullCopy(Blocks.GRAY_WOOL));
        paddingGreen = createSet(Items.GREEN_DYE, "green", BlockBehaviour.Properties.ofFullCopy(Blocks.GREEN_WOOL));
        paddingLightBlue = createSet(Items.LIGHT_BLUE_DYE, "light_blue", BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_BLUE_WOOL));
        paddingLightGray = createSet(Items.LIGHT_GRAY_DYE, "light_gray", BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_GRAY_WOOL));
        paddingLime = createSet(Items.LIME_DYE, "lime", BlockBehaviour.Properties.ofFullCopy(Blocks.LIME_WOOL));
        paddingMagenta = createSet(Items.MAGENTA_DYE, "magenta", BlockBehaviour.Properties.ofFullCopy(Blocks.MAGENTA_WOOL));
        paddingOrange = createSet(Items.ORANGE_DYE, "orange", BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_WOOL));
        paddingPink = createSet(Items.PINK_DYE, "pink", BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_WOOL));
        paddingPurple = createSet(Items.PURPLE_DYE, "purple", BlockBehaviour.Properties.ofFullCopy(Blocks.PURPLE_WOOL));
        paddingRed = createSet(Items.RED_DYE, "red", BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL));
        paddingWhite = createSet(Items.WHITE_DYE, "white", BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
        paddingYellow = createSet(Items.YELLOW_DYE, "yellow", BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_WOOL));
    }

    private PaddingBlockSet createSet(Item dyeItem, String prefix, BlockBehaviour.Properties properties) {
        var blockData = BlockData.simple(properties);

        var squareHolder = createHolder(prefix + "_square", blockData);
        var squareSlabData = BlockDataUtils.slab(squareHolder, properties);
        var squareStairData = BlockDataUtils.stairs(squareHolder, properties);
        var squareWallData = BlockDataUtils.wall(squareHolder, properties);

        var tilesHolder = createHolder(prefix + "_tiles", blockData);
        var tilesSlabData = BlockDataUtils.slab(tilesHolder, properties);
        var tilesStairData = BlockDataUtils.stairs(tilesHolder, properties);
        var tilesWallData = BlockDataUtils.wall(tilesHolder, properties);

        return new PaddingBlockSet(
            dyeItem,
            createHolder(prefix + "_panel", blockData),
            createHolder(prefix + "_pipes", blockData),
            squareHolder,
            createHolder(prefix + "_square_slab", squareSlabData),
            createHolder(prefix + "_square_stairs", squareStairData),
            createHolder(prefix + "_square_wall", squareWallData),
            tilesHolder,
            createHolder(prefix + "_tiles_slab", tilesSlabData),
            createHolder(prefix + "_tiles_stairs", tilesStairData),
            createHolder(prefix + "_tiles_wall", tilesWallData)
        );
    }

    public record PaddingBlockSet(
        Item dyeItem,

        Holder<Block> panel,
        Holder<Block> pipes,

        Holder<Block> square,
        Holder<Block> squareSlab,
        Holder<Block> squareStairs,
        Holder<Block> squareWall,

        Holder<Block> tiles,
        Holder<Block> tilesSlab,
        Holder<Block> tilesStairs,
        Holder<Block> tilesWall
    ) {}
}
