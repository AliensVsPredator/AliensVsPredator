package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPBlocks extends AVPDeferredBlockRegistry {

    public static final AVPBlocks INSTANCE = new AVPBlocks();

    public final Holder<Block> aluminumBlock;

    public final Holder<Block> titaniumBlock;

    private AVPBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK); // TODO:

        var aluminum = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var titanium = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        aluminumBlock = createHolder("aluminum_block", BlockData.simple(properties).tags(aluminum));
        titaniumBlock = createHolder("titanium_block", BlockData.simple(properties).tags(titanium));
    }
}
