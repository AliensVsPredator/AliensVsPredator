package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPAlienBlocks extends AVPDeferredBlockRegistry {

    public static final AVPAlienBlocks INSTANCE = new AVPAlienBlocks();

    public final BlockBehaviour.Properties resinProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);

    public final Holder<Block> resin;

    public final Holder<Block> resinVeins;

    public final Holder<Block> resinWebbing;

    private AVPAlienBlocks() {
        var resinTagData = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        resin = createHolder(new BlockData("resin", BlockModelData.cube(resinProperties), resinTagData));
        resinVeins = createHolder(new BlockData("resin_veins", BlockModelData.cube(resinProperties), resinTagData));
        // FIXME: fix model data
        resinWebbing = createHolder(new BlockData("resin_webbing", BlockModelData.cube(resinProperties), resinTagData));
    }
}
