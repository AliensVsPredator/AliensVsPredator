package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPBlocks extends AVPDeferredBlockRegistry {

    public static final AVPBlocks INSTANCE = new AVPBlocks();

    public final Holder<Block> aluminumBlock;

    public final Holder<Block> orioniteBlock;

    public final Holder<Block> steelBlock;

    public final Holder<Block> titaniumBlock;

    private AVPBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK); // TODO:

        var stoneTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL));
        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
        var diamondTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        aluminumBlock = createHolder("aluminum_block", BlockModelData.cube(properties), stoneTier);
        orioniteBlock = createHolder("orionite_block", BlockModelData.cube(properties), diamondTier);
        steelBlock = createHolder("steel_block", BlockModelData.cube(properties), ironTier);
        titaniumBlock = createHolder("titanium_block", BlockModelData.cube(properties), ironTier);
    }
}
