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

    public final Holder<Block> dirt;

    public final Holder<Block> gravel;

    public final Holder<Block> rock;

    public final Holder<Block> sand;

    public final Holder<Block> stone;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("unidentified_" + registryName, blockDataBuilder);
    }

    private AVPUnidentifiedBlocks() {
        var stoneProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY);

        var stoneTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(stoneProperties).blockTags(stoneTags);

        dirt = createHolder("dirt", pickProps.get());
        gravel = createHolder("gravel", pickProps.get());
        rock = createHolder("rock", pickProps.get());
        sand = createHolder("sand", pickProps.get());
        stone = createHolder("stone", pickProps.get());
    }
}
