package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPAlienBlocks extends AVPDeferredBlockRegistry {

    public static final AVPAlienBlocks INSTANCE = new AVPAlienBlocks();

    public final BlockBehaviour.Properties resinProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);


    public final Holder<Block> resin;

    public final Holder<Block> resinVeins;

    public final Holder<Block> resinWebbing;

    private AVPAlienBlocks() {
        super();
        var resinTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE);
        var pickaxeProps = BlockData.simple(resinProperties).tags(resinTags);

        resin = createHolder("resin", pickaxeProps);
        resinVeins = createHolder("resin_veins", pickaxeProps);
        resinWebbing = createHolder("resin_webbing", pickaxeProps);
    }
}
