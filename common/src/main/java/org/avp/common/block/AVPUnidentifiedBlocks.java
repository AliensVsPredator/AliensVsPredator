package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockTagData;
import org.avp.api.block.model.BlockModelData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

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

        dirt = createHolder("dirt", BlockModelData.cube(stoneProperties), stoneTags);
        gravel = createHolder("gravel", BlockModelData.cube(stoneProperties), stoneTags);
        rock = createHolder("rock", BlockModelData.cube(stoneProperties), stoneTags);
        sand = createHolder("sand", BlockModelData.cube(stoneProperties), stoneTags);
        stone = createHolder("stone", BlockModelData.cube(stoneProperties), stoneTags);
    }
}
