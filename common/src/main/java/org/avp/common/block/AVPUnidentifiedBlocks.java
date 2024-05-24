package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

import java.util.Set;

public class AVPUnidentifiedBlocks extends AVPDeferredBlockRegistry {

    public static final AVPUnidentifiedBlocks INSTANCE = new AVPUnidentifiedBlocks();

    public final Holder<Block> dirt;

    public final Holder<Block> gravel;

    public final Holder<Block> rock;

    public final Holder<Block> sand;

    public final Holder<Block> stone;

    @Override
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("unidentified_"));
    }

    private AVPUnidentifiedBlocks() {
        var stoneProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY);

        var stoneTags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        dirt = createHolder(new BlockData("dirt", BlockModelData.cube(stoneProperties), stoneTags));
        gravel = createHolder(new BlockData("gravel", BlockModelData.cube(stoneProperties), stoneTags));
        rock = createHolder(new BlockData("rock", BlockModelData.cube(stoneProperties), stoneTags));
        sand = createHolder(new BlockData("sand", BlockModelData.cube(stoneProperties), stoneTags));
        stone = createHolder(new BlockData("stone", BlockModelData.cube(stoneProperties), stoneTags));
    }
}
