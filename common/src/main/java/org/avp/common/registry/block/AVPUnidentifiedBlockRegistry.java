package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPUnidentifiedBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPUnidentifiedBlockRegistry INSTANCE = new AVPUnidentifiedBlockRegistry();

    public final BLHolder<Block> dirt;

    public final BLHolder<Block> gravel;

    public final BLHolder<Block> rock;

    public final BLHolder<Block> sand;

    public final BLHolder<Block> stone;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("unidentified_"));
    }

    private AVPUnidentifiedBlockRegistry() {
        var stoneProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY);

        var stoneTags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        dirt = createHolder("dirt", BlockModelData.cube(stoneProperties), stoneTags);
        gravel = createHolder("gravel", BlockModelData.cube(stoneProperties), stoneTags);
        rock = createHolder("rock", BlockModelData.cube(stoneProperties), stoneTags);
        sand = createHolder("sand", BlockModelData.cube(stoneProperties), stoneTags);
        stone = createHolder("stone", BlockModelData.cube(stoneProperties), stoneTags);
    }
}
