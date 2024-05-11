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

    public final PaddingBlockSet paddingOrange;

    public final PaddingBlockSet paddingWhite;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("padding_" + registryName, blockDataBuilder);
    }

    private AVPPaddingBlocks() {
        paddingOrange = createSet(Items.ORANGE_DYE, "orange", BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_WOOL));
        paddingWhite = createSet(Items.WHITE_DYE, "white", BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
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
