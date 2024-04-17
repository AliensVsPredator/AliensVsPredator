package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPUnidentifiedBlocks extends AVPDeferredBlockRegistry {

    public static final AVPUnidentifiedBlocks INSTANCE = new AVPUnidentifiedBlocks();

    public final BlockBehaviour.Properties STONE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .mapColor(MapColor.COLOR_GRAY);

    public final Holder<Block> DIRT;

    public final Holder<Block> GRAVEL;

    public final Holder<Block> ROCK;

    public final Holder<Block> SAND;

    public final Holder<Block> STONE;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("unidentified_" + registryName, blockDataBuilder);
    }

    private AVPUnidentifiedBlocks() {
        var stone = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(STONE_PROPERTIES).tags(stone);

        DIRT = createHolder("dirt", pickProps.get());
        GRAVEL = createHolder("gravel", pickProps.get());
        ROCK = createHolder("rock", pickProps.get());
        SAND = createHolder("sand", pickProps.get());
        STONE = createHolder("stone", pickProps.get());
    }
}
